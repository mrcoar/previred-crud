package cl.maraneda.previred.service;

import cl.maraneda.previred.dto.ComunaDto;
import cl.maraneda.previred.model.Comuna;
import cl.maraneda.previred.model.Region;
import cl.maraneda.previred.repository.ComunaRepository;
import cl.maraneda.previred.repository.RegionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ComunaServiceTest {
    @InjectMocks
    public transient ComunaService comunaService;

    @Mock
    public transient ComunaRepository comunaRepository;

    @Mock
    public transient RegionRepository regionRepository;

    @Mock
    public transient ModelMapper mapper;

    @Test
    void whenGettingValidRegionIDThenShouldReturnAssociatedCommunes() {
        Region reg = Region.builder()
                .id("XIII")
                .nombre("Region de Arica y Parinacota")
                .orden(1)
                .build();

        List<Comuna> comunas = List.of(
                Comuna.builder().id(1).nombre("Arica").region(reg).build(),
                Comuna.builder().id(2).nombre("Camarones").region(reg).build(),
                Comuna.builder().id(3).nombre("Putre").region(reg).build(),
                Comuna.builder().id(4).nombre("General Lagos").region(reg).build()
        );

        // Mock repo saves
        when(regionRepository.save(reg)).thenReturn(reg);
        when(comunaRepository.saveAll(comunas)).thenReturn(comunas);

        // Key: Mock repo's findByRegion to return entities (List<Comuna>)
        when(comunaRepository.findByRegion(reg.getId())).thenReturn(comunas);

        // Mock ModelMapper to return DTOs from entities
        when(mapper.map(any(Comuna.class), eq(ComunaDto.class))).thenAnswer(invocation -> {
            Comuna c = invocation.getArgument(0);
            return ComunaDto.builder()
                    .id(c.getId())
                    .nombre(c.getNombre())
                    .build();
        });

        // Act: Save (mocks hit)
        Region savedReg = regionRepository.save(reg);
        assertEquals(reg.getId(), savedReg.getId());

        List<Comuna> saved = comunaRepository.saveAll(comunas);
        assertEquals(saved.size(), comunas.size());
        IntStream.rangeClosed(1, 4).forEach(i ->
                assertTrue(saved.stream().anyMatch(c -> c.getId() == i))
        );

        // Act: Call real service (uses mocked repo and mapper)
        List<ComunaDto> loaded = comunaService.findByRegion(reg.getId());

        // Assert: DTOs from mapping
        assertFalse(loaded.isEmpty());
        assertEquals(loaded.size(), saved.size());
        IntStream.rangeClosed(1, 4).forEach(i ->
                assertEquals(1, loaded.stream().filter(c -> c.getId() == i).count())
        );
        comunas.forEach(c ->
                assertEquals(1, loaded.stream().filter(l -> c.getId().equals(l.getId())).count())
        );
    }
}
