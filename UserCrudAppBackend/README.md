# Proyecto previred-crud: BACKEND

## ¿En qué consiste?

Un backend es una aplicación, o un componente de una, encargada de la "capa de control" y la "capa de datos" del proyecto. La idea es interactuar con el frontend para enviar o recibir datos.
Para el caso de previred-crud, el backend consiste en una aplicación Spring Boot (versión 3.5.6, requerida por Previred) desarrollada con Java que contiene varios API-REST (Web services tipo REST) para interactuar con el frontend.

Además de los supuestos genéricos del proyecto, el backend opera bajo los siguientes supuestos:

* La base de datos con la que se trabajará es un motor [H2](https://www.h2database.com/html/main.html) "en memoria". Esto significa que la base de datos y todos los datos que contiene estarán disponibles mientras la aplicación esté activa. Si por alguna razón (programada o no), la aplicación deja de funcionar, la base de datos dejará de estar disponible y todos los datos guardados en ella se perderán.
* El backend debe contar con pruebas de integración para corroborar no solo que las llamadas a los API-REST hagan lo que deban, sino que también no hagan lo que no deban.
* Debido a que el frontend debe usar un framework basado en TypeScript (véase el README.md para el frontend), el backend debe tener habilitado CORS (Compartimiento de recursos de origen cruzado, refiérase [aquí](https://es.wikipedia.org/wiki/Intercambio_de_recursos_de_origen_cruzado) para más detalles.

##Instalación

Para instalar el backend, es necesario instalar las siguientes dependencias:

* [Gradle 9.1 o superior](https://gradle.org/releases/#9.1.0)
* [OpenJDK 22 o superior](https://jdk.java.net/archive/) _Nota: OpenJDK se puede sustituir por [Oracle JDK](https://www.oracle.com/java/technologies/downloads/archive/)_

Una vez descargadas las dependencias, siga los siguientes pasos:

1. Agregue la variable de entorno JAVA_HOME siguiendo las instrucciones correspondientes a su sistema operativo. Su valor debe ser la ruta absoluta donde se instaló el JDK 22.
2. Agregue la ruta absoluta de la carpeta bin que está dentro del directorio que es valor de la variable de entorno JAVA_HOME a la variable de entorno PATH, nuevamente siguiendo las instrucciones correspondientes a su sistema operativo.
3. Agregue la variable de entorno GRADLE_USER_HOME siguiendo las instrucciones correspondientes a su sistema operativo. Su valor debe ser la ruta absoluta donde se instaló Gradle 9.1
4. A partir de este punto, las instrucciones se deben hacer en línea de comandos (Símbolo de Sistema o Terminal o Consola).
5. Diríjase a la carpeta UserCrudAppBackend usando el comando cd
6. Asegúrese que los siguientes archivos estén presentes usando dir o ls dependiendo del sistema operativo:
  * gradlew.bat (en Windows) o gradlew (en UNIX)
  * gradle/wrapper/gradle-wrapper.jar
  * gradle/wrapper/gradle-wrapper.properties
7. Si gradlew.bat o gradlew están presentes, pero gradle-wrapper.jar y gradle-wrapper.properties no lo están, usted puede crearlos ejecutando ```gradle wrapper```
8. Ejecute ```gradlew build```. Esto descargará todas las dependencias, realizará la revisión de buenas prácticas de programación, realizará las pruebas unitarias y de integración y, en caso de éxito, creará una carpeta llamada libs conteniendo dos archivos .jar: UserCrudAppBackend-1.0-SNAPSHOT.jar y UserCrudAppBackend-1.0-SNAPSHOT-plain.jar

## Dependencias:

Además de JDK 22, el backend utiliza las siguientes librerías: 

* Flyway 10.0.0 para ejecutar scripts SQL de manera automática de la forma que se indica en la siguiente sección.
* H2 (véase más arriba para más detalles), versión 2.22.220, versión mínima requerida por Flyway para evitar errores al ejecutar los scripts.
* Lombok, para crear código fuente de manera automática en tiempo de ejecución para clases POJO (Plain Old Java Object)
* PMD y SonarCube para asegurar buenas prácticas de programación en tiempo de compilación.
* JUnit y Mockito para pruebas de integración y pruebas unitarias 

## Ejecución
Para ejecutar la aplicación, diríjase, desde línea de comandos, al directorio UserCrudAppBackend/libs y ejecute lo siguiente:

* EN WINDOWS: java -jar UserCrudAppBackend-1.0-SNAPSHOT.jar
* EN UNIX: <ruta_jdk_22>/bin/java -jar UserCrudAppBackend-1.0-SNAPSHOT.jar

Esto no solo mantendrá la aplicación Spring Boot "stand-by" esperando que sus API-REST sean invocados, sino que también levantará la base de datos. Además, si es primera vez que se ejecuta, una vez levantada la base de datos, se ejecutarán los scripts SQL que están dentro de la aplicación de manera automática.

## Cambio de motor de base de datos
Si usted desea dejar de usar H2 y empezar a usar una base de datos persistente, como MySQL, siga los siguientes pasos (**sólo si usted sabe lo que hace**)

1. Modifique el archivo UserCrudAppBackend/build.gradle reemplazando la siguiente línea: ```runtimeOnly 'com.h2database:h2:2.2.220'``` por la línea correspondiente al motor de base de datos deseado. Consulte en el sitio oficial de Maven para el driver de conexión a la base de datos deseada para más detalles.
2. Crea la base de datos users en el motor deseado de base de datos.
3. Asegúrese que la URL de la base de datos sea accesible desde su aplicación
4. Realice las siguientes modificaciones al archivo UserCrudAppBackend/src/user/main/resources/application.yml:
* Reemplaze los valores de spring.datasource.url, spring.datasource.driverClassName, spring.datasource.username y spring.datasource.password por valores correctos dependiendo de las credenciales existentes en el motor instalado y 
* De ser necesario, agregue propiedades adicionales (revise documentación pertinente)
5. Finalmente, ejecute nuevamente ```gradlew build``` y luego ejecute la aplicación resultante.
