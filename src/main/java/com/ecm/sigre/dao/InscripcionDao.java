package com.ecm.sigre.dao;

import com.ecm.sigre.modelo.Inscripcion;
import java.util.List;

/** Operaciones de persistencia de las matrículas. */
public interface InscripcionDao {

    boolean existe(int idUsuario, int idEvento);

    /**
     * Registra la matrícula. El trigger trg_actualizar_aforo_ins de la base de datos
     * descuenta el cupo disponible del evento.
     */
    int insertar(Inscripcion inscripcion);

    List<Inscripcion> listarPorEvento(int idEvento);
}
