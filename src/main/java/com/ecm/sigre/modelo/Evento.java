package com.ecm.sigre.modelo;

import java.time.LocalDateTime;

/** Programación de un curso en una pista de entrenamiento (tabla programaciones_evento). */
public class Evento {

    public static final String PROGRAMADO = "PROGRAMADO";

    private int idEvento;
    private int idCurso;
    private String nombreCurso;
    private int idInstructor;
    private String nombreInstructor;
    private String codigoEvento;
    private String pistaEntrenamiento;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private int cupoMaximo;
    private int cupoDisponible;
    private String estadoEvento;

    /** Indica si el evento todavía recibe inscripciones. */
    public boolean admiteInscripciones() {
        return PROGRAMADO.equals(estadoEvento) && cupoDisponible > 0;
    }

    public int getIdEvento() { return idEvento; }
    public void setIdEvento(int idEvento) { this.idEvento = idEvento; }
    public int getIdCurso() { return idCurso; }
    public void setIdCurso(int idCurso) { this.idCurso = idCurso; }
    public String getNombreCurso() { return nombreCurso; }
    public void setNombreCurso(String nombreCurso) { this.nombreCurso = nombreCurso; }
    public int getIdInstructor() { return idInstructor; }
    public void setIdInstructor(int idInstructor) { this.idInstructor = idInstructor; }
    public String getNombreInstructor() { return nombreInstructor; }
    public void setNombreInstructor(String nombreInstructor) { this.nombreInstructor = nombreInstructor; }
    public String getCodigoEvento() { return codigoEvento; }
    public void setCodigoEvento(String codigoEvento) { this.codigoEvento = codigoEvento; }
    public String getPistaEntrenamiento() { return pistaEntrenamiento; }
    public void setPistaEntrenamiento(String pistaEntrenamiento) { this.pistaEntrenamiento = pistaEntrenamiento; }
    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) { this.fechaHoraInicio = fechaHoraInicio; }
    public LocalDateTime getFechaHoraFin() { return fechaHoraFin; }
    public void setFechaHoraFin(LocalDateTime fechaHoraFin) { this.fechaHoraFin = fechaHoraFin; }
    public int getCupoMaximo() { return cupoMaximo; }
    public void setCupoMaximo(int cupoMaximo) { this.cupoMaximo = cupoMaximo; }
    public int getCupoDisponible() { return cupoDisponible; }
    public void setCupoDisponible(int cupoDisponible) { this.cupoDisponible = cupoDisponible; }
    public String getEstadoEvento() { return estadoEvento; }
    public void setEstadoEvento(String estadoEvento) { this.estadoEvento = estadoEvento; }
}
