package com.ecm.sigre.controlador;

import com.ecm.sigre.dao.CursoDao;
import com.ecm.sigre.dao.CursoDaoJdbc;
import com.ecm.sigre.modelo.Curso;
import com.ecm.sigre.servicio.CursoServicio;
import com.ecm.sigre.servicio.ReglaNegocioException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Controlador del catálogo de cursos TSA.
 * <ul>
 *   <li>GET  /cursos?accion=listar&amp;q=texto  - consulta con filtro (parámetro en la URL)</li>
 *   <li>GET  /cursos?accion=nuevo | editar&amp;id=N - muestra el formulario</li>
 *   <li>POST /cursos accion=guardar | eliminar    - modifica datos (parámetros en el cuerpo)</li>
 * </ul>
 */
@WebServlet(name = "CursoServlet", urlPatterns = "/cursos")
public class CursoServlet extends ServletBase {

    private static final long serialVersionUID = 1L;

    private final transient CursoDao cursoDao = new CursoDaoJdbc();
    private final transient CursoServicio cursoServicio = new CursoServicio(cursoDao);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configurarCodificacion(request, response);
        moverMensajeFlash(request);
        String accion = request.getParameter("accion") == null ? "listar" : request.getParameter("accion");

        switch (accion) {
            case "nuevo" -> mostrarFormulario(request, response, new Curso());
            case "editar" -> {
                Curso curso = cursoDao.buscarPorId(enteroObligatorio(request, "id")).orElse(null);
                if (curso == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "El curso no existe");
                    return;
                }
                mostrarFormulario(request, response, curso);
            }
            default -> {
                String filtro = request.getParameter("q");
                request.setAttribute("filtro", filtro == null ? "" : filtro);
                request.setAttribute("cursos", cursoDao.listar(filtro));
                mostrarVista(request, response, "cursos/lista.jsp");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configurarCodificacion(request, response);
        Map<String, String> datos = parametros(request);
        String accion = datos.getOrDefault("accion", "guardar");

        try {
            if ("eliminar".equals(accion)) {
                cursoServicio.eliminar(enteroObligatorio(request, "idCurso"));
                redirigirConMensaje(request, response, "/cursos", "Curso eliminado correctamente.");
            } else {
                boolean esNuevo = enteroObligatorio(request, "idCurso") == 0;
                cursoServicio.guardar(datos);
                redirigirConMensaje(request, response, "/cursos",
                        esNuevo ? "Curso registrado correctamente." : "Curso actualizado correctamente.");
            }
        } catch (ReglaNegocioException e) {
            mostrarErrores(request, e.getErrores());
            if ("eliminar".equals(accion)) {
                request.setAttribute("cursos", cursoDao.listar(""));
                request.setAttribute("filtro", "");
                mostrarVista(request, response, "cursos/lista.jsp");
            } else {
                mostrarFormulario(request, response, cursoDesdeFormulario(datos));
            }
        }
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response, Curso curso)
            throws ServletException, IOException {
        request.setAttribute("curso", curso);
        request.setAttribute("categorias", cursoDao.listarCategorias());
        mostrarVista(request, response, "cursos/formulario.jsp");
    }

    /** Reconstruye el curso con lo que escribió el usuario para no perder los datos cuando hay errores. */
    private Curso cursoDesdeFormulario(Map<String, String> datos) {
        Curso curso = new Curso();
        curso.setIdCurso(convertir(datos.get("idCurso")));
        curso.setIdCategoria(convertir(datos.get("idCategoria")));
        curso.setCodigoCurso(datos.getOrDefault("codigoCurso", ""));
        curso.setNombreCurso(datos.getOrDefault("nombreCurso", ""));
        curso.setIntensidadHoraria(convertir(datos.get("intensidadHoraria")));
        curso.setVigenciaMeses(convertir(datos.get("vigenciaMeses")));
        curso.setRequisitoNivelRiesgo(datos.getOrDefault("requisitoNivelRiesgo", "V"));
        return curso;
    }

    private int convertir(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }
}
