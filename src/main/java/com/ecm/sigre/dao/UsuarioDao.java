package com.ecm.sigre.dao;

import com.ecm.sigre.modelo.Usuario;
import java.util.List;
import java.util.Optional;

/** Consultas de usuarios necesarias para el módulo de eventos e inscripciones. */
public interface UsuarioDao {

    /** Busca un usuario por documento e incluye el estado de su última afiliación ARL. */
    Optional<Usuario> buscarPorDocumento(String numeroDocumento);

    List<Usuario> listarPorRol(String nombreRol);
}
