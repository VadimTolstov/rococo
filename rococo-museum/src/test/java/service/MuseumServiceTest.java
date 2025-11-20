//package service;
//
//import guru.qa.rococo.data.CountryEntity;
//import guru.qa.rococo.data.GeoEntity;
//import guru.qa.rococo.data.MuseumEntity;
//import guru.qa.rococo.data.repository.CountryRepository;
//import guru.qa.rococo.data.repository.GeoRepository;
//import guru.qa.rococo.data.repository.MuseumRepository;
//import guru.qa.rococo.ex.BadRequestException;
//import guru.qa.rococo.ex.NotFoundException;
//import guru.qa.rococo.model.CountryJson;
//import guru.qa.rococo.model.GeoJson;
//import guru.qa.rococo.model.MuseumJson;
//import guru.qa.rococo.service.MuseumService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class MuseumServiceTest {
//
//    @Mock
//    private MuseumRepository museumRepository;
//
//    @Mock
//    private CountryRepository countryRepository;
//
//    @Mock
//    private GeoRepository geoRepository;
//
//    @InjectMocks
//    private MuseumService museumService;
//
//    private UUID museumId;
//    private UUID countryId;
//    private UUID geoId;
//    private MuseumEntity museumEntity;
//    private CountryEntity countryEntity;
//    private GeoEntity geoEntity;
//    private MuseumJson museumJson;
//
//    @BeforeEach
//    void setUp() {
//        museumId = UUID.randomUUID();
//        countryId = UUID.randomUUID();
//        geoId = UUID.randomUUID();
//
//        countryEntity = new CountryEntity();
//        countryEntity.setId(countryId);
//        countryEntity.setName("Russia");
//
//        geoEntity = new GeoEntity();
//        geoEntity.setId(geoId);
//        geoEntity.setCity("Moscow");
//        geoEntity.setCountry(countryEntity);
//
//        museumEntity = new MuseumEntity();
//        museumEntity.setId(museumId);
//        museumEntity.setTitle("Pushkin Museum");
//        museumEntity.setDescription("Great art museum");
//        museumEntity.setPhoto("photo_bytes".getBytes());
//        museumEntity.setGeo(geoEntity);
//
//        museumJson = new MuseumJson(
//                museumId,
//                "Pushkin Museum",
//                "Great art museum",
//                "data:image/jpeg;base64,test_photo",
//                new GeoJson(
//                        "Moscow",
//                        new CountryJson(countryId, "Russia")
//                )
//        );
//    }
//
//    @Test
//    void getAllMuseum_ShouldReturnPageOfMuseums() {
//        Pageable pageable = Pageable.ofSize(10);
//        when(museumRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(museumEntity)));
//
//        Page<MuseumJson> result = museumService.getAllMuseum(pageable, null);
//
//        assertNotNull(result);
//        assertEquals(1, result.getTotalElements());
//        assertEquals("Pushkin Museum", result.getContent().get(0).title());
//    }
//
//    @Test
//    void getAllMuseum_WithTitleFilter_ShouldReturnFilteredMuseums() {
//        Pageable pageable = Pageable.ofSize(10);
//        when(museumRepository.findAllByTitleContainsIgnoreCase(pageable, "Pushkin"))
//                .thenReturn(new PageImpl<>(List.of(museumEntity)));
//
//        Page<MuseumJson> result = museumService.getAllMuseum(pageable, "Pushkin");
//
//        assertNotNull(result);
//        assertEquals(1, result.getTotalElements());
//        assertEquals("Pushkin Museum", result.getContent().get(0).title());
//    }
//
//    @Test
//    void getMuseumById_ShouldReturnMuseum() {
//        when(museumRepository.findById(museumId)).thenReturn(Optional.of(museumEntity));
//
//        MuseumJson result = museumService.getMuseumById(museumId);
//
//        assertNotNull(result);
//        assertEquals("Pushkin Museum", result.title());
//        assertEquals("Moscow", result.geo().city());
//    }
//
//    @Test
//    void getMuseumById_WhenNotFound_ShouldThrowException() {
//        when(museumRepository.findById(museumId)).thenReturn(Optional.empty());
//
//        assertThrows(NotFoundException.class, () -> museumService.getMuseumById(museumId));
//    }
//
//    @Test
//    void addMuseum_ShouldCreateNewMuseum() {
//        MuseumJson newMuseum = new MuseumJson(
//                null,
//                "New Museum",
//                "Description",
//                "data:image/jpeg;base64,test",
//                new GeoJson( "SPB", new CountryJson(null, "Russia"))
//        );
//
//        when(countryRepository.findByName("Russia")).thenReturn(Optional.of(countryEntity));
//        when(geoRepository.findByCityAndCountryId("SPB", countryId)).thenReturn(Optional.empty());
//        when(geoRepository.save(any(GeoEntity.class))).thenReturn(geoEntity);
//        when(museumRepository.save(any(MuseumEntity.class))).thenReturn(museumEntity);
//
//        MuseumJson result = museumService.addMuseum(newMuseum);
//
//        assertNotNull(result);
//        assertEquals("Pushkin Museum", result.title());
//        verify(museumRepository, times(1)).save(any(MuseumEntity.class));
//    }
//
//    @Test
//    void addMuseum_WithInvalidPhoto_ShouldThrowException() {
//        MuseumJson invalidMuseum = new MuseumJson(
//                null,
//                "New Museum",
//                "Description",
//                "invalid_photo",
//                new GeoJson( "SPB", new CountryJson(null, "Russia"))
//        );
//
//        assertThrows(BadRequestException.class, () -> museumService.addMuseum(invalidMuseum));
//    }
//
//    @Test
//    void updateMuseum_ShouldUpdateExistingMuseum() {
//        MuseumJson updatedMuseum = new MuseumJson(
//                museumId,
//                "Updated Museum",
//                "Updated description",
//                "data:image/jpeg;base64,updated_photo",
//                new GeoJson( "Moscow", new CountryJson(countryId, "Russia"))
//        );
//
//        when(museumRepository.findById(museumId)).thenReturn(Optional.of(museumEntity));
//        when(countryRepository.findById(countryId)).thenReturn(Optional.of(countryEntity));
//        when(geoRepository.findByCityAndCountryId("Moscow", countryId)).thenReturn(Optional.of(geoEntity));
//        when(museumRepository.save(any(MuseumEntity.class))).thenReturn(museumEntity);
//
//        MuseumJson result = museumService.updateMuseum(updatedMuseum);
//
//        assertNotNull(result);
//        verify(museumRepository, times(1)).save(any(MuseumEntity.class));
//    }
//
//    @Test
//    void updateMuseum_WhenNotFound_ShouldThrowException() {
//        when(museumRepository.findById(museumId)).thenReturn(Optional.empty());
//
//        assertThrows(NotFoundException.class, () -> museumService.updateMuseum(museumJson));
//    }
//
//    @Test
//    void getAllCountries_ShouldReturnPageOfCountries() {
//        Pageable pageable = Pageable.ofSize(10);
//        when(countryRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(countryEntity)));
//
//        Page<CountryJson> result = museumService.getAllCountries(pageable);
//
//        assertNotNull(result);
//        assertEquals(1, result.getTotalElements());
//        assertEquals("Russia", result.getContent().get(0).name());
//    }
//
//    @Test
//    void addMuseum_WithExistingCountryByName_ShouldUseExistingCountry() {
//        MuseumJson newMuseum = new MuseumJson(
//                null,
//                "New Museum",
//                "Description",
//                "data:image/jpeg;base64,test",
//                new GeoJson( "SPB", new CountryJson(null, "Russia"))
//        );
//
//        when(countryRepository.findByName("Russia")).thenReturn(Optional.of(countryEntity));
//        when(geoRepository.findByCityAndCountryId("SPB", countryId)).thenReturn(Optional.of(geoEntity));
//        when(museumRepository.save(any(MuseumEntity.class))).thenReturn(museumEntity);
//
//        MuseumJson result = museumService.addMuseum(newMuseum);
//
//        assertNotNull(result);
//        verify(countryRepository, never()).save(any(CountryEntity.class));
//    }
//
//    @Test
//    void addMuseum_WithNewCountry_ShouldCreateNewCountry() {
//        MuseumJson newMuseum = new MuseumJson(
//                null,
//                "New Museum",
//                "Description",
//                "data:image/jpeg;base64,test",
//                new GeoJson( "SPB", new CountryJson(null, "France"))
//        );
//
//        CountryEntity newCountry = new CountryEntity();
//        newCountry.setId(UUID.randomUUID());
//        newCountry.setName("France");
//
//        when(countryRepository.findByName("France")).thenReturn(Optional.empty());
//        when(countryRepository.existsByName("France")).thenReturn(false);
//        when(countryRepository.save(any(CountryEntity.class))).thenReturn(newCountry);
//        when(geoRepository.findByCityAndCountryId("SPB", newCountry.getId())).thenReturn(Optional.empty());
//        when(geoRepository.save(any(GeoEntity.class))).thenReturn(geoEntity);
//        when(museumRepository.save(any(MuseumEntity.class))).thenReturn(museumEntity);
//
//        MuseumJson result = museumService.addMuseum(newMuseum);
//
//        assertNotNull(result);
//        verify(countryRepository, times(1)).save(any(CountryEntity.class));
//    }
//
//    @Test
//    void addMuseum_WithExistingGeo_ShouldUseExistingGeo() {
//        MuseumJson newMuseum = new MuseumJson(
//                null,
//                "New Museum",
//                "Description",
//                "data:image/jpeg;base64,test",
//                new GeoJson("Moscow", new CountryJson(countryId, "Russia"))
//        );
//
//        when(countryRepository.findById(countryId)).thenReturn(Optional.of(countryEntity));
//        when(geoRepository.findByCityAndCountryId("Moscow", countryId)).thenReturn(Optional.of(geoEntity));
//        when(museumRepository.save(any(MuseumEntity.class))).thenReturn(museumEntity);
//
//        MuseumJson result = museumService.addMuseum(newMuseum);
//
//        assertNotNull(result);
//        verify(geoRepository, never()).save(any(GeoEntity.class));
//    }
//}
