package cl.maraneda.previred.service;

import cl.maraneda.previred.dto.RegionDto;
import cl.maraneda.previred.model.Region;
import cl.maraneda.previred.repository.RegionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegionServiceTest {
    @InjectMocks
    public transient RegionService regionService;

    @Mock
    public transient RegionRepository regionRepository;

    @Mock
    public transient ModelMapper mapper;

    @Test
    void testingObtainAllRegions(){
        List<Region> regs = Arrays.asList(
            Region.builder().id("I").nombre("Region de Tarapaca").orden(2).build(),
            Region.builder().id("II").nombre("Region de Antofagasta").orden(3).build(),
            Region.builder().id("III").nombre("Region de Atacama").orden(4).build(),
            Region.builder().id("IV").nombre("Region de Coquimbo").orden(5).build(),
            Region.builder().id("V").nombre("Region de Valparaiso").orden(6).build(),
            Region.builder().id("RM").nombre("Region Metropolitana de Santiago").orden(7).build(),
            Region.builder().id("VI").nombre("Region del Libertador Bernardo O'' Higgins").orden(8).build(),
            Region.builder().id("VII").nombre("Region del Maule").orden(9).build(),
            Region.builder().id("VIII").nombre("Region del Bio Bio").orden(11).build(),
            Region.builder().id("IX").nombre("Region de la Araucania").orden(12).build(),
            Region.builder().id("X").nombre("Region de Los Lagos").orden(14).build(),
            Region.builder().id("XI").nombre("Region de Aysen del General Carlos Ibanez del Campo").orden(15).build(),
            Region.builder().id("XII").nombre("Region de Magallanes y la Antartica Chilena").orden(16).build(),
            Region.builder().id("XIII").nombre("Region de Arica y Parinacota").orden(1).build(),
            Region.builder().id("XIV").nombre("Region de Los Rios").orden(13).build(),
            Region.builder().id("XV").nombre("Region del Nuble").orden(10).build()
        );
        regs.sort(Comparator.comparing(Region::getOrden));
        Sort s = Sort.by(Sort.Direction.ASC, "orden");
        when(regionRepository.saveAll(regs)).thenReturn(regs);
        when(regionRepository.findAll(eq(s))).thenReturn(regs);
        when(mapper.map(any(Region.class), eq(RegionDto.class))).thenAnswer(i -> {
            Region r = i.getArgument(0);
            return RegionDto.builder().id(r.getId()).nombre(r.getNombre()).build();
        });

        List<Region> saved = regionRepository.saveAll(regs);

        assertEquals(regs.size(), saved.size());

        List<RegionDto> loaded = regionService.findAllOrdered();
        assertFalse(loaded.isEmpty());
        IntStream.range(0, regs.size()).forEach(i ->
            assertEquals(loaded.get(i).getId(),
                         regs.stream().filter(r -> r.getOrden()==i+1).findFirst().orElseThrow().getId())
        );
    }
}
