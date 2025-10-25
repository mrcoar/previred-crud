package cl.maraneda.previred.util;

import lombok.Getter;

@Getter
public enum CriterioBusquedaUsuario {
    TODOS("All"),
    POR_NOMBRE("ByNombre"),
    POR_APELLIDO("ByApellido"),
    POR_NOMBRE_COMPLETO("ByNombreAndByApellido"),
    POR_COMUNA("ByComuna"),
    POR_REGION("ByRegion"),
    POR_RUT("ByRut");

    private final String value;

    private CriterioBusquedaUsuario(String val){
        value = val;
    }

}
