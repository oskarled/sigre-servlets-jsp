<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.ecm.sigre.modelo.Curso, com.ecm.sigre.modelo.Usuario, java.util.Map" %>
<%@ include file="/WEB-INF/jspf/encabezado.jspf" %>
<%
    List<Curso> cursos = (List<Curso>) request.getAttribute("cursos");
    List<Usuario> instructores = (List<Usuario>) request.getAttribute("instructores");
    Map<String, String> datos = (Map<String, String>) request.getAttribute("datos");
    if (datos == null) {
        datos = new java.util.HashMap<>();
    }
%>
<section class="tarjeta">
    <h1>Programar evento</h1>
    <form action="eventos" method="post" class="formulario">
        <div class="fila">
            <div>
                <label for="idCurso">Curso</label>
                <select id="idCurso" name="idCurso" required>
                    <option value="">Seleccione...</option>
                    <% for (Curso curso : cursos) { %>
                        <option value="<%= curso.getIdCurso() %>" <%= String.valueOf(curso.getIdCurso()).equals(datos.get("idCurso")) ? "selected" : "" %>>
                            <%= esc(curso.getCodigoCurso() + " - " + curso.getNombreCurso()) %></option>
                    <% } %>
                </select>
            </div>
            <div>
                <label for="idInstructor">Instructor</label>
                <select id="idInstructor" name="idInstructor" required>
                    <option value="">Seleccione...</option>
                    <% for (Usuario instructor : instructores) { %>
                        <option value="<%= instructor.getIdUsuario() %>" <%= String.valueOf(instructor.getIdUsuario()).equals(datos.get("idInstructor")) ? "selected" : "" %>>
                            <%= esc(instructor.getNombreCompleto()) %></option>
                    <% } %>
                </select>
            </div>
        </div>

        <div class="fila">
            <div>
                <label for="codigoEvento">Código del evento</label>
                <input id="codigoEvento" name="codigoEvento" maxlength="30" required placeholder="EVT-202611-01"
                       value="<%= esc(datos.get("codigoEvento")) %>">
            </div>
            <div>
                <label for="pistaEntrenamiento">Pista de entrenamiento</label>
                <input id="pistaEntrenamiento" name="pistaEntrenamiento" maxlength="100" required
                       value="<%= esc(datos.get("pistaEntrenamiento")) %>">
            </div>
        </div>

        <div class="fila">
            <div>
                <label for="fechaHoraInicio">Inicio</label>
                <input type="datetime-local" id="fechaHoraInicio" name="fechaHoraInicio" required value="<%= esc(datos.get("fechaHoraInicio")) %>">
            </div>
            <div>
                <label for="fechaHoraFin">Fin</label>
                <input type="datetime-local" id="fechaHoraFin" name="fechaHoraFin" required value="<%= esc(datos.get("fechaHoraFin")) %>">
            </div>
            <div>
                <label for="cupoMaximo">Cupo máximo (1 a 30)</label>
                <input type="number" id="cupoMaximo" name="cupoMaximo" min="1" max="30" required value="<%= esc(datos.get("cupoMaximo")) %>">
            </div>
        </div>

        <div class="botones">
            <button type="submit">Programar</button>
            <a href="eventos" class="secundario">Cancelar</a>
        </div>
    </form>
</section>
<%@ include file="/WEB-INF/jspf/pie.jspf" %>
