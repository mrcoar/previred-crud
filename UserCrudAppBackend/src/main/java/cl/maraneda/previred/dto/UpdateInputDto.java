package cl.maraneda.previred.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateInputDto {
    private String nombre;
    private String apellido;
    private String calle;
    private String comuna;
    private String fechaNacimiento;
}
