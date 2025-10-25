package cl.maraneda.previred.repository;

import cl.maraneda.previred.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, String>{
    @Query("SELECT r FROM Region r LEFT JOIN FETCH r.comunas WHERE r.id = :id")
    Optional<Region> findByIdWithComunas(@Param("id") String id);
}
