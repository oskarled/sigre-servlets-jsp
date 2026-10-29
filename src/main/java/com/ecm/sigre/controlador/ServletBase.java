package com.ecm.sigre.controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Funciones comunes de los controladores: lectura de parámetros, mensajes "flash"
 * (que sobreviven a una redirección) y envío a las vistas JSP.
 */
public abstract class ServletBase extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected static final String RUTA_VISTAS = "/WEB-INF/jsp/";

    /** Copia los parámetros del formulario (GET o POST) en un mapa. */
    protected Map<String, String> parametros(HttpServletRequest request) {
        Map<String, String> datos = new HashMap<>();
        request.getParameterMap().forEach((clave, valores) -> datos.put(clave, valores.length > 0 ? valores[0] : ""));
        return datos;
    }

    protected void mostrarVista(HttpServletRequest request, HttpServletResponse response, String vista)
            throws ServletException, IOException {
        request.getRequestDispatcher(RUTA_VISTAS + vista).forward(request, response);
    }

    /** Patrón Post/Redirect/Get: después de un POST se redirige para evitar reenvíos del formulario. */
    protected void redirigirConMensaje(HttpServletRequest request, HttpServletResponse response,
                                       String ruta, String mensaje) throws IOException {
        request.getSession().setAttribute("mensajeExito", mensaje);
        response.sendRedirect(request.getContextPath() + ruta);
    }

    protected void mostrarErrores(HttpServletRequest request, List<String> errores) {
        request.setAttribute("errores", errores);
    }

    /** Mueve el mensaje guardado en sesión a la petición actual y lo borra de la sesión. */
    protected void moverMensajeFlash(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        if (sesion != null && sesion.getAttribute("mensajeExito") != null) {
            request.setAttribute("mensajeExito", sesion.getAttribute("mensajeExito"));
            sesion.removeAttribute("mensajeExito");
        }
    }

    protected int enteroObligatorio(HttpServletRequest request, String nombre) {
        try {
            return Integer.parseInt(request.getParameter(nombre));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    protected void configurarCodificacion(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
    }
}
