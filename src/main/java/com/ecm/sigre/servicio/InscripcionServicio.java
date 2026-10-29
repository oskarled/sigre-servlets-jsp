package com.ecm.sigre.servicio;

import com.ecm.sigre.dao.EventoDao;
import com.ecm.sigre.dao.InscripcionDao;
import com.ecm.sigre.dao.UsuarioDao;
import com.ecm.sigre.modelo.Evento;
import com.ecm.sigre.modelo.Inscripcion;
import com.ecm.sigre.modelo.Usuario;
import com.ecm.sigre.util.Validador;

/**
 * Inscribe asistentes en un evento aplicando las reglas de SIGRE:
 * el asistente debe existir, tener ARL vigente y no estar inscrito; el evento debe estar
 * programado y tener cupos disponibles.
 */
public class InscripcionServicio {

    private final EventoDao eventoDao;
    private final UsuarioDao usuarioDao;
    private final InscripcionDao inscripcionDao;

    public InscripcionServicio(EventoDao eventoDao, UsuarioDao usuarioDao, InscripcionDao inscripcionDao) {
        this.eventoDao = eventoDao;
        this.usuarioDao = usuarioDao;
        this.inscripcionDao = inscripcionDao;
    }

    public Inscripcion inscribir(int idEvento, String numeroDocumento) {
        String documento = numeroDocumento == null ? "" : numeroDocumento.trim();
        Validador validador = new Validador().requerido(documento, "número de documento").documento(documento);
        if (!validador.esValido()) {
            throw new ReglaNegocioException(validador.getErrores());
        }

        Evento evento = eventoDao.buscarPorId(idEvento)
                .orElseThrow(() -> new ReglaNegocioException("El evento seleccionado no existe."));
        if (!Evento.PROGRAMADO.equals(evento.getEstadoEvento())) {
            throw new ReglaNegocioException("El evento " + evento.getCodigoEvento() + " no está abierto a inscripciones.");
        }
        if (evento.getCupoDisponible() <= 0) {
            throw new ReglaNegocioException("El evento " + evento.getCodigoEvento() + " no tiene cupos disponibles.");
        }

        Usuario usuario = usuarioDao.buscarPorDocumento(documento)
                .orElseThrow(() -> new ReglaNegocioException("No hay un usuario registrado con el documento " + documento + "."));
        if (!usuario.tieneArlVigente()) {
            throw new ReglaNegocioException(usuario.getNombreCompleto()
                    + " no tiene afiliación ARL vigente (riesgo IV o V); no puede inscribirse.");
        }
        if (inscripcionDao.existe(usuario.getIdUsuario(), idEvento)) {
            throw new ReglaNegocioException(usuario.getNombreCompleto() + " ya está inscrito en este evento.");
        }

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setIdUsuario(usuario.getIdUsuario());
        inscripcion.setIdEvento(idEvento);
        inscripcion.setNombreAsistente(usuario.getNombreCompleto());
        inscripcion.setNumeroDocumento(documento);
        inscripcion.setCodigoQr(generarCodigoQr(evento, usuario));

        int idMatricula = inscripcionDao.insertar(inscripcion);
        if (idMatricula == 0) {
            throw new ReglaNegocioException("El evento se quedó sin cupos mientras se procesaba la inscripción.");
        }
        inscripcion.setIdMatricula(idMatricula);
        return inscripcion;
    }

    /** Código único que luego se imprime como QR para el control de acceso a la pista. */
    static String generarCodigoQr(Evento evento, Usuario usuario) {
        return "QR-SIGRE-" + evento.getCodigoEvento() + "-USR" + String.format("%04d", usuario.getIdUsuario());
    }
}
