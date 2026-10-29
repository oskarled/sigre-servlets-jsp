<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.ecm.sigre.modelo.Evento, com.ecm.sigre.modelo.Curso" %>
<%@ include file="/WEB-INF/jspf/encabezado.jspf" %>
<%
    List<Evento> eventos = (List<Evento>) request.getAttribute("eventos");
    List<Curso> cursos = (List<Curso>) request.getAttribute("cursos");
    String estado = request.getParameter("estado") == null ? "" : request.getParameter("estado");
    String idCurso = request.getParameter("idCurso") == null ? "" : request.getParameter("idCurso");
    String[] estados = {"PROGRAMADO", "EN_CURSO", "FINALIZADO", "CANCELADO"};
%>
<section class="tarjeta">
    <div class="titulo-seccion">
        <h1>Eventos programados</h1>
        <a class="boton" href="eventos?accion=nuevo">+ Programar evento</a>
    </div>

    <%-- Filtros con método GET: el servlet los lee con request.getParameter() --%>
    <form action="eventos" method="get" class="busqueda">
        <select name="estado">
            <option value="">Todos los estados</option>
            <% for (String opcion : estados) { %>
                <option value="<%= opcion %>" <%= opcion.equals(estado) ? "selected" : "" %>><%= opcion %></option>
            <% } %>
        </select>
        <select name="idCurso">
            <option value="">Todos los cursos</option>
            <% for (Curso curso : cursos) { %>
                <option value="<%= curso.getIdCurso() %>" <%= String.valueOf(curso.getIdCurso()).equals(idCurso) ? "selected" : "" %>><%= esc(curso.getCodigoCurso()) %></option>
            <% } %>
        </select>
        <button type="submit">Filtrar</button>
    </form>

    <table>
        <thead>
        <tr><th>Código</th><th>Curso</th><th>Instructor</th><th>Pista</th><th>Inicio</th><th>Cupos</th><th>Estado</th><th></th></tr>
        </thead>
        <tbody>
        <% if (eventos.isEmpty()) { %>
            <tr><td colspan="8" class="vacio">No hay eventos con los filtros seleccionados.</td></tr>
        <% } %>
        <% for (Evento evento : eventos) { %>
            <tr>
                <td><%= esc(evento.getCodigoEvento()) %></td>
                <td><%= esc(evento.getNombreCurso()) %></td>
                <td><%= esc(evento.getNombreInstructor()) %></td>
                <td><%= esc(evento.getPistaEntrenamiento()) %></td>
                <td><%= fecha(evento.getFechaHoraInicio()) %></td>
                <td><%= evento.getCupoDisponible() %> / <%= evento.getCupoMaximo() %></td>
                <td><span class="estado <%= evento.getEstadoEvento().toLowerCase() %>"><%= evento.getEstadoEvento() %></span></td>
                <td><a href="inscripciones?idEvento=<%= evento.getIdEvento() %>">Inscripciones</a></td>
            </tr>
        <% } %>
        </tbody>
    </table>
    <p class="nota">Eventos encontrados: <%= eventos.size() %></p>
</section>
<%@ include file="/WEB-INF/jspf/pie.jspf" %>
