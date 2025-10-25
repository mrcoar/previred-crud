package cl.maraneda.previred.service;

import cl.maraneda.previred.dto.RegionDto;
import cl.maraneda.previred.model.Comuna;
import cl.maraneda.previred.model.Region;
import cl.maraneda.previred.repository.RegionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegionService {
    private transient final RegionRepository regionRepository;
    private transient final ModelMapper modelMapper;

    @PersistenceContext  // For manual flush if needed
    private transient EntityManager entityManager;

    @Autowired
    public RegionService(RegionRepository repository, ModelMapper mapper){
        regionRepository = repository;
        modelMapper = mapper;
    }

    public List<RegionDto> findAllOrdered(){
        return regionRepository.findAll(Sort.by(Sort.Direction.ASC, "orden"))
                               .stream()
                               .map(r -> modelMapper.map(r, RegionDto.class))
                               .collect(Collectors.toList());
    }

    @Transactional
    public void deleteRegion(String regionId) {
        // Step 1: Fetch with eager comunas to initialize
        Region region = regionRepository.findByIdWithComunas(regionId)
                .orElseThrow(() -> new EntityNotFoundException("Region not found: " + regionId));

        // Step 2: Sync bidirectional and orphan (nullify owning FKs)
        for (Comuna comuna : region.getComunas()) {
            comuna.setRegion(null);  // Nullify owning side FK
        }
        region.getComunas().clear();  // Calls setRegion(null) on each comuna

        // Step 3: Flush to persist orphans as deleted
        entityManager.flush();  // Ensures DELETEs for comunas before region delete

        // Step 4: Delete region (now safe)
        regionRepository.delete(region);
    }
}
