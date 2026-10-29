<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.ecm.sigre.modelo.Curso" %>
<%@ include file="/WEB-INF/jspf/encabezado.jspf" %>
<%
    List<Curso> cursos = (List<Curso>) request.getAttribute("cursos");
    String filtro = (String) request.getAttribute("filtro");
%>
<section class="tarjeta">
    <div class="titulo-seccion">
        <h1>Catálogo de cursos TSA</h1>
        <a class="boton" href="cursos?accion=nuevo">+ Nuevo curso</a>
    </div>

    <%-- Formulario de búsqueda con método GET: el filtro queda en la URL (cursos?q=...) --%>
    <form action="cursos" method="get" class="busqueda">
        <input type="hidden" name="accion" value="listar">
        <input type="search" name="q" value="<%= esc(filtro) %>" placeholder="Buscar por código o nombre">
        <button type="submit">Buscar</button>
    </form>

    <table>
        <thead>
        <tr><th>Código</th><th>Nombre</th><th>Categoría</th><th>Horas</th><th>Vigencia</th><th>Riesgo</th><th>Acciones</th></tr>
        </thead>
        <tbody>
        <% if (cursos.isEmpty()) { %>
            <tr><td colspan="7" class="vacio">No hay cursos que coincidan con la búsqueda.</td></tr>
        <% } %>
        <% for (Curso curso : cursos) { %>
            <tr>
                <td><%= esc(curso.getCodigoCurso()) %></td>
                <td><%= esc(curso.getNombreCurso()) %></td>
                <td><%= esc(curso.getNombreCategoria()) %></td>
                <td><%= curso.getIntensidadHoraria() %> h</td>
                <td><%= curso.getVigenciaMeses() %> meses</td>
                <td><%= esc(curso.getRequisitoNivelRiesgo()) %></td>
                <td class="acciones">
                    <a href="cursos?accion=editar&amp;id=<%= curso.getIdCurso() %>">Editar</a>
                    <%-- Eliminar modifica datos, por eso se envía con POST y no con un enlace GET --%>
                    <form action="cursos" method="post" onsubmit="return confirm('¿Eliminar este curso?');">
                        <input type="hidden" name="accion" value="eliminar">
                        <input type="hidden" name="idCurso" value="<%= curso.getIdCurso() %>">
                        <button type="submit" class="enlace-peligro">Eliminar</button>
                    </form>
                </td>
            </tr>
        <% } %>
        </tbody>
    </table>
    <p class="nota">Total de cursos: <%= cursos.size() %></p>
</section>
<%@ include file="/WEB-INF/jspf/pie.jspf" %>
