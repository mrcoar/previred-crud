package cl.maraneda.previred.controller;

import cl.maraneda.previred.ApplicationConfig;
import cl.maraneda.previred.dto.SearchDto;
import cl.maraneda.previred.dto.UserDto;
import cl.maraneda.previred.model.Comuna;
import cl.maraneda.previred.model.Region;
import cl.maraneda.previred.repository.ComunaRepository;
import cl.maraneda.previred.repository.RegionRepository;
import cl.maraneda.previred.service.UserService;
import cl.maraneda.previred.util.CriterioBusquedaUsuario;
import cl.maraneda.previred.util.Util;
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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)  // Reset context after tests
@Transactional  // Shared tx for @*All; no auto-rollback
@Import(ApplicationConfig.class)
public class UserControllerTest {
    @Autowired
    private transient MockMvc mockMvc;

    @Autowired
    protected transient ObjectMapper objectMapper;

    private static final String USER_PATH = "/previred/user/";
    private static final String SEARCH_PATH = USER_PATH + "search";
    private static final String TEST_REGION_ID = "XXIII";
    private static final String REQUIRED_VAL_MSG = "El valor es obligatorio para el criterio de busqueda especificado";
    private static final String REQUIRED_COMPLETE_NAME = "El nombre y el apellido son obligatorios para buscar usuarios por ese criterio";

    private static final SearchDto EMPTY_SEARCH_FILTER =
        SearchDto.builder()
                 .rut(null)
                 .nombre(null)
                 .apellido(null)
                 .fechaNacimiento(null)
                 .calle(null)
                 .comuna(null)
                 .region(null)
                 .criteria("TODOS")
                 .build();
    private static final Region TEST_REGION =
        Region.builder()
                .id(TEST_REGION_ID) //La region XXIII no existe, pero se creara una para evitar ruido
                .nombre("Region de Arica y Parinacota")
                .orden(1)
                .build();

    private static final List<Comuna> TEST_COMUNAS =
        List.of(
            Comuna.builder().id(1).nombre("Arica").region(TEST_REGION).build(),
            Comuna.builder().id(2).nombre("Camarones").region(TEST_REGION).build(),
            Comuna.builder().id(3).nombre("Putre").region(TEST_REGION).build(),
            Comuna.builder().id(4).nombre("General Lagos").region(TEST_REGION).build());

    private static final UserDto TEST_USER =
        UserDto.builder()
               .rut(Util.getRamdomRut())
               .nombre("Test").apellido("user")
               .fechaNacimiento("2000-01-01")
               .calle("Calle de prueba")
               .comuna(1)
               .build();

    private RequestBuilder getSaveRequestBuilder() throws Exception{
        return put(USER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TEST_USER));
    }
    private RequestBuilder getSearchRequestBuilder(SearchDto srch) throws Exception{
        return post(SEARCH_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(srch));
    }

    private MvcResult getSuccessfulSearchResult(SearchDto srch) throws Exception{
        return mockMvc.perform(this.getSearchRequestBuilder(srch))
                      .andExpect(status().isOk())
                      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                      .andReturn();
    }

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
    public void whenTryingInsertUserWithCorrectDataShouldObtain201StatusCode() throws Exception{
        mockMvc.perform(this.getSaveRequestBuilder()).andExpect(status().isCreated());
    }

    @Test
    public void whenTryingToSearchUserWithoutRestrictionsAndNoUserIsFoundShouldObtain404StatusCode() throws Exception{
        SearchDto search =
                SearchDto.builder().criteria(CriterioBusquedaUsuario.TODOS.toString()).build();
        mockMvc.perform(
            post(SEARCH_PATH).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(search)))
               .andExpect(status().isNotFound());
    }

    @Test
    public void whenTryingInsertAlreadyExistingUserShouldObtain403StatusCode() throws Exception{
        this.whenTryingInsertUserWithCorrectDataShouldObtain201StatusCode();
        mockMvc.perform(this.getSaveRequestBuilder()).andExpect(status().isForbidden());
    }

    @Test
    public void whenTryingInsertUserWithNullDataShouldObtain400StatusCode() throws Exception{
        UserDto emptyUser =
            UserDto.builder()
                   .rut(Util.getRamdomRut())
                   .nombre("").apellido("")
                   .fechaNacimiento("").calle("").comuna(0).build();
        String resp =
            mockMvc.perform(
                    put(USER_PATH).contentType(MediaType.APPLICATION_JSON)
                                  .content(objectMapper.writeValueAsString(emptyUser)))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();
        assertEquals("Todos los campos son obligatorios", resp);
    }

    @Test
    public void whenTryingInsertUserWithInvalidRutShouldObtain400StatusCode() throws Exception{
        UserDto newUser =
            UserDto.builder()
                    .rut("1-1")
                    .nombre("Alpha").apellido("Beta")
                    .fechaNacimiento("1999-09-09").calle("Sin calle").comuna(1).build();
        String resp =
                mockMvc.perform(
                        put(USER_PATH).contentType(MediaType.APPLICATION_JSON)
                                      .content(objectMapper.writeValueAsString(newUser)))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                        .andReturn().getResponse().getContentAsString();
        assertEquals("Formato de rut invalido o Rut no concuerda con digito verificador", resp);
    }

    @Test
    public void whenSearchingByNameWithExistingNameShouldReturnData() throws Exception{
        this.whenTryingInsertUserWithCorrectDataShouldObtain201StatusCode();
        SearchDto byNameFilter = EMPTY_SEARCH_FILTER.clone();
        byNameFilter.setNombre(TEST_USER.getNombre());
        byNameFilter.setCriteria(CriterioBusquedaUsuario.POR_NOMBRE.toString());
        MvcResult res = this.getSuccessfulSearchResult(byNameFilter);
        String content = res.getResponse().getContentAsString();
        List<SearchDto> outputUsers = objectMapper.readValue(content, new TypeReference<>() {});
        assertFalse(outputUsers.isEmpty());
        outputUsers.forEach(u ->
            assertEquals(u.getNombre(), TEST_USER.getNombre())
        );
    }

    @Test
    public void whenSearchingByNameWithUnexistingNameShouldNotReturnData() throws Exception{
        this.whenTryingInsertUserWithCorrectDataShouldObtain201StatusCode();
        SearchDto byNameFilter = EMPTY_SEARCH_FILTER.clone();
        byNameFilter.setNombre("UnexistingName");
        byNameFilter.setCriteria(CriterioBusquedaUsuario.POR_NOMBRE.toString());
        mockMvc.perform(this.getSearchRequestBuilder(byNameFilter))
               .andExpect(status().isNotFound());
    }

    @Test
    public void whenSearchingByLastnameWithExistingLastnameShouldReturnData() throws Exception{
        this.whenTryingInsertUserWithCorrectDataShouldObtain201StatusCode();
        SearchDto byLastnameFilter = EMPTY_SEARCH_FILTER.clone();
        byLastnameFilter.setApellido(TEST_USER.getApellido());
        byLastnameFilter.setCriteria(CriterioBusquedaUsuario.POR_APELLIDO.toString());
        MvcResult res = this.getSuccessfulSearchResult(byLastnameFilter);
        String content = res.getResponse().getContentAsString();
        List<SearchDto> outputUsers = objectMapper.readValue(content, new TypeReference<>() {});
        assertFalse(outputUsers.isEmpty());
        outputUsers.forEach(u ->
                assertEquals(u.getApellido(), TEST_USER.getApellido())
        );
    }

    @Test
    public void whenSearchingByLastnameWithUnexistingNameShouldNotReturnData() throws Exception{
        this.whenTryingInsertUserWithCorrectDataShouldObtain201StatusCode();
        SearchDto byLastnameFilter = EMPTY_SEARCH_FILTER.clone();
        byLastnameFilter.setApellido("UnexistingLastname");
        byLastnameFilter.setCriteria(CriterioBusquedaUsuario.POR_APELLIDO.toString());
        mockMvc.perform(this.getSearchRequestBuilder(byLastnameFilter))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenSearchingByCompleteNameWithExistingLastnameShouldReturnData() throws Exception{
        this.whenTryingInsertUserWithCorrectDataShouldObtain201StatusCode();
        SearchDto byCompleteNameFilter = EMPTY_SEARCH_FILTER.clone();
        byCompleteNameFilter.setNombre(TEST_USER.getNombre());
        byCompleteNameFilter.setApellido(TEST_USER.getApellido());
        byCompleteNameFilter.setCriteria(CriterioBusquedaUsuario.POR_NOMBRE_COMPLETO.toString());
        MvcResult res = this.getSuccessfulSearchResult(byCompleteNameFilter);
        String content = res.getResponse().getContentAsString();
        List<SearchDto> outputUsers = objectMapper.readValue(content, new TypeReference<>() {});
        assertFalse(outputUsers.isEmpty());
        outputUsers.forEach(u -> {
            assertEquals(u.getNombre(), TEST_USER.getNombre());
            assertEquals(u.getApellido(), TEST_USER.getApellido());
        });
    }

    @Test
    public void whenSearchingByCompleteNameWithUnexistingNameShouldNotReturnData() throws Exception{
        this.whenTryingInsertUserWithCorrectDataShouldObtain201StatusCode();
        SearchDto byCompleteNameFilter = EMPTY_SEARCH_FILTER.clone();
        byCompleteNameFilter.setNombre("UnexistingName2");
        byCompleteNameFilter.setApellido("UnexistingLastname2");
        byCompleteNameFilter.setCriteria(CriterioBusquedaUsuario.POR_NOMBRE_COMPLETO.toString());
        mockMvc.perform(this.getSearchRequestBuilder(byCompleteNameFilter))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenSearchingByCompleteNameWithNameAndWithoutLastNameShouldObtain400StatusCode() throws Exception{
        this.whenTryingInsertUserWithCorrectDataShouldObtain201StatusCode();
        SearchDto byNameFilter = EMPTY_SEARCH_FILTER.clone();
        byNameFilter.setNombre(TEST_USER.getNombre());
        byNameFilter.setCriteria(CriterioBusquedaUsuario.POR_NOMBRE_COMPLETO.toString());
        String res = mockMvc.perform(this.getSearchRequestBuilder(byNameFilter))
                               .andExpect(status().isBadRequest())
                               .andReturn().getResponse().getContentAsString();
        assertEquals(REQUIRED_COMPLETE_NAME, res);
    }

    @Test
    public void whenSearchingByCompleteNameWithNameAndWithoutNameShouldObtain400StatusCode() throws Exception{
        this.whenTryingInsertUserWithCorrectDataShouldObtain201StatusCode();
        SearchDto byNameFilter = EMPTY_SEARCH_FILTER.clone();
        byNameFilter.setApellido(TEST_USER.getApellido());
        byNameFilter.setCriteria(CriterioBusquedaUsuario.POR_NOMBRE_COMPLETO.toString());
        String res = mockMvc.perform(this.getSearchRequestBuilder(byNameFilter))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        assertEquals(REQUIRED_COMPLETE_NAME, res);
    }

    @Test
    public void whenSearchingByExistingComunaShouldObtain200StatusCode() throws Exception{
        this.whenTryingInsertUserWithCorrectDataShouldObtain201StatusCode();
        SearchDto byComunaFilter = EMPTY_SEARCH_FILTER.clone();
        byComunaFilter.setComuna(TEST_USER.getComuna());
        byComunaFilter.setCriteria(CriterioBusquedaUsuario.POR_COMUNA.toString());
        MvcResult res = this.getSuccessfulSearchResult(byComunaFilter);
        String content = res.getResponse().getContentAsString();
        List<SearchDto> outputUsers = objectMapper.readValue(content, new TypeReference<>() {});
        assertFalse(outputUsers.isEmpty());
        outputUsers.forEach(u ->
                assertEquals(u.getComuna(), TEST_USER.getComuna())
        );
    }

    @Test
    public void whenSearchingByUnexistingComunaSHouldObtain404StatusCode() throws Exception{
        this.whenTryingInsertUserWithCorrectDataShouldObtain201StatusCode();
        SearchDto byComunaFilter = EMPTY_SEARCH_FILTER.clone();
        byComunaFilter.setComuna(10000);
        byComunaFilter.setCriteria(CriterioBusquedaUsuario.POR_COMUNA.toString());
        mockMvc.perform(this.getSearchRequestBuilder(byComunaFilter))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenSearchingByExistingRegionShouldObtain200StatusCode() throws Exception{
        this.whenTryingInsertUserWithCorrectDataShouldObtain201StatusCode();
        SearchDto byRegionFilter = EMPTY_SEARCH_FILTER.clone();
        byRegionFilter.setRegion(TEST_REGION.getId());
        byRegionFilter.setCriteria(CriterioBusquedaUsuario.POR_REGION.toString());
        MvcResult res = this.getSuccessfulSearchResult(byRegionFilter);
        String content = res.getResponse().getContentAsString();
        List<SearchDto> outputUsers = objectMapper.readValue(content, new TypeReference<>() {});
        assertFalse(outputUsers.isEmpty());
        outputUsers.forEach(u ->
                assertEquals(TEST_REGION.getId(), u.getRegion())
        );
    }

    @Test
    public void whenSearchingByUnexistingRegionShouldObtain404StatusCode() throws Exception{
        this.whenTryingInsertUserWithCorrectDataShouldObtain201StatusCode();
        SearchDto byComunaFilter = EMPTY_SEARCH_FILTER.clone();
        byComunaFilter.setRegion("XXXIII");
        byComunaFilter.setCriteria(CriterioBusquedaUsuario.POR_REGION.toString());
        mockMvc.perform(this.getSearchRequestBuilder(byComunaFilter))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenSearchingByExistingRutShouldObtain200StatusCode() throws Exception{
        this.whenTryingInsertUserWithCorrectDataShouldObtain201StatusCode();
        SearchDto byRutFilter = EMPTY_SEARCH_FILTER.clone();
        byRutFilter.setRut(TEST_USER.getRut());
        byRutFilter.setCriteria(CriterioBusquedaUsuario.POR_RUT.toString());
        MvcResult res = this.getSuccessfulSearchResult(byRutFilter);
        String content = res.getResponse().getContentAsString();
        List<SearchDto> outputUsers = objectMapper.readValue(content, new TypeReference<>() {});
        assertFalse(outputUsers.isEmpty());
        outputUsers.forEach(u ->
                assertEquals(TEST_USER.getRut(), u.getRut())
        );
    }

    @Test
    public void whenSearchingByUnexistingRutShouldObtain404StatusCode() throws Exception{
        this.whenTryingInsertUserWithCorrectDataShouldObtain201StatusCode();
        SearchDto byRutFilter = EMPTY_SEARCH_FILTER.clone();
        byRutFilter.setRut("1-9");
        byRutFilter.setCriteria(CriterioBusquedaUsuario.POR_RUT.toString());
        mockMvc.perform(this.getSearchRequestBuilder(byRutFilter))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenSearchingByInvalidRutShouldObtain400StatusCode() throws Exception{
        this.whenTryingInsertUserWithCorrectDataShouldObtain201StatusCode();
        SearchDto byRutFilter = EMPTY_SEARCH_FILTER.clone();
        byRutFilter.setRut("1-8");
        byRutFilter.setCriteria(CriterioBusquedaUsuario.POR_RUT.toString());
        String res =
            mockMvc.perform(this.getSearchRequestBuilder(byRutFilter))
                   .andExpect(status().isBadRequest())
                   .andReturn().getResponse().getContentAsString();
        assertEquals("El valor ingresado no es un RUT valido: 1-8", res);
    }

    @Test
    public void whenSearchingForAllUsersAndUsersExistThenShouldObtain200StatusCode() throws Exception{
        this.whenTryingInsertUserWithCorrectDataShouldObtain201StatusCode();
        SearchDto filter = EMPTY_SEARCH_FILTER.clone();
        filter.setCriteria(CriterioBusquedaUsuario.TODOS.toString());
        MvcResult res = this.getSuccessfulSearchResult(filter);
        String content = res.getResponse().getContentAsString();
        List<SearchDto> outputUsers = objectMapper.readValue(content, new TypeReference<>() {});
        assertFalse(outputUsers.isEmpty());
        assertEquals(1, outputUsers.size());
    }

    @Test
    public void whenDeletingExistingUserShouldObtain200StatusCode() throws Exception{
        this.whenTryingInsertUserWithCorrectDataShouldObtain201StatusCode();
        mockMvc.perform(delete(USER_PATH + TEST_USER.getRut()))
               .andExpect(status().isOk());
    }

    @Test
    public void whenDeletingUnexistingUserShouldObtain404StatusCode() throws Exception{
        mockMvc.perform(delete(USER_PATH + TEST_USER.getRut()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenSearchingByNameAndNoValueIsProvidedShouldObtain400StatusCode() throws Exception{
        SearchDto filter = EMPTY_SEARCH_FILTER.clone();
        filter.setCriteria(CriterioBusquedaUsuario.POR_NOMBRE.toString());
        String res =
                mockMvc.perform(this.getSearchRequestBuilder(filter))
                        .andExpect(status().isBadRequest())
                        .andReturn().getResponse().getContentAsString();
        assertEquals(REQUIRED_VAL_MSG, res);
    }

    @Test
    public void whenSearchingByLastNameAndNoValueIsProvidedShouldObtain400StatusCode() throws Exception{
        SearchDto filter = EMPTY_SEARCH_FILTER.clone();
        filter.setCriteria(CriterioBusquedaUsuario.POR_APELLIDO.toString());
        String res =
                mockMvc.perform(this.getSearchRequestBuilder(filter))
                        .andExpect(status().isBadRequest())
                        .andReturn().getResponse().getContentAsString();
        assertEquals(REQUIRED_VAL_MSG, res);
    }

    @Test
    public void whenSearchingByRegionAndNoValueIsProvidedShouldObtain400StatusCode() throws Exception{
        SearchDto filter = EMPTY_SEARCH_FILTER.clone();
        filter.setCriteria(CriterioBusquedaUsuario.POR_REGION.toString());
        String res =
                mockMvc.perform(this.getSearchRequestBuilder(filter))
                        .andExpect(status().isBadRequest())
                        .andReturn().getResponse().getContentAsString();
        assertEquals(REQUIRED_VAL_MSG, res);
    }

    @Test
    public void whenSearchingByComunaAndNoValueIsProvidedShouldObtain400StatusCode() throws Exception{
        SearchDto filter = EMPTY_SEARCH_FILTER.clone();
        filter.setCriteria(CriterioBusquedaUsuario.POR_COMUNA.toString());
        String res =
                mockMvc.perform(this.getSearchRequestBuilder(filter))
                        .andExpect(status().isBadRequest())
                        .andReturn().getResponse().getContentAsString();
        assertEquals(REQUIRED_VAL_MSG, res);
    }

    @Test
    public void whenSearchingByRutAndNoValueIsProvidedShouldObtain400StatusCode() throws Exception{
        SearchDto filter = EMPTY_SEARCH_FILTER.clone();
        filter.setCriteria(CriterioBusquedaUsuario.POR_RUT.toString());
        String res =
                mockMvc.perform(this.getSearchRequestBuilder(filter))
                        .andExpect(status().isBadRequest())
                        .andReturn().getResponse().getContentAsString();
        assertEquals("El RUT es obligatorio para buscar usuarios por ese criterio", res);
    }

    @Test
    public void whenSearchingByNameAndLastnameAndNoValuesAreProvidedShouldObtain400StatusCode() throws Exception{
        SearchDto filter = EMPTY_SEARCH_FILTER.clone();
        filter.setCriteria(CriterioBusquedaUsuario.POR_NOMBRE_COMPLETO.toString());
        String res =
                mockMvc.perform(this.getSearchRequestBuilder(filter))
                        .andExpect(status().isBadRequest())
                        .andReturn().getResponse().getContentAsString();
        assertEquals(REQUIRED_COMPLETE_NAME, res);
    }

    @AfterAll
    public static void deleteAllTestData(
            @Autowired RegionRepository regionRepository,
            @Autowired ComunaRepository comunaRepository,
            @Autowired UserService userService){
        userService.deleteTestUsers();
        regionRepository.deleteById(TEST_REGION_ID);
        comunaRepository.deleteTestComunas();

    }
}
