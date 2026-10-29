package com.ecm.sigre.servicio;

import java.util.List;

/** Se lanza cuando los datos no cumplen las reglas del negocio; lleva los mensajes para el usuario. */
public class ReglaNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final transient List<String> errores;

    public ReglaNegocioException(List<String> errores) {
        super(String.join(" ", errores));
        this.errores = List.copyOf(errores);
    }

    public ReglaNegocioException(String error) {
        this(List.of(error));
    }

    public List<String> getErrores() {
        return errores;
    }
}
