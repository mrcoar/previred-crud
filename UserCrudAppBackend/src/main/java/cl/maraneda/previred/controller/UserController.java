package cl.maraneda.previred.controller;

import cl.maraneda.previred.dto.SearchDto;
import cl.maraneda.previred.dto.SearchResultDto;
import cl.maraneda.previred.dto.UpdateInputDto;
import cl.maraneda.previred.dto.UserDto;
import cl.maraneda.previred.exceptions.RUTException;
import cl.maraneda.previred.exceptions.RutRuntimeException;
import cl.maraneda.previred.service.ComunaService;
import cl.maraneda.previred.service.UserService;
import cl.maraneda.previred.util.CriterioBusquedaUsuario;
import cl.maraneda.previred.util.RUT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/previred/user")
public class UserController {
    @Autowired
    private transient UserService userService;

    @Autowired
    private transient ComunaService comunaService;

    private static final ResponseEntity<String> RESPUESTA_RUT_INVALIDO =
        ResponseEntity.badRequest().body("Formato de rut invalido o Rut no concuerda con digito verificador");

    private ResponseEntity<Object> buscarPorRut(String rut){
        if(rut==null || rut.isBlank()){
            return ResponseEntity.badRequest()
                    .body("El RUT es obligatorio para buscar usuarios por ese criterio");
        }
        try{
            return ResponseEntity.ok(List.of(userService.findById(new RUT(rut.trim()).toString())));
        }catch(RUTException | RutRuntimeException e){
            return ResponseEntity.badRequest()
                    .body("El valor ingresado no es un RUT valido: " + rut);
        }catch(IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    private ResponseEntity<Object> buscarPorNombreYApellido(String nombre, String apellido){
        if(nombre==null || nombre.isBlank() || apellido==null || apellido.isBlank()){
            return ResponseEntity.badRequest()
                    .body("El nombre y el apellido son obligatorios para buscar usuarios por ese criterio");
        }
        List<SearchResultDto> users = userService.findByNombreAndApellido(nombre, apellido);
        return users.isEmpty() ?
            new ResponseEntity<>("No se encontraron usuarios por nombre y apellido", HttpStatus.NOT_FOUND) :
            ResponseEntity.ok(users);
    }

    private ResponseEntity<Object> buscarPorOtrosCriterios(SearchDto input, CriterioBusquedaUsuario crit){
        String valor = switch(crit){
            case POR_NOMBRE -> input.getNombre();
            case POR_APELLIDO -> input.getApellido();
            case POR_REGION -> input.getRegion();
            case POR_COMUNA -> Optional.ofNullable(input.getComuna()).orElse(0).toString();
            default -> "?";
        };
        if(valor == null || valor.isBlank()){
            return ResponseEntity.badRequest().body("El valor es obligatorio para el criterio de busqueda especificado");
        }
        if((crit == CriterioBusquedaUsuario.POR_REGION || crit == CriterioBusquedaUsuario.POR_COMUNA) &&
           valor.equals("0")){
            return ResponseEntity.badRequest().body("El valor es obligatorio para el criterio de busqueda especificado");
        }
        try {
            List<SearchResultDto> users = userService.findByCriterio(valor, crit);
            if(users.isEmpty()){
                return new ResponseEntity<>("No se encontraron usuarios por el criterio especificado", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(users);
        }catch(NoSuchMethodException e){
            return ResponseEntity.badRequest().body("Criterio de busqueda no reconocido");
        }catch(InvocationTargetException e){
            return ResponseEntity.internalServerError()
                .body("Error interno al buscar por el criterio " + crit.toString().replace("_", "") + ": " + e.getCause());
        }catch(IllegalAccessException e){
            return ResponseEntity.internalServerError()
                    .body("Acceso ilegal al buscar por el criterio especificado " + crit.toString().replace("_", "") + ": " + e);
        }
    }

    private ResponseEntity<Object> buscarTodos(){
        List<SearchResultDto> users = userService.findAll();
        if(users.isEmpty()){
            return new ResponseEntity<>("No se encontraron usuarios", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(users);
    }

    @PostMapping(value="/search",
                 consumes=MediaType.APPLICATION_JSON_VALUE,
                 produces={MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<Object> buscar(@RequestBody SearchDto filter){
        try {
            CriterioBusquedaUsuario crit = CriterioBusquedaUsuario.valueOf(filter.getCriteria());
            return switch(crit){
                case TODOS -> this.buscarTodos();
                case POR_RUT -> this.buscarPorRut(filter.getRut());
                case POR_NOMBRE_COMPLETO -> this.buscarPorNombreYApellido(filter.getNombre(), filter.getApellido());
                default -> this.buscarPorOtrosCriterios(filter, crit);
            };
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest()
                                 .body(String.format("Criterio no reconocido: %s", filter.getCriteria()));
        }
    }

    @PutMapping(value="/",
                consumes=MediaType.APPLICATION_JSON_VALUE,
                produces=MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> guardar(@RequestBody UserDto user){
        if(user==null || user.getNombre()==null || user.getNombre().isBlank() ||
           user.getApellido()==null || user.getApellido().isBlank() ||
           user.getRut()==null || user.getRut().isBlank() ||
           user.getCalle()==null || user.getCalle().isBlank() ||
           user.getComuna().equals(0)){
            return ResponseEntity.badRequest().body("Todos los campos son obligatorios");
        }
        try{
            new RUT(user.getRut());
            return userService.save(user) ?
                ResponseEntity.status(HttpStatus.CREATED).body("Usuario guardado exitosamente") :
                ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("No se pudo guardar el usuario por razones desconocidas");
        }catch(RUTException | RutRuntimeException e){
            return RESPUESTA_RUT_INVALIDO;
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PutMapping(value="/{rut}",
            consumes=MediaType.APPLICATION_JSON_VALUE,
            produces=MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> actualizar(@RequestBody UpdateInputDto user, @PathVariable("rut")String rut){
        if(user==null || user.getNombre()==null || user.getNombre().isBlank() ||
                user.getApellido()==null || user.getApellido().isBlank() ||
                user.getCalle()==null || user.getCalle().isBlank() ||
                user.getFechaNacimiento()==null || user.getFechaNacimiento().isBlank() ||
                rut == null || rut.isBlank() || user.getComuna().equals("0")){
            return ResponseEntity.badRequest().body("Todos los campos son obligatorios");
        }
        try{
            new RUT(rut.trim());
            UserDto u = UserDto.builder()
                               .rut(rut.trim())
                               .nombre(user.getNombre().trim())
                               .apellido(user.getApellido().trim())
                               .calle(user.getCalle().trim())
                               .comuna(Integer.parseInt(user.getComuna()))
                               .fechaNacimiento(user.getFechaNacimiento())
                               .build();
            return userService.update(u) ?
                    ResponseEntity.ok("Usuario actualizado exitosamente") :
                    ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("No se pudo actualizar el usuario por razones desconocidas");
        }catch(RUTException | RutRuntimeException e){
            return RESPUESTA_RUT_INVALIDO;
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping(value="/{rut}",
            produces=MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> eliminar(@PathVariable("rut") String rut){
        if(rut==null || rut.isBlank()){
            return ResponseEntity.badRequest().body("El RUT es obligatorio");
        }
        try {
            new RUT(rut.trim());
            return userService.delete(rut) ?
                    ResponseEntity.ok("Usuario eliminado exitosamente") :
                    ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("No se pudo eliminar el usuario por razones desconocidas");
        }catch(RUTException | RutRuntimeException e){
            return RESPUESTA_RUT_INVALIDO;
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
