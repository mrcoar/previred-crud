package cl.maraneda.previred.service;

import cl.maraneda.previred.dto.SearchDto;
import cl.maraneda.previred.dto.SearchResultDto;
import cl.maraneda.previred.dto.UserDto;
import cl.maraneda.previred.model.User;
import cl.maraneda.previred.repository.ComunaRepository;
import cl.maraneda.previred.repository.UserRepository;
import cl.maraneda.previred.util.CriterioBusquedaUsuario;
import cl.maraneda.previred.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Service
public class UserService {
    private transient final UserRepository userRepository;
    private transient final ComunaRepository comunaRepository;

    @Autowired
    public UserService(UserRepository repository, ComunaRepository crepository){
        userRepository = repository;
        comunaRepository = crepository;
    }

    public List<SearchResultDto> findAll(){
        return Util.mapToSearchDto(userRepository.findAll());
    }
    public List<SearchResultDto> findByNombreAndApellido(String nombre, String apellido){
        return Util.mapToSearchDto(userRepository.findByNombreAndApellido(nombre, apellido));
    }

    @SuppressWarnings("unchecked")
    public List<SearchResultDto> findByCriterio(String valor, CriterioBusquedaUsuario criterio) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return Util.mapToSearchDto(
            (List<User>) UserRepository.class
                                       .getMethod("find" + criterio.getValue(), String.class)
                                       .invoke(userRepository, valor));
    }

    public boolean save(UserDto userDto){
        if(userRepository.existsById(userDto.getRut())){
            throw new IllegalArgumentException("El usuario ya existe");
        }
        userRepository.saveAndFlush(
            Util.mapToUser(
                userDto,
                comunaRepository.findById(
                    userDto.getComuna()).orElseThrow(
                        Util::throwUnexistingCommunaException)));
        return userRepository.existsById(userDto.getRut());
    }

    public boolean update(UserDto userDto){
        if(!userRepository.existsById(userDto.getRut())){
            throw Util.throwUnexistingUserException();
        }
        return Util.mapToUserDto(
            userRepository.saveAndFlush(
                Util.mapToUser(
                    userDto,
                    comunaRepository.findById(userDto.getComuna()).orElseThrow(Util::throwUnexistingCommunaException)
                )
            )
        ).equals(userDto);
    }

    public boolean delete(String rut){
        if(!userRepository.existsById(rut)){
            throw Util.throwUnexistingUserException();
        }
        userRepository.deleteById(rut);
        return !userRepository.existsById(rut);
    }

    public SearchResultDto findById(String rut){
        return Util.mapToSearchDto(List.of(
            userRepository.findById(rut)
                          .orElseThrow(Util::throwUnexistingUserException))).getFirst();
    }

    public void deleteTestUsers(){
        userRepository.deleteTestUsers();
    }
}
