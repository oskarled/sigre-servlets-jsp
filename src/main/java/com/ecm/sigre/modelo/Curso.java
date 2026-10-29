package com.ecm.sigre.modelo;

/** Curso de Trabajo Seguro en Alturas (tabla cursos_tsa). */
public class Curso {

    private int idCurso;
    private int idCategoria;
    private String nombreCategoria;
    private String codigoCurso;
    private String nombreCurso;
    private int intensidadHoraria;
    private int vigenciaMeses;
    private String requisitoNivelRiesgo;

    public int getIdCurso() { return idCurso; }
    public void setIdCurso(int idCurso) { this.idCurso = idCurso; }
    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }
    public String getCodigoCurso() { return codigoCurso; }
    public void setCodigoCurso(String codigoCurso) { this.codigoCurso = codigoCurso; }
    public String getNombreCurso() { return nombreCurso; }
    public void setNombreCurso(String nombreCurso) { this.nombreCurso = nombreCurso; }
    public int getIntensidadHoraria() { return intensidadHoraria; }
    public void setIntensidadHoraria(int intensidadHoraria) { this.intensidadHoraria = intensidadHoraria; }
    public int getVigenciaMeses() { return vigenciaMeses; }
    public void setVigenciaMeses(int vigenciaMeses) { this.vigenciaMeses = vigenciaMeses; }
    public String getRequisitoNivelRiesgo() { return requisitoNivelRiesgo; }
    public void setRequisitoNivelRiesgo(String requisitoNivelRiesgo) { this.requisitoNivelRiesgo = requisitoNivelRiesgo; }
}
