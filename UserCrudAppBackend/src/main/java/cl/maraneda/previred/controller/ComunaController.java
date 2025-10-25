package cl.maraneda.previred.controller;

import cl.maraneda.previred.dto.ComunaDto;
import cl.maraneda.previred.model.Comuna;
import cl.maraneda.previred.repository.RegionRepository;
import cl.maraneda.previred.service.ComunaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/previred/comuna")
public class ComunaController {
    @Autowired
    private transient ComunaService comunaService;

    @Autowired
    private transient RegionRepository regionRepository;

    @GetMapping(value="/porRegion/{regionId}",
                produces={MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<List<ComunaDto>> obtieneComunasPorRegion(@PathVariable("regionId") String regionId){
        if(regionId == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        List<ComunaDto> comunasPorRegion = comunaService.findByRegion(regionId);
        if(comunasPorRegion == null || comunasPorRegion.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        comunasPorRegion.addFirst(ComunaDto.builder().id(0).nombre("Seleccione una comuna").build());
        return ResponseEntity.ok(comunasPorRegion);
    }

    @GetMapping(value="/regionDeComuna/{comunaId}", produces=MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> obtieneRegionDeComuna(@PathVariable Integer comunaId){
        try {
            Comuna c = comunaService.findOne(comunaId);
            if(c.getRegion() != null){
                return ResponseEntity.ok(c.getRegion().getId());
            }else{
                String rid = comunaService.getRegionId(comunaId);
                if(rid == null){
                    throw new IllegalArgumentException("Comuna asociada a una region inexistente");
                }
                return ResponseEntity.ok(rid);
            }
        }catch(IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
