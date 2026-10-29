package com.ecm.sigre.dao;

import com.ecm.sigre.modelo.Usuario;
import com.ecm.sigre.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Implementación JDBC de {@link UsuarioDao}. */
public class UsuarioDaoJdbc implements UsuarioDao {

    private static final String SQL_BASE =
            "SELECT u.id_usuario, u.numero_documento, u.nombres, u.apellidos, u.correo_electronico, "
            + "r.nombre_rol, (SELECT a.estado_vigencia FROM afiliaciones_arl a WHERE a.id_usuario = u.id_usuario "
            + "ORDER BY a.id_afiliacion DESC LIMIT 1) AS estado_arl "
            + "FROM usuarios u JOIN roles r ON r.id_rol = u.id_rol ";

    @Override
    public Optional<Usuario> buscarPorDocumento(String numeroDocumento) {
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_BASE + "WHERE u.numero_documento = ?")) {
            ps.setString(1, numeroDocumento);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatosException("Error al buscar el usuario", e);
        }
    }

    @Override
    public List<Usuario> listarPorRol(String nombreRol) {
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(
                     SQL_BASE + "WHERE r.nombre_rol = ? AND u.estado_usuario = 'ACTIVO' ORDER BY u.nombres")) {
            ps.setString(1, nombreRol);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatosException("Error al listar usuarios por rol", e);
        }
        return usuarios;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setNumeroDocumento(rs.getString("numero_documento"));
        usuario.setNombres(rs.getString("nombres"));
        usuario.setApellidos(rs.getString("apellidos"));
        usuario.setCorreoElectronico(rs.getString("correo_electronico"));
        usuario.setNombreRol(rs.getString("nombre_rol"));
        usuario.setEstadoArl(rs.getString("estado_arl"));
        return usuario;
    }
}
