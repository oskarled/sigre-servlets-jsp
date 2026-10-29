<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jspf/encabezado.jspf" %>
<%-- Acción estándar jsp:useBean: crea un objeto Date con el momento de la petición --%>
<jsp:useBean id="ahora" class="java.util.Date" scope="request"/>
<section class="tarjeta">
    <h1>Sistema de Gestión de Registro para Eventos</h1>
    <p>Módulo web para administrar el catálogo de cursos de Trabajo Seguro en Alturas,
       programar eventos en las pistas de entrenamiento e inscribir asistentes.</p>
    <div class="accesos">
        <a class="acceso" href="cursos"><strong>Cursos TSA</strong><span>Registrar, editar y eliminar cursos</span></a>
        <a class="acceso" href="eventos"><strong>Eventos</strong><span>Programar eventos y consultar cupos</span></a>
        <a class="acceso" href="consulta-eventos.html"><strong>Consulta rápida</strong><span>Formulario HTML con método GET</span></a>
    </div>
    <p class="nota">Petición atendida con el método <%= request.getMethod() %> el <%= ahora %>.
       Sesión: <%= session.isNew() ? "nueva" : "activa" %>.</p>
</section>
<%@ include file="/WEB-INF/jspf/pie.jspf" %>
