package cl.maraneda.previred.util;

import cl.maraneda.previred.dto.SearchResultDto;
import cl.maraneda.previred.dto.UserDto;
import cl.maraneda.previred.model.Comuna;
import cl.maraneda.previred.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Util {
    public static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat OUTPUT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    public static UserDto mapToUserDto(User item){
        return UserDto.builder()
                .nombre(item.getNombre())
                .apellido(item.getApellido())
                .rut(item.getRut())
                .fechaNacimiento(
                        String.format(
                                "%s (%d) años",
                                INPUT_DATE_FORMAT.format(item.getFechaNacimiento()),
                                getYears(item.getFechaNacimiento())
                        ))
                .calle(item.getCalle())
                .comuna(item.getComuna().getId())
                .build();
    }
    public static User mapToUser(UserDto input, Comuna c){
        try{
           return User.builder()
                      .rut(input.getRut())
                   .nombre(input.getNombre())
                   .apellido(input.getApellido())
                   .fechaNacimiento(INPUT_DATE_FORMAT.parse(input.getFechaNacimiento()))
                   .calle(input.getCalle())
                   .comuna(c)
                   .build();
        }catch(NumberFormatException e){
            throw new IllegalArgumentException("Identificador de comuna no reconocido");
        }catch(ParseException e){
            throw new IllegalArgumentException("Formato de fecha distinto de yyyy-MM-dd");
        }
    }

    public static List<SearchResultDto> mapToSearchDto(List<User> input){
        return input.stream()
                    .map(item -> {
                        return SearchResultDto.builder()
                                        .nombre(item.getNombre())
                                        .apellido(item.getApellido())
                                        .rut(item.getRut())
                                        .fechaNacimiento(
                                          String.format(
                                            "%s (%d años)",
                                            OUTPUT_DATE_FORMAT.format(item.getFechaNacimiento()),
                                            getYears(item.getFechaNacimiento())
                                          ))
                                        .calle(item.getCalle())
                                        .nombreComuna(item.getComuna().getNombre())
                                        .comuna(item.getComuna().getId())
                                        .region(item.getComuna().getRegion().getId())
                                        .build();
                    })
                    .collect(Collectors.toList());
    }

    public static IllegalArgumentException throwUnexistingUserException(){
        return new IllegalArgumentException("El usuario no existe");
    }

    public static IllegalArgumentException throwUnexistingCommunaException(){
        return new IllegalArgumentException("La comuna especificada no existe");
    }


    public static int getYears(Date d){
        return Period.between(
                    LocalDate.ofInstant(Instant.ofEpochMilli(d.getTime()), ZoneId.systemDefault()),
                    LocalDate.now(ZoneId.systemDefault()).plusDays(1L))
                .getYears();
    }

    public static String getRamdomRut(){
        int num = new Random().ints(1, 100000000).findFirst().orElse(2);
        return num + "-" + RUT.getDigito(num);
    }
}
