<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.ecm.sigre.modelo.Evento, com.ecm.sigre.modelo.Inscripcion" %>
<%@ include file="/WEB-INF/jspf/encabezado.jspf" %>
<%
    Evento evento = (Evento) request.getAttribute("evento");
    List<Inscripcion> inscripciones = (List<Inscripcion>) request.getAttribute("inscripciones");
    String documento = (String) request.getAttribute("documentoDigitado");
%>
<section class="tarjeta">
    <h1>Evento <%= esc(evento.getCodigoEvento()) %></h1>
    <dl class="detalle">
        <dt>Curso</dt><dd><%= esc(evento.getNombreCurso()) %></dd>
        <dt>Instructor</dt><dd><%= esc(evento.getNombreInstructor()) %></dd>
        <dt>Pista</dt><dd><%= esc(evento.getPistaEntrenamiento()) %></dd>
        <dt>Horario</dt><dd><%= fecha(evento.getFechaHoraInicio()) %> a <%= fecha(evento.getFechaHoraFin()) %></dd>
        <dt>Cupos disponibles</dt><dd><strong><%= evento.getCupoDisponible() %></strong> de <%= evento.getCupoMaximo() %></dd>
        <dt>Estado</dt><dd><span class="estado <%= evento.getEstadoEvento().toLowerCase() %>"><%= evento.getEstadoEvento() %></span></dd>
    </dl>
</section>

<section class="tarjeta">
    <h2>Inscribir asistente</h2>
    <% if (evento.admiteInscripciones()) { %>
        <%-- Formulario POST: la inscripción modifica la base de datos y descuenta un cupo --%>
        <form action="inscripciones" method="post" class="busqueda">
            <input type="hidden" name="idEvento" value="<%= evento.getIdEvento() %>">
            <input name="numeroDocumento" inputmode="numeric" required placeholder="Número de documento del asistente"
                   value="<%= esc(documento) %>">
            <button type="submit">Inscribir</button>
        </form>
        <p class="nota">Se valida que el asistente esté registrado, tenga ARL vigente y no esté inscrito en este evento.</p>
    <% } else { %>
        <p class="vacio">Este evento no recibe inscripciones (sin cupos o no está programado).</p>
    <% } %>
</section>

<section class="tarjeta">
    <h2>Asistentes inscritos (<%= inscripciones.size() %>)</h2>
    <table>
        <thead><tr><th>Documento</th><th>Asistente</th><th>Código de acceso</th><th>Fecha de inscripción</th><th>Estado</th></tr></thead>
        <tbody>
        <% if (inscripciones.isEmpty()) { %>
            <tr><td colspan="5" class="vacio">Aún no hay inscritos.</td></tr>
        <% } %>
        <% for (Inscripcion inscripcion : inscripciones) { %>
            <tr>
                <td><%= esc(inscripcion.getNumeroDocumento()) %></td>
                <td><%= esc(inscripcion.getNombreAsistente()) %></td>
                <td><code><%= esc(inscripcion.getCodigoQr()) %></code></td>
                <td><%= fecha(inscripcion.getFechaInscripcion()) %></td>
                <td><%= esc(inscripcion.getEstadoMatricula()) %></td>
            </tr>
        <% } %>
        </tbody>
    </table>
    <p><a href="eventos">&larr; Volver a eventos</a></p>
</section>
<%@ include file="/WEB-INF/jspf/pie.jspf" %>
