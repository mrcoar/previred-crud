package cl.maraneda.previred.controller;

import cl.maraneda.previred.dto.RegionDto;
import cl.maraneda.previred.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/previred/region")
public class RegionController {
    @Autowired
    private transient RegionService regionService;

    @GetMapping(value="/", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RegionDto>> obtieneRegiones(){
        List<RegionDto> regiones =
            new ArrayList<>(List.of(RegionDto.builder().id("0").nombre("Seleccione una region").build()));
        regiones.addAll(regionService.findAllOrdered());
        return ResponseEntity.ok(regiones);
    }
}
