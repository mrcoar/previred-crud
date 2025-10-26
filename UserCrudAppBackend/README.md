# Proyecto previred-crud: BACKEND

## ¿En qué consiste?

Un backend es una aplicación, o un componente de una, encargada de la "capa de control" y la "capa de datos" del proyecto. La idea es interactuar con el frontend para enviar o recibir datos.
Para el caso de previred-crud, el backend consiste en una aplicación Spring Boot desarrollada con Java que contiene varios API-REST (Web services tipo REST) para interactuar con el frontend.

Además de los supuestos genéricos del proyecto, el backend opera bajo los siguientes supuestos:

* La base de datos con la que se trabajará es una "en memoria". Esto significa que la base de datos y todos los datos que contiene estarán disponibles mientras la aplicación esté activa. Si por alguna razón (programada o no), la aplicación deja de funcionar, la base de datos dejará de estar disponible y todos los datos guardados en ella se perderán.
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
5. Diríjase a la carpeta UserCrudAppBackend
