package cl.maraneda.previred.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Cloneable{
    protected String rut;
    protected String nombre;
    protected String apellido;
    protected String calle;
    protected String fechaNacimiento;
    protected Integer comuna;

    @Override
    public UserDto clone() throws CloneNotSupportedException {
        return (UserDto) super.clone();
    }

    @Override
    public boolean equals(Object other){
        if(other instanceof UserDto uother){
            return this.getRut().equals(uother.getRut()) &&
                   this.getNombre().equals(uother.getNombre()) &&
                   this.getApellido().equals(uother.getApellido()) &&
                    (this.getFechaNacimiento().equals(uother.getFechaNacimiento()) || this.getFechaNacimiento().startsWith(uother.getFechaNacimiento())) &&
                   this.getCalle().equals(uother.getCalle()) &&
                   this.getComuna().equals(uother.getComuna());
        }
        return false;
    }

    @Override
    public int hashCode(){
        return super.hashCode();
    }
}
