package com.ecm.sigre.dao;

import com.ecm.sigre.modelo.Evento;
import java.util.List;
import java.util.Optional;

/** Operaciones de persistencia de las programaciones de evento. */
public interface EventoDao {

    List<Evento> listar(String estado, Integer idCurso);

    Optional<Evento> buscarPorId(int idEvento);

    boolean existeCodigo(String codigoEvento);

    int insertar(Evento evento);
}
