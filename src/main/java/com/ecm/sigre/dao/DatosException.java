package com.ecm.sigre.dao;

/** Error de acceso a datos (envuelve las SQLException de JDBC). */
public class DatosException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
