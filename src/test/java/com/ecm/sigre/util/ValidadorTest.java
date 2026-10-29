package com.ecm.sigre.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ValidadorTest {

    @Test
    void aceptaCodigoDeCursoConFormatoCorrecto() {
        assertTrue(new Validador().codigoCurso("TSA-ADV-01").esValido());
    }

    @Test
    void rechazaCodigoDeCursoConFormatoIncorrecto() {
        Validador validador = new Validador().codigoCurso("tsa01");
        assertFalse(validador.esValido());
        assertEquals(1, validador.getErrores().size());
    }

    @Test
    void exigeCamposObligatorios() {
        Validador validador = new Validador().requerido("", "nombre").requerido(null, "código");
        assertEquals(2, validador.getErrores().size());
    }

    @Test
    void validaRangoDeEnteros() {
        assertTrue(new Validador().enteroEnRango("15", 1, 30, "cupo").esValido());
        assertFalse(new Validador().enteroEnRango("31", 1, 30, "cupo").esValido());
        assertFalse(new Validador().enteroEnRango("abc", 1, 30, "cupo").esValido());
    }

    @Test
    void rechazaFechaFinAnteriorAlInicio() {
        assertFalse(new Validador().rangoFechas("2026-11-10T08:00", "2026-11-09T17:00").esValido());
        assertTrue(new Validador().rangoFechas("2026-11-10T08:00", "2026-11-12T17:00").esValido());
    }

    @Test
    void validaDocumentoNumerico() {
        assertTrue(new Validador().documento("1047500123").esValido());
        assertFalse(new Validador().documento("12AB").esValido());
    }

    @Test
    void conversionesDevuelvenNullConDatosInvalidos() {
        assertNull(Validador.aEntero("x"));
        assertNull(Validador.aFecha("10/11/2026"));
        assertEquals(8, Validador.aEntero(" 8 "));
    }
}
