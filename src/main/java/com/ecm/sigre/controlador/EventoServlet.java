package com.ecm.sigre.controlador;

import com.ecm.sigre.dao.CursoDaoJdbc;
import com.ecm.sigre.dao.EventoDao;
import com.ecm.sigre.dao.EventoDaoJdbc;
import com.ecm.sigre.dao.UsuarioDaoJdbc;
import com.ecm.sigre.servicio.EventoServicio;
import com.ecm.sigre.servicio.ReglaNegocioException;
import com.ecm.sigre.util.Validador;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controlador de la programación de eventos.
 * <ul>
 *   <li>GET  /eventos?estado=PROGRAMADO&amp;idCurso=1 - consulta filtrada (formulario HTML con method="get")</li>
 *   <li>GET  /eventos?accion=nuevo                   - formulario de programación</li>
 *   <li>POST /eventos                                - registra el evento (formulario con method="post")</li>
 * </ul>
 */
@WebServlet(name = "EventoServlet", urlPatterns = "/eventos")
public class EventoServlet extends ServletBase {

    private static final long serialVersionUID = 1L;

    private final transient EventoDao eventoDao = new EventoDaoJdbc();
    private final transient EventoServicio eventoServicio = new EventoServicio(eventoDao);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configurarCodificacion(request, response);
        moverMensajeFlash(request);
        if ("nuevo".equals(request.getParameter("accion"))) {
            cargarListas(request);
            mostrarVista(request, response, "eventos/formulario.jsp");
            return;
        }
        String estado = request.getParameter("estado");
        Integer idCurso = Validador.aEntero(request.getParameter("idCurso"));
        request.setAttribute("eventos", eventoDao.listar(estado, idCurso));
        request.setAttribute("cursos", new CursoDaoJdbc().listar(""));
        mostrarVista(request, response, "eventos/lista.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configurarCodificacion(request, response);
        Map<String, String> datos = parametros(request);
        try {
            int idEvento = eventoServicio.programar(datos, LocalDateTime.now());
            redirigirConMensaje(request, response, "/inscripciones?idEvento=" + idEvento,
                    "Evento " + datos.get("codigoEvento").toUpperCase() + " programado correctamente.");
        } catch (ReglaNegocioException e) {
            mostrarErrores(request, e.getErrores());
            request.setAttribute("datos", datos);
            cargarListas(request);
            mostrarVista(request, response, "eventos/formulario.jsp");
        }
    }

    private void cargarListas(HttpServletRequest request) {
        request.setAttribute("cursos", new CursoDaoJdbc().listar(""));
        request.setAttribute("instructores", new UsuarioDaoJdbc().listarPorRol("INSTRUCTOR"));
    }
}
