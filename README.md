# Proyecto: **Despliegue Automatizado WAR con Java Swing**

Este proyecto está desarrollado en **Java 8** con una interfaz gráfica construida con **Swing**. Tiene como objetivo automatizar la compilación y despliegue de una aplicación web empaquetada como `.war` en el servidor **WildFly**.

## Características principales

- Utiliza **Maven** para compilar y generar el archivo `.war`.
- El archivo `.war` generado se copia automáticamente al directorio de despliegue de **WildFly**, lo que permite que este lo detecte y lo levante automáticamente.
- Guarda los **logs** de ejecución en la carpeta `logs`.
- Crea y almacena la **base de datos H2** en el directorio `configdb`.
- Incluye una interfaz de **configuración** desde la aplicación Swing.

## Estructura de la configuración

- **Nombre del proyecto:** `deploy-webapp-swing`
- **Directorio del POM de Maven:** raíz del proyecto (`/deploy-webapp-swing/pom.xml`)
- **Directorio de compilación:** `/deploy-webapp-swing/target/`
- **Nombre del archivo WAR generado:** `mi-webapp.war`
- **Ruta del bin de Maven:** `/usr/share/maven/bin/mvn` (o la que corresponda a tu sistema)
- **Carpeta de despliegue de WildFly:** `/opt/wildfly/standalone/deployments/`

## Ejecución del build & deploy

El proyecto acepta parámetros al momento de ejecutar para realizar el proceso de compilación y despliegue, como:

- Ruta del bin de Maven
- Ruta del `pom.xml`
- Ruta de destino para el archivo `.war` en WildFly

Ejemplo de ejecución (desde consola o ejecutable):

```bash
java -jar deploy-webapp-swing.jar --maven-bin "/ruta/mvn" --pom "/ruta/pom.xml" --deploy-dir "/wildfly/deployments/"
```

## Tareas pendientes

- [ ] Detectar si **WildFly** se encuentra en ejecución.
- [ ] Detener **WildFly** antes del despliegue.
- [ ] Otras tareas que se agregarán según las necesidades futuras del flujo de despliegue.