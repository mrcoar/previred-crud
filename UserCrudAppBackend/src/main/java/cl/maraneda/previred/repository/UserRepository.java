package cl.maraneda.previred.repository;

import cl.maraneda.previred.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    List<User> findByNombre(String nombre);
    List<User> findByApellido(String apellido);

    List<User> findByNombreAndApellido(String nombre, String apellido);

    @Query("SELECT u FROM User u WHERE u.comuna.region.id=:id")
    List<User> findByRegion(@Param("id") String id);

    @Query("SELECT u FROM User u WHERE u.comuna.id=CAST(:id AS INTEGER)")
    List<User> findByComuna(@Param("id") String id);

    @Query("DELETE FROM User u WHERE u.nombre='Test'")
    @Transactional
    @Modifying
    void deleteTestUsers();
}
