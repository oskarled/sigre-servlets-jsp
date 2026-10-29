package com.ecm.sigre.dao;

import com.ecm.sigre.modelo.Categoria;
import com.ecm.sigre.modelo.Curso;
import java.util.List;
import java.util.Optional;

/** Operaciones de persistencia de los cursos TSA. */
public interface CursoDao {

    List<Curso> listar(String filtroNombre);

    Optional<Curso> buscarPorId(int idCurso);

    boolean existeCodigo(String codigoCurso, int idCursoExcluido);

    int insertar(Curso curso);

    void actualizar(Curso curso);

    /** @return false si el curso tiene eventos asociados y no se puede borrar. */
    boolean eliminar(int idCurso);

    List<Categoria> listarCategorias();
}
