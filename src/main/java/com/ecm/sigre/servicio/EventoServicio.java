package com.ecm.sigre.servicio;

import com.ecm.sigre.dao.EventoDao;
import com.ecm.sigre.modelo.Evento;
import com.ecm.sigre.util.Validador;
import java.time.LocalDateTime;
import java.util.Map;

/** Reglas de negocio para programar eventos en las pistas de entrenamiento. */
public class EventoServicio {

    /** La Resolución 4272 de 2021 y la capacidad de las pistas limitan el aforo a 30 personas. */
    public static final int AFORO_MAXIMO = 30;

    private final EventoDao eventoDao;

    public EventoServicio(EventoDao eventoDao) {
        this.eventoDao = eventoDao;
    }

    public int programar(Map<String, String> datos, LocalDateTime ahora) {
        String codigo = valor(datos, "codigoEvento").toUpperCase();
        Validador validador = new Validador()
                .requerido(valor(datos, "idCurso"), "curso")
                .requerido(valor(datos, "idInstructor"), "instructor")
                .requerido(codigo, "código del evento")
                .longitudMaxima(codigo, 30, "código del evento")
                .requerido(valor(datos, "pistaEntrenamiento"), "pista de entrenamiento")
                .requerido(valor(datos, "fechaHoraInicio"), "fecha de inicio")
                .requerido(valor(datos, "fechaHoraFin"), "fecha de fin")
                .rangoFechas(valor(datos, "fechaHoraInicio"), valor(datos, "fechaHoraFin"))
                .requerido(valor(datos, "cupoMaximo"), "cupo máximo")
                .enteroEnRango(valor(datos, "cupoMaximo"), 1, AFORO_MAXIMO, "cupo máximo");
        LocalDateTime inicio = Validador.aFecha(valor(datos, "fechaHoraInicio"));
        if (inicio != null && inicio.isBefore(ahora)) {
            validador.getErrores().add("No se pueden programar eventos en una fecha pasada.");
        }
        if (!validador.esValido()) {
            throw new ReglaNegocioException(validador.getErrores());
        }
        if (eventoDao.existeCodigo(codigo)) {
            throw new ReglaNegocioException("Ya existe un evento con el código " + codigo + ".");
        }

        Evento evento = new Evento();
        evento.setIdCurso(Validador.aEntero(valor(datos, "idCurso")));
        evento.setIdInstructor(Validador.aEntero(valor(datos, "idInstructor")));
        evento.setCodigoEvento(codigo);
        evento.setPistaEntrenamiento(valor(datos, "pistaEntrenamiento"));
        evento.setFechaHoraInicio(inicio);
        evento.setFechaHoraFin(Validador.aFecha(valor(datos, "fechaHoraFin")));
        evento.setCupoMaximo(Validador.aEntero(valor(datos, "cupoMaximo")));
        return eventoDao.insertar(evento);
    }

    private static String valor(Map<String, String> datos, String clave) {
        String valor = datos.get(clave);
        return valor == null ? "" : valor.trim();
    }
}
