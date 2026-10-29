package com.ecm.sigre.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gestiona la conexión JDBC con la base de datos sigre_db.
 * Lee los parámetros del archivo db.properties y permite sobrescribirlos
 * con variables de entorno para no dejar credenciales en el código.
 */
public final class ConexionBD {

    private static final Properties CONFIGURACION = cargarConfiguracion();

    private ConexionBD() {
        // Clase utilitaria: no se instancia.
    }

    private static Properties cargarConfiguracion() {
        Properties propiedades = new Properties();
        try (InputStream entrada = ConexionBD.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (entrada != null) {
                propiedades.load(entrada);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No fue posible leer db.properties", e);
        }
        sobrescribirConEntorno(propiedades, "db.driver", "SIGRE_DB_DRIVER");
        sobrescribirConEntorno(propiedades, "db.url", "SIGRE_DB_URL");
        sobrescribirConEntorno(propiedades, "db.usuario", "SIGRE_DB_USUARIO");
        sobrescribirConEntorno(propiedades, "db.clave", "SIGRE_DB_CLAVE");
        try {
            Class.forName(propiedades.getProperty("db.driver"));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("No se encontró el driver JDBC " + propiedades.getProperty("db.driver"), e);
        }
        return propiedades;
    }

    private static void sobrescribirConEntorno(Properties propiedades, String clave, String variable) {
        String valor = System.getenv(variable);
        if (valor != null && !valor.isBlank()) {
            propiedades.setProperty(clave, valor);
        }
    }

    /**
     * Abre una conexión nueva. Quien la solicita debe cerrarla (try-with-resources).
     *
     * @return conexión activa con la base de datos
     * @throws SQLException si la base de datos no está disponible
     */
    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(
                CONFIGURACION.getProperty("db.url"),
                CONFIGURACION.getProperty("db.usuario"),
                CONFIGURACION.getProperty("db.clave", ""));
    }
}
