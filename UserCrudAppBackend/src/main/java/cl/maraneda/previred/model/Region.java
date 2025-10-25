package cl.maraneda.previred.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="region")
@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Region {
    @Id
    @Column(name="id", length=8, nullable=false)
    private String id;

    @Column(name="nombre", length=60, nullable=false)
    private String nombre;

    @Column(name="orden", nullable=false)
    private Integer orden;

    @Builder.Default
    @OneToMany(mappedBy="region", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch= FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)  // Hibernate-specific: Forces orphan delete
    private List<Comuna> comunas = new ArrayList<>();
}
