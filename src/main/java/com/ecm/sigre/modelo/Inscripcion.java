package com.ecm.sigre.modelo;

import java.time.LocalDateTime;

/** Matrícula de un asistente en un evento (tabla matriculas_inscripcion). */
public class Inscripcion {

    private int idMatricula;
    private int idUsuario;
    private int idEvento;
    private String numeroDocumento;
    private String nombreAsistente;
    private String codigoQr;
    private LocalDateTime fechaInscripcion;
    private String estadoMatricula;

    public int getIdMatricula() { return idMatricula; }
    public void setIdMatricula(int idMatricula) { this.idMatricula = idMatricula; }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public int getIdEvento() { return idEvento; }
    public void setIdEvento(int idEvento) { this.idEvento = idEvento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    public String getNombreAsistente() { return nombreAsistente; }
    public void setNombreAsistente(String nombreAsistente) { this.nombreAsistente = nombreAsistente; }
    public String getCodigoQr() { return codigoQr; }
    public void setCodigoQr(String codigoQr) { this.codigoQr = codigoQr; }
    public LocalDateTime getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDateTime fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }
    public String getEstadoMatricula() { return estadoMatricula; }
    public void setEstadoMatricula(String estadoMatricula) { this.estadoMatricula = estadoMatricula; }
}
