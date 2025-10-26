# Projecto previred-crud

## ¿Qué es previred-crud?

Es un proyecto que consiste en un frontend y un backend para el "coding challenge" solicitado por la empresa _Previred_. Se llama previred-crud porque consiste en la implementación de un CRUD (Create, Read, Update, Delete) o, como se dice en el buen español, un mantendor. En este caso, un mantenedor de usuarios.

El desafío estipula que cada usuario debe tener la siguiente información:

* RUT (Rol único tributario)
* Nombre
* Rut
* Fecha de Nacimiento
* Dirección:
  * Calle
  * Comuna
  * Región

Además, el desafío también exige que el proyecto consista en una parte frontend y una parte backend, que tienen su propio README.md

Este proyecto fue desarrollado en base a los siguientes supuestos:

* El RUT debe consistir en un número entero positivo menor que 100000000, sin puntos, con guion y dígito verificador.
* Se debe validar que el número del RUT concuerde con el dígito verificador (refiérase a [este enlace](https://es.wikipedia.org/wiki/Rol_%C3%9Anico_Tributario) para más detalles)
* Cada usuario cuenta con una única dirección.
* Se existir consistencia entre una comuna y la región a la que pertenece. Por ejemplo, si el usuario pertenece a la Región de Arica y Parinacota, la comuna donde el usuario habita solo puede ser una que pertenece a esa region, es decir, una de las siguientes: Arica, Camarones, Putre, General Lagos.
* Todos los datos son requeridos
* La región asociada a un usuario no se guarda en la base de datos porque solo basta con la comuna. Sin embargo:
  * Es necesario especificar la región para poder elegir la comuna, debido a lo explicado en el supuesto anterior.
  * Es necesario que todas las regiones estén disponibles para poder escoger una.

Para poder descargar el proyecto completo, asumiendo que se usará solamente línea de comandos, se deben seguir los siguientes pasos:

1. Descargue e instale [Git](https://git-scm.com/downloads) (use la versión correspondiente a su sistema operativo)
2. Abra una ventana de línea de comandos (terminal para UNIX, Símbolo de sistema para Windows)
3. En línea de comandos, diríjase a la carpeta donde desea descargar el proyecto (usando md y/o cd en el caso de Windows o mkdir y/o cd en el caso de UNIX)
4. Estando en la carpeta deseada, ejecute ```git clone --branch main https://github.com/mrcoar/previred-crud.git```. Esto descargará el frontend y el backend junto con este archivo en una carpeta llamada previred-crud
5. Entre al directorio previred-crud usando el comando ```cd previred-crud```
6. Siga las instrucciones de los README.md correspondientes al frontend y al backend, empezando por el backend.

