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
public class SearchResultDto extends SearchDto implements Cloneable{
    private String nombreComuna;

    @Override
    public SearchResultDto clone() throws CloneNotSupportedException{
        return (SearchResultDto) super.clone();
    }
}
