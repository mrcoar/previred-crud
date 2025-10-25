package cl.maraneda.previred.controller;

import cl.maraneda.previred.ApplicationConfig;
import cl.maraneda.previred.dto.ComunaDto;
import cl.maraneda.previred.model.Comuna;
import cl.maraneda.previred.model.Region;
import cl.maraneda.previred.repository.ComunaRepository;
import cl.maraneda.previred.repository.RegionRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)  // Reset context after tests
@Transactional  // Shared tx for @*All; no auto-rollback
@Import(ApplicationConfig.class)
public class ComunaControllerTest {

    @Autowired
    private transient MockMvc mockMvc;

    @Autowired
    protected transient ObjectMapper objectMapper;

    private static final String PATH = "/previred/comuna/porRegion/%s";
    private static final String TEST_REGION_ID = "XXIII";
    private static final Region TEST_REGION =
        Region.builder()
                .id(TEST_REGION_ID) //La region XXIII no existe, pero se creara una para evitar ruido
                .nombre("Region de Arica y Parinacota")
                .orden(1)
                .build();
    private static final List<Comuna> TEST_COMUNAS =
        List.of(
            Comuna.builder().id(1001).nombre("Arica").region(TEST_REGION).build(),
            Comuna.builder().id(1002).nombre("Camarones").region(TEST_REGION).build(),
            Comuna.builder().id(1003).nombre("Putre").region(TEST_REGION).build(),
            Comuna.builder().id(1004).nombre("General Lagos").region(TEST_REGION).build());

    @BeforeAll
    public static void prepareTest(
            @Autowired RegionRepository regionRepository,
            @Autowired ComunaRepository comunaRepository){
        if(!regionRepository.existsById(TEST_REGION.getId())) {
            regionRepository.save(TEST_REGION);
        }

        TEST_COMUNAS.stream().filter(c -> !comunaRepository.existsById(c.getId())).forEach(comunaRepository::save);
    }

    @Test
    public void whenSelectingInvalidRegionShouldReturnEmptyList() throws Exception{
        mockMvc.perform(get(String.format(PATH, "0")))
               .andExpect(status().isNoContent());
    }

    @Test
    public void whenSelectingExistingRegionShouldReturnComunas() throws Exception{
        MvcResult resp =
            mockMvc.perform(get(String.format(PATH, TEST_REGION_ID)))
                   .andExpect(status().isOk())
                   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                   .andReturn();
        String content = resp.getResponse().getContentAsString();
        List<ComunaDto> outputUsers = objectMapper.readValue(content, new TypeReference<>(){});
        assertEquals(
            TEST_COMUNAS.size(),
            outputUsers.stream().filter(c -> c.getId() !=0 ).count());
        TEST_COMUNAS.forEach(tc ->
            assertTrue(outputUsers.stream().anyMatch(c -> tc.getId().equals(c.getId()))));
    }

    @AfterAll
    public static void deleteAllTestData(
            @Autowired RegionRepository regionRepository,
            @Autowired ComunaRepository comunaRepository){
        regionRepository.deleteById(TEST_REGION_ID);
        comunaRepository.deleteTestComunas();
    }
}
