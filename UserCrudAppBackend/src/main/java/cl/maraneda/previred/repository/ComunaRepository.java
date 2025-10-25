package cl.maraneda.previred.repository;

import cl.maraneda.previred.model.Comuna;
import cl.maraneda.previred.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ComunaRepository extends JpaRepository<Comuna, Integer> {
    @Query("SELECT c FROM Comuna c WHERE c.region.id=:region ORDER BY c.nombre ASC")
    List<Comuna> findByRegion(@Param("region") String region);

    @Query("DELETE FROM Comuna c WHERE c.region.id='XXIII'")
    @Transactional
    @Modifying
    void deleteTestComunas();

    @Query("SELECT c.region.id FROM Comuna c WHERE c.id = :id")
    String findRegionId(@Param("id") Integer comunaId);
}
