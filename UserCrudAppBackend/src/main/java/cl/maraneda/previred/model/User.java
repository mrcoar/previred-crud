package cl.maraneda.previred.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Date;

@Entity
@Table(name="usuario")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Comparable<User>{

    @Id
    @Column(name="rut", length = 10, nullable=false)
    private String rut;

    @Column(name="nombre", length=20, nullable=false)
    private String nombre;

    @Column(name="apellido", length=20, nullable=false)
    private String apellido;

    @Column(name="fecha_nacimiento", nullable=false)
    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;

    @Column(name="calle", length=50, nullable=false)
    private String calle;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="comuna", referencedColumnName = "id")
    private Comuna comuna;

    @Override
    public int compareTo(@NonNull User other){
        if(other.getRut() == null ||  other.getRut().isBlank()){
            throw new NullPointerException("No se puede comparar un usuario no nulo con uno nulo");
        }
        return rut.compareTo(other.getRut());
    }

    @Override
    public boolean equals(Object other){
        if(!(other instanceof User u)){
            return false;
        }
        return this.compareTo(u) == 0;
    }

    @Override
    public int hashCode(){
        return super.hashCode();
    }
}
