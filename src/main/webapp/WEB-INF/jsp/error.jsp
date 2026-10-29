<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ include file="/WEB-INF/jspf/encabezado.jspf" %>
<%
    Integer codigo = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
    String mensaje = (String) request.getAttribute("jakarta.servlet.error.message");
    if (exception != null) {
        // Objeto implícito "exception" (disponible porque isErrorPage="true")
        log("Error en SIGRE", exception);
    }
%>
<section class="tarjeta">
    <h1>Ocurrió un problema</h1>
    <% if (codigo != null && codigo == 404) { %>
        <p>La página o el registro solicitado no existe. <%= esc(mensaje) %></p>
    <% } else { %>
        <p>No fue posible completar la operación. Verifique que la base de datos <code>sigre_db</code> esté disponible
           e intente de nuevo.</p>
    <% } %>
    <p><a href="${pageContext.request.contextPath}/index.jsp">Volver al inicio</a></p>
</section>
<%@ include file="/WEB-INF/jspf/pie.jspf" %>
