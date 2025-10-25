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
@EqualsAndHashCode(callSuper=false)
public class SearchDto extends UserDto implements Cloneable{
    private String criteria;
    private String region;

    public SearchDto clone() throws CloneNotSupportedException {
        return (SearchDto) super.clone();
    }
}