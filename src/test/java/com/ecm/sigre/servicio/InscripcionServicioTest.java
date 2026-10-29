package com.ecm.sigre.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ecm.sigre.dao.EventoDao;
import com.ecm.sigre.dao.InscripcionDao;
import com.ecm.sigre.dao.UsuarioDao;
import com.ecm.sigre.modelo.Evento;
import com.ecm.sigre.modelo.Inscripcion;
import com.ecm.sigre.modelo.Usuario;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Pruebas de las reglas de inscripción usando DAO en memoria (sin base de datos). */
class InscripcionServicioTest {

    private Evento evento;
    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Inscripcion> inscripciones = new ArrayList<>();
    private InscripcionServicio servicio;

    @BeforeEach
    void preparar() {
        evento = new Evento();
        evento.setIdEvento(1);
        evento.setCodigoEvento("EVT-202611-01");
        evento.setEstadoEvento(Evento.PROGRAMADO);
        evento.setCupoMaximo(2);
        evento.setCupoDisponible(2);

        usuarios.clear();
        inscripciones.clear();
        usuarios.add(usuario(3, "1047500123", "VIGENTE"));
        usuarios.add(usuario(4, "1047500200", "VENCIDA"));

        EventoDao eventoDao = new EventoDao() {
            public List<Evento> listar(String estado, Integer idCurso) { return List.of(evento); }
            public Optional<Evento> buscarPorId(int idEvento) { return idEvento == 1 ? Optional.of(evento) : Optional.empty(); }
            public boolean existeCodigo(String codigoEvento) { return false; }
            public int insertar(Evento nuevo) { return 1; }
        };
        UsuarioDao usuarioDao = new UsuarioDao() {
            public Optional<Usuario> buscarPorDocumento(String documento) {
                return usuarios.stream().filter(u -> u.getNumeroDocumento().equals(documento)).findFirst();
            }
            public List<Usuario> listarPorRol(String rol) { return usuarios; }
        };
        InscripcionDao inscripcionDao = new InscripcionDao() {
            public boolean existe(int idUsuario, int idEvento) {
                return inscripciones.stream().anyMatch(i -> i.getIdUsuario() == idUsuario && i.getIdEvento() == idEvento);
            }
            public int insertar(Inscripcion inscripcion) {
                inscripciones.add(inscripcion);
                evento.setCupoDisponible(evento.getCupoDisponible() - 1);
                return inscripciones.size();
            }
            public List<Inscripcion> listarPorEvento(int idEvento) { return inscripciones; }
        };
        servicio = new InscripcionServicio(eventoDao, usuarioDao, inscripcionDao);
    }

    @Test
    void inscribeAsistenteConArlVigenteYDescuentaCupo() {
        Inscripcion inscripcion = servicio.inscribir(1, "1047500123");
        assertEquals("QR-SIGRE-EVT-202611-01-USR0003", inscripcion.getCodigoQr());
        assertEquals(1, evento.getCupoDisponible());
    }

    @Test
    void rechazaAsistenteSinArlVigente() {
        ReglaNegocioException error = assertThrows(ReglaNegocioException.class,
                () -> servicio.inscribir(1, "1047500200"));
        assertTrue(error.getMessage().contains("ARL"));
    }

    @Test
    void rechazaInscripcionDuplicada() {
        servicio.inscribir(1, "1047500123");
        assertThrows(ReglaNegocioException.class, () -> servicio.inscribir(1, "1047500123"));
    }

    @Test
    void rechazaEventoSinCupos() {
        evento.setCupoDisponible(0);
        ReglaNegocioException error = assertThrows(ReglaNegocioException.class,
                () -> servicio.inscribir(1, "1047500123"));
        assertTrue(error.getMessage().contains("cupos"));
    }

    @Test
    void rechazaEventoNoProgramado() {
        evento.setEstadoEvento("FINALIZADO");
        assertThrows(ReglaNegocioException.class, () -> servicio.inscribir(1, "1047500123"));
    }

    @Test
    void rechazaDocumentoInexistenteOInvalido() {
        assertThrows(ReglaNegocioException.class, () -> servicio.inscribir(1, "999999999"));
        assertThrows(ReglaNegocioException.class, () -> servicio.inscribir(1, "abc"));
    }

    private static Usuario usuario(int id, String documento, String estadoArl) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        usuario.setNumeroDocumento(documento);
        usuario.setNombres("Asistente");
        usuario.setApellidos(String.valueOf(id));
        usuario.setEstadoArl(estadoArl);
        return usuario;
    }
}
