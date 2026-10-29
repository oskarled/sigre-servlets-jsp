package com.ecm.sigre.dao;

import com.ecm.sigre.modelo.Categoria;
import com.ecm.sigre.modelo.Curso;
import com.ecm.sigre.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Implementación JDBC de {@link CursoDao} sobre la tabla cursos_tsa. */
public class CursoDaoJdbc implements CursoDao {

    private static final String SQL_LISTAR =
            "SELECT c.*, cat.nombre_categoria FROM cursos_tsa c "
            + "JOIN categorias_curso cat ON cat.id_categoria = c.id_categoria "
            + "WHERE c.nombre_curso LIKE ? OR c.codigo_curso LIKE ? ORDER BY c.codigo_curso";
    private static final String SQL_POR_ID =
            "SELECT c.*, cat.nombre_categoria FROM cursos_tsa c "
            + "JOIN categorias_curso cat ON cat.id_categoria = c.id_categoria WHERE c.id_curso = ?";
    private static final String SQL_EXISTE_CODIGO =
            "SELECT COUNT(*) FROM cursos_tsa WHERE codigo_curso = ? AND id_curso <> ?";
    private static final String SQL_INSERTAR =
            "INSERT INTO cursos_tsa (id_categoria, codigo_curso, nombre_curso, intensidad_horaria_hs, "
            + "vigencia_meses, requisito_nivel_riesgo) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_ACTUALIZAR =
            "UPDATE cursos_tsa SET id_categoria = ?, codigo_curso = ?, nombre_curso = ?, "
            + "intensidad_horaria_hs = ?, vigencia_meses = ?, requisito_nivel_riesgo = ? WHERE id_curso = ?";
    private static final String SQL_TIENE_EVENTOS =
            "SELECT COUNT(*) FROM programaciones_evento WHERE id_curso = ?";
    private static final String SQL_ELIMINAR = "DELETE FROM cursos_tsa WHERE id_curso = ?";
    private static final String SQL_CATEGORIAS =
            "SELECT id_categoria, nombre_categoria FROM categorias_curso ORDER BY nombre_categoria";

    @Override
    public List<Curso> listar(String filtroNombre) {
        String patron = "%" + (filtroNombre == null ? "" : filtroNombre.trim()) + "%";
        List<Curso> cursos = new ArrayList<>();
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_LISTAR)) {
            ps.setString(1, patron);
            ps.setString(2, patron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cursos.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatosException("Error al listar los cursos", e);
        }
        return cursos;
    }

    @Override
    public Optional<Curso> buscarPorId(int idCurso) {
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_POR_ID)) {
            ps.setInt(1, idCurso);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatosException("Error al consultar el curso " + idCurso, e);
        }
    }

    @Override
    public boolean existeCodigo(String codigoCurso, int idCursoExcluido) {
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_EXISTE_CODIGO)) {
            ps.setString(1, codigoCurso);
            ps.setInt(2, idCursoExcluido);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DatosException("Error al validar el código del curso", e);
        }
    }

    @Override
    public int insertar(Curso curso) {
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            asignarParametros(ps, curso);
            ps.executeUpdate();
            try (ResultSet llaves = ps.getGeneratedKeys()) {
                return llaves.next() ? llaves.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DatosException("Error al registrar el curso", e);
        }
    }

    @Override
    public void actualizar(Curso curso) {
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_ACTUALIZAR)) {
            asignarParametros(ps, curso);
            ps.setInt(7, curso.getIdCurso());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatosException("Error al actualizar el curso", e);
        }
    }

    @Override
    public boolean eliminar(int idCurso) {
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement consulta = cn.prepareStatement(SQL_TIENE_EVENTOS);
             PreparedStatement borrado = cn.prepareStatement(SQL_ELIMINAR)) {
            consulta.setInt(1, idCurso);
            try (ResultSet rs = consulta.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    return false;
                }
            }
            borrado.setInt(1, idCurso);
            return borrado.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatosException("Error al eliminar el curso", e);
        }
    }

    @Override
    public List<Categoria> listarCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        try (Connection cn = ConexionBD.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_CATEGORIAS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categorias.add(new Categoria(rs.getInt("id_categoria"), rs.getString("nombre_categoria")));
            }
        } catch (SQLException e) {
            throw new DatosException("Error al listar las categorías", e);
        }
        return categorias;
    }

    private void asignarParametros(PreparedStatement ps, Curso curso) throws SQLException {
        ps.setInt(1, curso.getIdCategoria());
        ps.setString(2, curso.getCodigoCurso());
        ps.setString(3, curso.getNombreCurso());
        ps.setInt(4, curso.getIntensidadHoraria());
        ps.setInt(5, curso.getVigenciaMeses());
        ps.setString(6, curso.getRequisitoNivelRiesgo());
    }

    private Curso mapear(ResultSet rs) throws SQLException {
        Curso curso = new Curso();
        curso.setIdCurso(rs.getInt("id_curso"));
        curso.setIdCategoria(rs.getInt("id_categoria"));
        curso.setNombreCategoria(rs.getString("nombre_categoria"));
        curso.setCodigoCurso(rs.getString("codigo_curso"));
        curso.setNombreCurso(rs.getString("nombre_curso"));
        curso.setIntensidadHoraria(rs.getInt("intensidad_horaria_hs"));
        curso.setVigenciaMeses(rs.getInt("vigencia_meses"));
        curso.setRequisitoNivelRiesgo(rs.getString("requisito_nivel_riesgo"));
        return curso;
    }
}
