package com.ecm.sigre.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validaciones de los datos que llegan desde los formularios HTML.
 * Cada método agrega a la lista de errores un mensaje claro para el usuario.
 */
public final class Validador {

    private static final Pattern CODIGO_CURSO = Pattern.compile("^[A-Z]{3}-[A-Z]{3}-\\d{2}$");
    private static final Pattern DOCUMENTO = Pattern.compile("^\\d{6,20}$");

    private final List<String> errores = new ArrayList<>();

    public Validador requerido(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            errores.add("El campo " + campo + " es obligatorio.");
        }
        return this;
    }

    public Validador longitudMaxima(String valor, int maximo, String campo) {
        if (valor != null && valor.length() > maximo) {
            errores.add("El campo " + campo + " admite máximo " + maximo + " caracteres.");
        }
        return this;
    }

    public Validador codigoCurso(String valor) {
        if (valor != null && !valor.isBlank() && !CODIGO_CURSO.matcher(valor).matches()) {
            errores.add("El código del curso debe tener el formato AAA-AAA-00 (ejemplo: TSA-ADV-01).");
        }
        return this;
    }

    public Validador documento(String valor) {
        if (valor != null && !valor.isBlank() && !DOCUMENTO.matcher(valor).matches()) {
            errores.add("El número de documento debe tener entre 6 y 20 dígitos.");
        }
        return this;
    }

    public Validador enteroEnRango(String valor, int minimo, int maximo, String campo) {
        Integer numero = aEntero(valor);
        if (valor != null && !valor.isBlank() && (numero == null || numero < minimo || numero > maximo)) {
            errores.add("El campo " + campo + " debe ser un número entre " + minimo + " y " + maximo + ".");
        }
        return this;
    }

    public Validador rangoFechas(String inicio, String fin) {
        LocalDateTime fechaInicio = aFecha(inicio);
        LocalDateTime fechaFin = aFecha(fin);
        if (inicio != null && !inicio.isBlank() && fechaInicio == null) {
            errores.add("La fecha de inicio no tiene un formato válido.");
        }
        if (fin != null && !fin.isBlank() && fechaFin == null) {
            errores.add("La fecha de fin no tiene un formato válido.");
        }
        if (fechaInicio != null && fechaFin != null && !fechaFin.isAfter(fechaInicio)) {
            errores.add("La fecha de fin debe ser posterior a la fecha de inicio.");
        }
        return this;
    }

    public boolean esValido() {
        return errores.isEmpty();
    }

    public List<String> getErrores() {
        return errores;
    }

    /** Convierte un texto a entero; devuelve null si no es un número. */
    public static Integer aEntero(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Convierte el valor de un campo datetime-local (yyyy-MM-ddTHH:mm) a fecha. */
    public static LocalDateTime aFecha(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(valor.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
