package com.ecm.sigre.dao;

import com.ecm.sigre.modelo.Inscripcion;
import com.ecm.sigre.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Implementación JDBC de {@link InscripcionDao} sobre la tabla matriculas_inscripcion. */
public class InscripcionDaoJdbc implements InscripcionDao {

    private static final String SQL_INSERTAR =
            "INSERT INTO matriculas_inscripcion (id_usuario, id_evento, codigo_qr) VALUES (?, ?, ?)";
    private static final String SQL_POR_EVENTO =
            "SELECT m.*, u.numero_documento, CONCAT(u.nombres, ' ', u.apellidos) AS nombre_asistente "
            + "FROM matriculas_inscripcion m JOIN usuarios u ON u.id_usuario = m.id_usuario "
            + "WHERE m.id_evento = ? ORDER BY m.fecha_inscripcion";

    @Override
    public boolean existe(int idUsuario, int idEvento) {
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT COUNT(*) FROM matriculas_inscripcion WHERE id_usuario = ? AND id_evento = ?")) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idEvento);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DatosException("Error al validar la inscripción", e);
        }
    }

    @Override
    public int insertar(Inscripcion inscripcion) {
        try (Connection cn = ConexionBD.obtenerConexion()) {
            cn.setAutoCommit(false);
            try (PreparedStatement bloqueo = cn.prepareStatement(
                         "SELECT cupo_disponible FROM programaciones_evento WHERE id_evento = ? FOR UPDATE");
                 PreparedStatement ps = cn.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
                bloqueo.setInt(1, inscripcion.getIdEvento());
                try (ResultSet rs = bloqueo.executeQuery()) {
                    if (!rs.next() || rs.getInt(1) <= 0) {
                        cn.rollback();
                        return 0;
                    }
                }
                ps.setInt(1, inscripcion.getIdUsuario());
                ps.setInt(2, inscripcion.getIdEvento());
                ps.setString(3, inscripcion.getCodigoQr());
                ps.executeUpdate();
                int id;
                try (ResultSet llaves = ps.getGeneratedKeys()) {
                    id = llaves.next() ? llaves.getInt(1) : 0;
                }
                cn.commit();
                return id;
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DatosException("Error al registrar la inscripción", e);
        }
    }

    @Override
    public List<Inscripcion> listarPorEvento(int idEvento) {
        List<Inscripcion> inscripciones = new ArrayList<>();
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_POR_EVENTO)) {
            ps.setInt(1, idEvento);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Inscripcion inscripcion = new Inscripcion();
                    inscripcion.setIdMatricula(rs.getInt("id_matricula"));
                    inscripcion.setIdUsuario(rs.getInt("id_usuario"));
                    inscripcion.setIdEvento(rs.getInt("id_evento"));
                    inscripcion.setNumeroDocumento(rs.getString("numero_documento"));
                    inscripcion.setNombreAsistente(rs.getString("nombre_asistente"));
                    inscripcion.setCodigoQr(rs.getString("codigo_qr"));
                    inscripcion.setFechaInscripcion(rs.getTimestamp("fecha_inscripcion").toLocalDateTime());
                    inscripcion.setEstadoMatricula(rs.getString("estado_matricula"));
                    inscripciones.add(inscripcion);
                }
            }
        } catch (SQLException e) {
            throw new DatosException("Error al listar las inscripciones", e);
        }
        return inscripciones;
    }
}
