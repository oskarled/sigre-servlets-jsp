package com.ecm.sigre.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ecm.sigre.dao.EventoDao;
import com.ecm.sigre.modelo.Evento;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventoServicioTest {

    private static final LocalDateTime HOY = LocalDateTime.of(2026, 10, 29, 9, 0);
    private Evento guardado;
    private EventoServicio servicio;

    @BeforeEach
    void preparar() {
        guardado = null;
        servicio = new EventoServicio(new EventoDao() {
            public List<Evento> listar(String estado, Integer idCurso) { return List.of(); }
            public Optional<Evento> buscarPorId(int idEvento) { return Optional.empty(); }
            public boolean existeCodigo(String codigo) { return "EVT-REPETIDO".equals(codigo); }
            public int insertar(Evento evento) { guardado = evento; return 7; }
        });
    }

    @Test
    void programaEventoValido() {
        assertEquals(7, servicio.programar(datos("EVT-202611-01", "15", "2026-11-10T08:00"), HOY));
        assertEquals(15, guardado.getCupoMaximo());
    }

    @Test
    void rechazaCupoMayorAlAforoPermitido() {
        ReglaNegocioException error = assertThrows(ReglaNegocioException.class,
                () -> servicio.programar(datos("EVT-202611-02", "40", "2026-11-10T08:00"), HOY));
        assertTrue(error.getMessage().contains("cupo"));
    }

    @Test
    void rechazaFechaPasadaYCodigoRepetido() {
        assertThrows(ReglaNegocioException.class,
                () -> servicio.programar(datos("EVT-202611-03", "10", "2026-10-01T08:00"), HOY));
        assertThrows(ReglaNegocioException.class,
                () -> servicio.programar(datos("EVT-REPETIDO", "10", "2026-11-10T08:00"), HOY));
    }

    private static Map<String, String> datos(String codigo, String cupo, String inicio) {
        Map<String, String> datos = new HashMap<>();
        datos.put("idCurso", "1");
        datos.put("idInstructor", "2");
        datos.put("codigoEvento", codigo);
        datos.put("pistaEntrenamiento", "Pista 1 - Torre de Entrenamiento Mamonal");
        datos.put("fechaHoraInicio", inicio);
        datos.put("fechaHoraFin", LocalDateTime.parse(inicio).plusDays(3).toString());
        datos.put("cupoMaximo", cupo);
        return datos;
    }
}
