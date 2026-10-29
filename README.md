# SIGRE – Módulo web de cursos, eventos e inscripciones (Servlets y JSP)

**Evidencia:** GA7-220501096-AA2-EV02 – Módulos de software codificados y probados
**Aprendiz:** Oscar Ramón Ledesma Puello · **Ficha:** 3134533 · **Instructor:** Luis Manuel Cabrales Valdés
**Cliente:** E.C.M. Training Center S.A.S. (Cartagena de Indias)

Módulo web del Sistema de Gestión de Registro para Eventos (SIGRE) construido con **Java, Servlets, JSP y JDBC** sobre la base de datos `sigre_db` (MySQL 8). Permite:

- Administrar el catálogo de cursos de Trabajo Seguro en Alturas (registrar, consultar, editar y eliminar).
- Programar eventos en las pistas de entrenamiento con control de aforo (máximo 30 personas).
- Inscribir asistentes validando que estén registrados, tengan ARL vigente y no estén inscritos dos veces; cada inscripción descuenta un cupo y genera un código de acceso.

## Requisitos de la evidencia

| Indicador | Dónde está en el código |
|---|---|
| **1. Formularios HTML con servlets** | `cursos/formulario.jsp` → `CursoServlet`; `eventos/formulario.jsp` → `EventoServlet`; `inscripciones/formulario.jsp` → `InscripcionServlet`; formulario HTML estático `consulta-eventos.html` → `EventoServlet`. Los servlets usan `@WebServlet` y extienden `HttpServlet`. |
| **2. Métodos GET y POST** | **GET** (`doGet`) para consultas con parámetros en la URL: `cursos?accion=listar&q=REC`, `eventos?estado=PROGRAMADO&idCurso=1`, `inscripciones?idEvento=2`. **POST** (`doPost`) para todo lo que modifica datos: registrar/editar/eliminar cursos, programar eventos e inscribir asistentes. Después de cada POST se aplica el patrón Post/Redirect/Get. |
| **3. Elementos de JSP** | Directivas `<%@ page %>` e `<%@ include %>` (fragmentos `encabezado.jspf` y `pie.jspf`); declaraciones `<%! %>` (métodos `esc()` y `fecha()`); scriptlets `<% %>` (ciclos y condiciones); expresiones `<%= %>`; acción estándar `<jsp:useBean>` (index.jsp); expresiones EL `${pageContext.request.contextPath}`; objetos implícitos `request`, `session` y `exception`; página de error con `isErrorPage="true"`. |
| **4. Herramienta de versionamiento** | Proyecto versionado con Git y publicado en GitHub (ver `ENLACE_REPOSITORIO.txt`). |

## Tecnologías

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Web | Jakarta Servlet 6.0, Jakarta Server Pages 3.1 |
| Servidor | Apache Tomcat 10.1 |
| Base de datos | MySQL 8 (JDBC con `mysql-connector-j` 8.4) |
| Pruebas | JUnit 5 |
| Construcción | Maven 3.9 |
| Versionamiento | Git y GitHub |

## Estructura del proyecto

```
sigre-servlets-jsp/
├── pom.xml
├── sql/
│   ├── 01_sigre_db.sql            Script de la base de datos sigre_db (tablas, vista, procedimiento, trigger)
│   └── 02_datos_prueba_ev02.sql   Datos de prueba para esta evidencia
├── src/main/java/com/ecm/sigre/
│   ├── controlador/   Servlets: CursoServlet, EventoServlet, InscripcionServlet, ServletBase
│   ├── servicio/      Reglas de negocio: CursoServicio, EventoServicio, InscripcionServicio
│   ├── dao/           Acceso a datos con JDBC (interfaces + implementaciones *DaoJdbc)
│   ├── modelo/        Clases de dominio: Curso, Categoria, Evento, Usuario, Inscripcion
│   └── util/          ConexionBD (JDBC) y Validador (validación de formularios)
├── src/main/resources/db.properties   Parámetros de conexión
├── src/main/webapp/
│   ├── index.jsp, consulta-eventos.html, css/estilos.css
│   └── WEB-INF/ web.xml, jspf/ (fragmentos), jsp/ (vistas de cursos, eventos, inscripciones y error)
├── src/test/java/     Pruebas unitarias JUnit 5
└── docs/pruebas/      Capturas de las pruebas funcionales y resultado de las pruebas unitarias
```

Arquitectura en capas MVC: la **vista** (JSP) muestra los datos y los formularios, el **controlador** (servlet) recibe la petición GET o POST, el **servicio** aplica las reglas de negocio y el **DAO** ejecuta las sentencias SQL con `PreparedStatement`.

## Cómo ejecutar el proyecto

1. **Base de datos.** En MySQL Workbench (o la consola `mysql`) ejecutar, en este orden:
   `sql/01_sigre_db.sql` y luego `sql/02_datos_prueba_ev02.sql`.
2. **Conexión.** Editar `src/main/resources/db.properties` con el usuario y la clave de MySQL (o definir las variables de entorno `SIGRE_DB_URL`, `SIGRE_DB_USUARIO` y `SIGRE_DB_CLAVE`).
3. **Compilar y probar.** En la carpeta del proyecto:
   ```
   mvn clean package
   ```
   Maven ejecuta las pruebas unitarias y genera `target/sigre.war`.
4. **Desplegar.** Copiar `target/sigre.war` en la carpeta `webapps` de Apache Tomcat 10.1 e iniciar Tomcat (o abrir el proyecto en NetBeans/IntelliJ y ejecutarlo con un servidor Tomcat 10.1).
5. Abrir `http://localhost:8080/sigre/`.

> Tomcat 10.1 usa el espacio de nombres `jakarta.*`. Con Tomcat 9 (javax.*) el proyecto no funciona.

## Pruebas realizadas

**Pruebas unitarias (JUnit 5): 16 de 16 exitosas.** Detalle en `docs/pruebas/resultado_pruebas_unitarias.txt`.

| Clase de prueba | Qué verifica |
|---|---|
| `ValidadorTest` | Formato del código de curso, campos obligatorios, rangos numéricos, fechas y documento. |
| `EventoServicioTest` | Programación válida, rechazo de cupo mayor a 30, fecha pasada y código repetido. |
| `InscripcionServicioTest` | Inscripción con ARL vigente y descuento del cupo; rechazo por ARL vencida, inscripción duplicada, evento sin cupos, evento no programado y documento inexistente. |

**Pruebas funcionales** sobre Tomcat 10.1 con la base de datos `sigre_db` y los datos de prueba (capturas en `docs/pruebas/`):

| N.º | Caso | Método | Resultado esperado | Resultado | Captura |
|---|---|---|---|---|---|
| 1 | Listar cursos | GET | Tabla con los cursos registrados | Cumple | 02 |
| 2 | Buscar curso "REC" | GET | URL `cursos?accion=listar&q=REC` y un resultado | Cumple | 03 |
| 3 | Registrar curso con código e intensidad inválidos | POST | Mensajes de validación sin perder los datos | Cumple | 04 |
| 4 | Registrar curso válido | POST | "Curso registrado correctamente" | Cumple | 05 |
| 5 | Editar curso | POST | "Curso actualizado correctamente" | Cumple | 06 |
| 6 | Eliminar curso con eventos | POST | Bloquea el borrado | Cumple | 07 |
| 7 | Eliminar curso sin eventos | POST | "Curso eliminado correctamente" | Cumple | 08 |
| 8 | Consulta desde formulario HTML estático | GET | URL `eventos?estado=PROGRAMADO` y eventos filtrados | Cumple | 09, 10 |
| 9 | Programar evento con cupo de 40 | POST | Rechaza: cupo entre 1 y 30 | Cumple | 11 |
| 10 | Programar evento válido | POST | Redirige al detalle del evento | Cumple | 12 |
| 11 | Inscribir asistente con ARL vigente | POST | Inscripción, código de acceso y cupo 2 → 1 | Cumple | 14 |
| 12 | Inscribir asistente con ARL vencida | POST | Rechaza por ARL | Cumple | 15 |
| 13 | Inscribir de nuevo al mismo asistente | POST | Rechaza por duplicado | Cumple | 16 |
| 14 | Inscribir al último cupo | POST | Cupo 0 y se oculta el formulario | Cumple | 17 |
| 15 | Consultar evento inexistente | GET | Página de error 404 | Cumple | 18 |

Usuarios de prueba: `1047500200` y `1047500300` (ARL vigente), `1047500400` (ARL vencida).

## Estándares de codificación aplicados

- Paquetes en minúscula por capa (`com.ecm.sigre.controlador`, `.servicio`, `.dao`, `.modelo`, `.util`).
- Clases en PascalCase, métodos y variables en camelCase, constantes en MAYÚSCULAS; nombres en español, acordes con el dominio.
- Javadoc en clases y métodos públicos relevantes.
- Consultas SQL solo con `PreparedStatement` (previene inyección SQL) y recursos JDBC cerrados con try-with-resources.
- Transacción con `SELECT ... FOR UPDATE` al inscribir, para que dos inscripciones simultáneas no superen el aforo.
- Texto del usuario escapado en las JSP (`esc()`) para prevenir XSS; credenciales fuera del código (`db.properties` / variables de entorno).
- Mensajes de commit con la convención Conventional Commits (`feat:`, `test:`, `docs:`, `chore:`).
