package com.ecm.sigre.modelo;

/** Usuario del sistema: instructor o asistente (tabla usuarios). */
public class Usuario {

    private int idUsuario;
    private String numeroDocumento;
    private String nombres;
    private String apellidos;
    private String correoElectronico;
    private String nombreRol;
    private String estadoArl;

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    public boolean tieneArlVigente() {
        return "VIGENTE".equals(estadoArl);
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }
    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }
    public String getEstadoArl() { return estadoArl; }
    public void setEstadoArl(String estadoArl) { this.estadoArl = estadoArl; }
}
