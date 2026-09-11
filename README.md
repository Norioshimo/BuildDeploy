# BuildDeploy

Herramienta de escritorio (**Java 8 + Swing**) que automatiza la **compilacion y el despliegue de un WAR** en **WildFly** o **GlassFish**, con soporte para **Maven** y **Ant**, y gestion del ciclo de vida del servidor (iniciar / detener / reiniciar / estado).

## Caracteristicas

- Compila con **Maven** (`mvn clean install`) o **Ant** (`ant -f build.xml <target>`).
- Despliega en:
  - **WildFly** (carpeta `standalone/deployments` + archivos marcadores).
  - **GlassFish** por **autodeploy** o por **asadmin deploy**.
- Controla el servidor desde la UI: **Iniciar**, **Detener**, **Reiniciar**, **Estado**.
- Opcion configurable por proyecto: **detener antes** y/o **reiniciar despues** del deploy.
- Persiste las configuraciones en **SQLite** (`configdb/build_deploy_config.db`).
- Migra automaticamente una base **H2** heredada si existe.
- Registra todo en `logs/app.log` (rotacion diaria).

## Requisitos

- **JDK 8** (o superior compatible).
- **Maven 3** para compilar el proyecto y/o para las configuraciones que usen Maven.
- **Ant** instalado (solo si se compila con Ant).
- **WildFly** y/o **GlassFish** instalados para el despliegue.
- Variables de entorno recomendadas: `JAVA_HOME` (y `ANT_HOME` si aplica).

## Compilar el proyecto

```bash
mvn clean package
```

Genera el fat-jar (ejecutable):

```
target/BuildDeployFinal.jar
```

## Ejecutar

```bash
java -jar target/BuildDeployFinal.jar
```

Carpetas creadas en tiempo de ejecucion:

- `logs/` — `app.log` y logs de consola de servidores.
- `configdb/` — base de datos SQLite.

## Uso de la aplicacion

La interfaz usa un tema plano moderno con menu de tarjetas:

1. **CONFIGURACION**: crear/editar/eliminar una configuracion de proyecto (secciones Proyecto, Compilacion, Servidor y Automatizacion).
2. **EJECUTAR**: seleccionar la configuracion, controlar el servidor (Iniciar / Detener / Reiniciar / Estado), ver la **consola en vivo** y pulsar **Procesar**.
3. **VER LOGS**: visor con selector de fuente (aplicacion, servidor o consola de arranque), auto-refresh y apertura de carpeta.
4. **AYUDA**: ejemplos de configuracion.

### Ver logs desde la aplicacion

- **Consola en vivo** (pantalla Ejecutar): muestra en tiempo real las acciones de la app y la salida de `mvn`, `ant`, `asadmin` y `jboss-cli`.
- **Visor de logs** (pantalla Ver Logs): permite elegir la fuente:
  - **Aplicacion** → `logs/app.log`
  - **Servidor** → WildFly `<serverHome>/standalone/log/server.log` o GlassFish `<serverHome>/domains/<domain>/logs/server.log`
  - **Consola de arranque** → `logs/wildfly-consola.log`
- Botones **Refrescar**, **Auto-refresh**, **Abrir carpeta** y **Limpiar vista**.

> Nota: el log de la aplicacion no es el log interno del servidor; son fuentes distintas que el visor permite seleccionar.

---

## Configuracion para WildFly

| Campo | Ejemplo |
|---|---|
| Herramienta de build | Maven o Ant |
| Directorio de compilacion | `C:\proyecto\target` |
| Nombre de WAR | `mi-webapp.war` |
| Servidor | WildFly |
| Deployments de WildFly | `C:\wildfly-26\standalone\deployments` |
| Carpeta del servidor (server home) | `C:\wildfly-26` |

El deploy copia el WAR y crea `<war>.dodeploy`; la app espera `<war>.deployed` (exito) o `<war>.failed` (error).

Para **iniciar/detener** WildFly se usan `bin/standalone.bat` (Windows) o `bin/standalone.sh` (Linux) y `bin/jboss-cli` para el shutdown.

## Configuracion para GlassFish

| Campo | Ejemplo |
|---|---|
| Servidor | GlassFish |
| Carpeta autodeploy (*) | `C:\glassfish\glassfish\domains\domain1\autodeploy` |
| Modo de deploy | `Autodeploy (carpeta)` o `asadmin (comando)` |
| Ruta de asadmin | `C:\glassfish\glassfish\bin\asadmin.bat` |
| Host admin / Puerto admin | `localhost` / `4848` |
| Usuario / Password admin | credenciales de administracion |
| Dominio GlassFish | `domain1` |
| Carpeta del servidor (server home) | `C:\glassfish\glassfish` |

- **Autodeploy**: deja el WAR en la carpeta `autodeploy`; la app espera `<war>_deployed` (exito) o `<war>_deploymentfailed` (error).
- **asadmin**: ejecuta `asadmin deploy --force=true --host ... --port ... --user ... --passwordfile ...`.

> Las credenciales de asadmin se guardan en la base de datos SQLite.

## Configuracion para Ant

| Campo | Ejemplo |
|---|---|
| Herramienta de build | Ant |
| Directorio de Ant | `C:\ant\bin\ant.bat` (vacio usa `ant` del PATH) |
| Archivo build.xml (*) | `C:\proyecto\build.xml` |
| Target de Ant | `dist` |
| Directorio de compilacion | `C:\proyecto\dist` |
| Nombre de WAR | `mi-webapp.war` |

La app ejecuta `ant -f <build.xml> <target>`. El WAR debe quedar en el directorio de compilacion configurado.

## Base de datos

- Motor: **SQLite** embebido.
- Archivo: `configdb/build_deploy_config.db`.
- Tabla: `configuraciones`.
- Migracion: si existe `configdb/build_deploy_config.mv.db` (H2) y SQLite esta vacia, se migran los datos automaticamente y el archivo H2 se renombra a `.migrado`.

## Logs

- Configuracion en `src/main/resources/log4j2.xml`.
- Archivo: `logs/app.log` (rotacion diaria `app-AAAA-MM-DD.log`, maximo 10 archivos).
- Consola de servidores: `logs/wildfly-consola.log`.
- Visibles desde la app: consola en vivo (pantalla Ejecutar) y visor multi-fuente (pantalla Ver Logs).

## Estructura del proyecto

```
src/main/java/nsg/portafolio
├── controller/    EjecutarController, ServerController
├── dao/           ConfiguracionDAO
├── db/            ConfigDB (SQLite), MigradorH2ASQLite
├── enums/         BuildTool, AppServer, DeployMode
├── model/         Configuracion
├── service/       BuildStrategy, DeployStrategy, ServerLifecycleStrategy, EstrategiaFactory
│   └── impl/      Maven/Ant, WildFly/GlassFish, ciclo de vida
├── formulario/    PrincipalFrm, ConfiguracionFrm, EjecutarFrm, AyudaFrm
├── ui/            UITheme, BaseFrm, BotonPlano, TarjetaBoton, Iconos, RoundedBorder,
│                  TextAreaAppender, LogFrm, App
└── utiles/        ProcesoUtil, Sonidos
```

## Tareas pendientes

- [ ] Editor de configuraciones de servidor remoto.
- [ ] Empaquetado nativo (instalador).

