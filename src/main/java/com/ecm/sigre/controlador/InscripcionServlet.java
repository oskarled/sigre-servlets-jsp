package com.ecm.sigre.controlador;

import com.ecm.sigre.dao.EventoDao;
import com.ecm.sigre.dao.EventoDaoJdbc;
import com.ecm.sigre.dao.InscripcionDao;
import com.ecm.sigre.dao.InscripcionDaoJdbc;
import com.ecm.sigre.dao.UsuarioDaoJdbc;
import com.ecm.sigre.modelo.Evento;
import com.ecm.sigre.modelo.Inscripcion;
import com.ecm.sigre.servicio.InscripcionServicio;
import com.ecm.sigre.servicio.ReglaNegocioException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controlador de inscripciones a un evento.
 * <ul>
 *   <li>GET  /inscripciones?idEvento=N - detalle del evento, inscritos y formulario</li>
 *   <li>POST /inscripciones            - inscribe al asistente por número de documento</li>
 * </ul>
 */
@WebServlet(name = "InscripcionServlet", urlPatterns = "/inscripciones")
public class InscripcionServlet extends ServletBase {

    private static final long serialVersionUID = 1L;

    private final transient EventoDao eventoDao = new EventoDaoJdbc();
    private final transient InscripcionDao inscripcionDao = new InscripcionDaoJdbc();
    private final transient InscripcionServicio inscripcionServicio =
            new InscripcionServicio(eventoDao, new UsuarioDaoJdbc(), inscripcionDao);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configurarCodificacion(request, response);
        moverMensajeFlash(request);
        if (!cargarEvento(request, enteroObligatorio(request, "idEvento"))) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "El evento no existe");
            return;
        }
        mostrarVista(request, response, "inscripciones/formulario.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configurarCodificacion(request, response);
        int idEvento = enteroObligatorio(request, "idEvento");
        String documento = request.getParameter("numeroDocumento");
        try {
            Inscripcion inscripcion = inscripcionServicio.inscribir(idEvento, documento);
            redirigirConMensaje(request, response, "/inscripciones?idEvento=" + idEvento,
                    "Inscripción registrada para " + inscripcion.getNombreAsistente() + ". Código de acceso: " + inscripcion.getCodigoQr());
        } catch (ReglaNegocioException e) {
            mostrarErrores(request, e.getErrores());
            request.setAttribute("documentoDigitado", documento);
            if (!cargarEvento(request, idEvento)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "El evento no existe");
                return;
            }
            mostrarVista(request, response, "inscripciones/formulario.jsp");
        }
    }

    private boolean cargarEvento(HttpServletRequest request, int idEvento) {
        Evento evento = eventoDao.buscarPorId(idEvento).orElse(null);
        if (evento == null) {
            return false;
        }
        request.setAttribute("evento", evento);
        request.setAttribute("inscripciones", inscripcionDao.listarPorEvento(idEvento));
        return true;
    }
}
