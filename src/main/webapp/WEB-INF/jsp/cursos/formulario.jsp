<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.ecm.sigre.modelo.Curso, com.ecm.sigre.modelo.Categoria" %>
<%@ include file="/WEB-INF/jspf/encabezado.jspf" %>
<%
    Curso curso = (Curso) request.getAttribute("curso");
    List<Categoria> categorias = (List<Categoria>) request.getAttribute("categorias");
    boolean esNuevo = curso.getIdCurso() == 0;
    String nivel = curso.getRequisitoNivelRiesgo() == null ? "V" : curso.getRequisitoNivelRiesgo();
%>
<section class="tarjeta">
    <h1><%= esNuevo ? "Registrar curso" : "Editar curso " + esc(curso.getCodigoCurso()) %></h1>

    <%-- Formulario HTML que envía los datos al servlet CursoServlet con el método POST --%>
    <form action="cursos" method="post" class="formulario">
        <input type="hidden" name="accion" value="guardar">
        <input type="hidden" name="idCurso" value="<%= curso.getIdCurso() %>">

        <label for="codigoCurso">Código</label>
        <input id="codigoCurso" name="codigoCurso" maxlength="20" required placeholder="TSA-ADV-01"
               value="<%= esc(curso.getCodigoCurso()) %>">

        <label for="nombreCurso">Nombre del curso</label>
        <input id="nombreCurso" name="nombreCurso" maxlength="150" required value="<%= esc(curso.getNombreCurso()) %>">

        <label for="idCategoria">Categoría</label>
        <select id="idCategoria" name="idCategoria" required>
            <% for (Categoria categoria : categorias) { %>
                <option value="<%= categoria.getIdCategoria() %>"
                    <%= categoria.getIdCategoria() == curso.getIdCategoria() ? "selected" : "" %>><%= esc(categoria.getNombreCategoria()) %></option>
            <% } %>
        </select>

        <div class="fila">
            <div>
                <label for="intensidadHoraria">Intensidad horaria (horas)</label>
                <input type="number" id="intensidadHoraria" name="intensidadHoraria" min="1" max="200" required
                       value="<%= curso.getIntensidadHoraria() == 0 ? "" : curso.getIntensidadHoraria() %>">
            </div>
            <div>
                <label for="vigenciaMeses">Vigencia del certificado (meses)</label>
                <input type="number" id="vigenciaMeses" name="vigenciaMeses" min="1" max="60" required
                       value="<%= curso.getVigenciaMeses() == 0 ? 12 : curso.getVigenciaMeses() %>">
            </div>
            <div>
                <label for="requisitoNivelRiesgo">Nivel de riesgo ARL exigido</label>
                <select id="requisitoNivelRiesgo" name="requisitoNivelRiesgo">
                    <option value="IV" <%= "IV".equals(nivel) ? "selected" : "" %>>IV</option>
                    <option value="V" <%= "V".equals(nivel) ? "selected" : "" %>>V</option>
                </select>
            </div>
        </div>

        <div class="botones">
            <button type="submit"><%= esNuevo ? "Registrar" : "Guardar cambios" %></button>
            <a href="cursos" class="secundario">Cancelar</a>
        </div>
    </form>
</section>
<%@ include file="/WEB-INF/jspf/pie.jspf" %>
