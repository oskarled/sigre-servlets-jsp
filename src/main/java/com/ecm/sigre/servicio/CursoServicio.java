package com.ecm.sigre.servicio;

import com.ecm.sigre.dao.CursoDao;
import com.ecm.sigre.modelo.Curso;
import com.ecm.sigre.util.Validador;
import java.util.Map;

/** Reglas de negocio para registrar y modificar cursos TSA. */
public class CursoServicio {

    private final CursoDao cursoDao;

    public CursoServicio(CursoDao cursoDao) {
        this.cursoDao = cursoDao;
    }

    /**
     * Valida los parámetros del formulario y guarda el curso (inserta si idCurso es 0, si no actualiza).
     *
     * @param datos parámetros recibidos por POST
     * @return id del curso guardado
     */
    public int guardar(Map<String, String> datos) {
        String codigo = valor(datos, "codigoCurso").toUpperCase();
        Validador validador = new Validador()
                .requerido(codigo, "código")
                .codigoCurso(codigo)
                .requerido(valor(datos, "nombreCurso"), "nombre del curso")
                .longitudMaxima(valor(datos, "nombreCurso"), 150, "nombre del curso")
                .requerido(valor(datos, "idCategoria"), "categoría")
                .requerido(valor(datos, "intensidadHoraria"), "intensidad horaria")
                .enteroEnRango(valor(datos, "intensidadHoraria"), 1, 200, "intensidad horaria")
                .requerido(valor(datos, "vigenciaMeses"), "vigencia")
                .enteroEnRango(valor(datos, "vigenciaMeses"), 1, 60, "vigencia");
        String nivel = valor(datos, "requisitoNivelRiesgo");
        if (!nivel.equals("IV") && !nivel.equals("V")) {
            validador.getErrores().add("El nivel de riesgo debe ser IV o V.");
        }
        if (!validador.esValido()) {
            throw new ReglaNegocioException(validador.getErrores());
        }

        int idCurso = Validador.aEntero(valor(datos, "idCurso")) == null ? 0 : Validador.aEntero(valor(datos, "idCurso"));
        if (cursoDao.existeCodigo(codigo, idCurso)) {
            throw new ReglaNegocioException("Ya existe un curso con el código " + codigo + ".");
        }

        Curso curso = new Curso();
        curso.setIdCurso(idCurso);
        curso.setCodigoCurso(codigo);
        curso.setNombreCurso(valor(datos, "nombreCurso"));
        curso.setIdCategoria(Validador.aEntero(valor(datos, "idCategoria")));
        curso.setIntensidadHoraria(Validador.aEntero(valor(datos, "intensidadHoraria")));
        curso.setVigenciaMeses(Validador.aEntero(valor(datos, "vigenciaMeses")));
        curso.setRequisitoNivelRiesgo(nivel);

        if (idCurso == 0) {
            return cursoDao.insertar(curso);
        }
        cursoDao.actualizar(curso);
        return idCurso;
    }

    public void eliminar(int idCurso) {
        if (!cursoDao.eliminar(idCurso)) {
            throw new ReglaNegocioException("No se puede eliminar el curso porque tiene eventos programados.");
        }
    }

    private static String valor(Map<String, String> datos, String clave) {
        String valor = datos.get(clave);
        return valor == null ? "" : valor.trim();
    }
}
