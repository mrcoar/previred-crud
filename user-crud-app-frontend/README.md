# Proyecto previred-crud: frontend

## ¿En qué consiste?

El frontend para previred-crud consiste en una aplicación TypeScript con React y Vite que implementa un mantenedor de usuarios. 

El frontend fue implementado bajo el supuesto de que las funcionalidades para crear/buscar/modificar/eliminar usuarios están en una misma página y de que no se requiere un diseño especial para la UI del sitio, y de que se usan puertos de conexión por defecto, tanto para el frontend como para el backend.

La única dependencia para poder ejecutar el frontend es Node.js y npm. [Aquí](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm) están las instrucciones en inglés para instalar ambos programas y para agregar npm a la variable de entorno PATH, dependiendo de su sistema operativo.

## Instalación

En línea de comandos, una vez instalados node.js y npm, vaya a la carpeta user-crud-app-frontend y ejecute ```npm install```. Esto descargará todas las dependencias especificadas en un archivo de ese directorio llamado package.json (**No editar ese archivo a menos que sea estrictamente necesario y solo si usted sabe lo que hace**)

Si esto no funciona, usted puede crear el frontend desde cualquier directorio ejecutando ```npm create vite@latest user-crud-app-frontend -- --template react-ts```. Luego copie el directorio user-crud-app-frontend/src (desde donde usted descargó previred-crud usando git) hasta el nuevo directorio y luego repita el paso del párrafo anterior.

## Ejecución

Una vez descargadas todas las dependencias, simplemente ejecute ```npm run dev```
**ADVERTENCIA**: Se recomienda fuertemente ejecutar primero el backend y luego el frontend.

Para acceder al sitio generado  por el frontend, usando su explorador favorito, ingrese la siguiente URL: http://localhost:5173/
