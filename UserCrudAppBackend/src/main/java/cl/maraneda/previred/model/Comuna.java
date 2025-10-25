package cl.maraneda.previred.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name="comuna")
@Data
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Comuna {
    @Id
    @Column(name="id")
    private Integer id;

    @Column(name="nombre", length=50, nullable=false)
    private String nombre;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="region_id", referencedColumnName = "id", foreignKey = @ForeignKey(name="none"))
    private Region region;
}
