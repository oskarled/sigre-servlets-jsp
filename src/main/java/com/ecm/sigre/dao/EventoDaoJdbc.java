package com.ecm.sigre.dao;

import com.ecm.sigre.modelo.Evento;
import com.ecm.sigre.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Implementación JDBC de {@link EventoDao} sobre la tabla programaciones_evento. */
public class EventoDaoJdbc implements EventoDao {

    private static final String SQL_BASE =
            "SELECT e.*, c.nombre_curso, CONCAT(u.nombres, ' ', u.apellidos) AS nombre_instructor "
            + "FROM programaciones_evento e "
            + "JOIN cursos_tsa c ON c.id_curso = e.id_curso "
            + "JOIN usuarios u ON u.id_usuario = e.id_instructor ";
    private static final String SQL_INSERTAR =
            "INSERT INTO programaciones_evento (id_curso, id_instructor, codigo_evento, pista_entrenamiento, "
            + "fecha_hora_inicio, fecha_hora_fin, cupo_maximo, cupo_disponible, estado_evento) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'PROGRAMADO')";

    @Override
    public List<Evento> listar(String estado, Integer idCurso) {
        StringBuilder sql = new StringBuilder(SQL_BASE).append("WHERE 1 = 1 ");
        List<Object> parametros = new ArrayList<>();
        if (estado != null && !estado.isBlank()) {
            sql.append("AND e.estado_evento = ? ");
            parametros.add(estado);
        }
        if (idCurso != null) {
            sql.append("AND e.id_curso = ? ");
            parametros.add(idCurso);
        }
        sql.append("ORDER BY e.fecha_hora_inicio");

        List<Evento> eventos = new ArrayList<>();
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    eventos.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatosException("Error al listar los eventos", e);
        }
        return eventos;
    }

    @Override
    public Optional<Evento> buscarPorId(int idEvento) {
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_BASE + "WHERE e.id_evento = ?")) {
            ps.setInt(1, idEvento);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatosException("Error al consultar el evento " + idEvento, e);
        }
    }

    @Override
    public boolean existeCodigo(String codigoEvento) {
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT COUNT(*) FROM programaciones_evento WHERE codigo_evento = ?")) {
            ps.setString(1, codigoEvento);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DatosException("Error al validar el código del evento", e);
        }
    }

    @Override
    public int insertar(Evento evento) {
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, evento.getIdCurso());
            ps.setInt(2, evento.getIdInstructor());
            ps.setString(3, evento.getCodigoEvento());
            ps.setString(4, evento.getPistaEntrenamiento());
            ps.setTimestamp(5, Timestamp.valueOf(evento.getFechaHoraInicio()));
            ps.setTimestamp(6, Timestamp.valueOf(evento.getFechaHoraFin()));
            ps.setInt(7, evento.getCupoMaximo());
            ps.setInt(8, evento.getCupoMaximo());
            ps.executeUpdate();
            try (ResultSet llaves = ps.getGeneratedKeys()) {
                return llaves.next() ? llaves.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DatosException("Error al registrar el evento", e);
        }
    }

    private Evento mapear(ResultSet rs) throws SQLException {
        Evento evento = new Evento();
        evento.setIdEvento(rs.getInt("id_evento"));
        evento.setIdCurso(rs.getInt("id_curso"));
        evento.setNombreCurso(rs.getString("nombre_curso"));
        evento.setIdInstructor(rs.getInt("id_instructor"));
        evento.setNombreInstructor(rs.getString("nombre_instructor"));
        evento.setCodigoEvento(rs.getString("codigo_evento"));
        evento.setPistaEntrenamiento(rs.getString("pista_entrenamiento"));
        evento.setFechaHoraInicio(rs.getTimestamp("fecha_hora_inicio").toLocalDateTime());
        evento.setFechaHoraFin(rs.getTimestamp("fecha_hora_fin").toLocalDateTime());
        evento.setCupoMaximo(rs.getInt("cupo_maximo"));
        evento.setCupoDisponible(rs.getInt("cupo_disponible"));
        evento.setEstadoEvento(rs.getString("estado_evento"));
        return evento;
    }
}
