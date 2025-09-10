package service;

import guru.qa.rococo.data.PaintingEntity;
import guru.qa.rococo.data.repository.PaintingRepository;
import guru.qa.rococo.ex.BadRequestException;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.*;
import guru.qa.rococo.service.PaintingService;
import guru.qa.rococo.service.api.RestArtistClient;
import guru.qa.rococo.service.api.RestMuseumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class PaintingServiceTest {

    @Mock
    private PaintingRepository paintingRepository;

    @Mock
    private RestArtistClient restArtistClient;

    @Mock
    private RestMuseumClient restMuseumClient;

    private PaintingService paintingService;

    private UUID artistId;
    private UUID museumId;
    private UUID paintingId;
    private UUID countryId;
    private UUID artistId2;
    private UUID museumId2;
    private final String paintingContent = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD";
    private final String paintingTitle = "Прекрасный вечер";
    private final String paintingDescription = "Одна из самых лучших картин в мире который видел этот мир";
    private final String paintingUpdateContent = "data:image/png;base64,iVBORw0KGgoANSUhEUgAEAB";
    private final String paintingUpdateTitle = "Новое название";
    private final String paintingUpdateDescription = "Новое описание";
    private final String countryName = "Россия";
    private final String museumTitle = "Третьяковская галерея";
    private final String museumDescription = "Третьяковская галерея – один из главных музеев национального искусства России, отражающий его уникальный вклад в мировую культуру. Это гостеприимный музей, известный своей богатой коллекцией и многообразием представленных идей.";
    private final String museumCity = "City";
    private final String museumPhoto = "data:image/jpeg;base64,/9LDAKLDSJFJDSFS83S";
    private final String artistName = "Александр Пушкин";
    private final String artistBiography = "В 1834 году поэт прочитал сказку Петра Ершова «Конёк-Горбунок» и был так восхищён ей, что решил больше никогда не писать сказок.";
    private final String artistPhoto = "data:image/png;base64,iVBORw0KGgoANSUhEUgAEABCAYAfFcSJADUlEQVR42mNkYPhfDwAChwGA60e6kgABJRU5ErkJg==";

    private PaintingEntity paintingEntity;
    PaintingEntity paintingEntity2;
    private PaintingRequestJson paintingRequestJson;
    private ArtistJson artistJson;
    private MuseumJson museumJson;
    private CountryJson country;
    private ArtistRef artistRef;
    private MuseumRef museumRef;
    ArtistJson artistJson2;
    MuseumJson museumJson2;

    @BeforeEach
    void setUp() {
        artistId = UUID.randomUUID();
        museumId = UUID.randomUUID();
        paintingId = UUID.randomUUID();
        countryId = UUID.randomUUID();
        artistId2 = UUID.randomUUID();
        museumId2 = UUID.randomUUID();

        paintingEntity = new PaintingEntity();
        paintingEntity.setId(paintingId);
        paintingEntity.setTitle(paintingTitle);
        paintingEntity.setDescription(paintingDescription);
        paintingEntity.setArtist(artistId);
        paintingEntity.setMuseum(museumId);
        paintingEntity.setContent(paintingContent.getBytes(StandardCharsets.UTF_8));
        paintingService = new PaintingService(paintingRepository, restArtistClient, restMuseumClient);

        artistJson = new ArtistJson(artistId, artistName, artistBiography, artistPhoto);
        country = new CountryJson(countryId, countryName);
        museumJson = new MuseumJson(museumId, museumTitle, museumDescription, museumCity, new GeoJson("Москва", new CountryJson(UUID.randomUUID(), "Россия")));
        artistRef = new ArtistRef(artistId);
        museumRef = new MuseumRef(museumId);
        paintingRequestJson = new PaintingRequestJson(paintingId, paintingTitle, paintingDescription, paintingContent, artistRef, museumRef);

        artistJson2 = new ArtistJson(artistId2, "Новый художник", "Новая биография", "data:image/png;base64,iVBORw0KGgo");
        museumJson2 = new MuseumJson(museumId2, "Новый музей", "Новая история музея", "data:image/png;base64,iVBORw0", new GeoJson("Банког", new CountryJson(UUID.randomUUID(), "Тайланд")));

        paintingEntity2 = new PaintingEntity();
        paintingEntity2.setId(paintingId);
        paintingEntity2.setTitle(paintingUpdateTitle);
        paintingEntity2.setDescription(paintingUpdateDescription);
        paintingEntity2.setContent(paintingUpdateContent.getBytes(StandardCharsets.UTF_8));

    }

    @Test
    void getPaintingByIdShouldReturnPainting() {
        Mockito.when(paintingRepository.findById(paintingId)).thenReturn(Optional.of(paintingEntity));
        Mockito.when(restArtistClient.getArtistById(artistId)).thenReturn(artistJson);
        Mockito.when(restMuseumClient.getMuseumById(museumId)).thenReturn(museumJson);


        PaintingResponseJson result = paintingService.getPaintingById(paintingId);

        assertThat(result)
                .isNotNull()
                .extracting(
                        PaintingResponseJson::id,
                        PaintingResponseJson::title,
                        PaintingResponseJson::description,
                        PaintingResponseJson::artist,
                        PaintingResponseJson::museum,
                        PaintingResponseJson::content
                ).containsExactly(
                        paintingId,
                        paintingTitle,
                        paintingDescription,
                        artistJson,
                        museumJson,
                        paintingContent
                );
    }

    @Test
    void getPaintingByIdShouldThrowExceptionForInvalidId() {
        final UUID nonExistentId = UUID.randomUUID();
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> paintingService.getPaintingById(nonExistentId)
        );
        assertEquals("Картина не найдена: " + nonExistentId,
                exception.getMessage()
        );
    }

    @Test
    void getAllPaintingShouldReturnFilteredResults() {
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("title").ascending());

        Mockito.when(paintingRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(paintingEntity), pageable, 1));
        Mockito.when(restArtistClient.getArtistById(artistId)).thenReturn(artistJson);
        Mockito.when(restMuseumClient.getMuseumById(museumId)).thenReturn(museumJson);

        Page<PaintingResponseJson> result = paintingService.getAllPaintings(pageable, null);
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().id()).isEqualTo(paintingId);
        assertThat(result.getContent().getFirst().title()).isEqualTo(paintingTitle);
        assertThat(result.getContent().getFirst().description()).isEqualTo(paintingDescription);
        assertThat(result.getContent().getFirst().artist()).isEqualTo(artistJson);
        assertThat(result.getContent().getFirst().museum()).isEqualTo(museumJson);
        assertThat(result.getContent().getFirst().content()).isEqualTo(paintingContent);
    }

    @Test
    void getAllPaintingShouldSearchByTitle() {
        PageRequest pageable = PageRequest.of(0, 10);
        Mockito.when(paintingRepository.findAllByTitleContainingIgnoreCase(pageable, paintingTitle))
                .thenReturn(new PageImpl<>(List.of(paintingEntity), pageable, 1));
        Mockito.when(restArtistClient.getArtistById(artistId)).thenReturn(artistJson);
        Mockito.when(restMuseumClient.getMuseumById(museumId)).thenReturn(museumJson);

        Page<PaintingResponseJson> result = paintingService.getAllPaintings(pageable, paintingTitle);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().title()).isEqualTo(paintingTitle);
    }

    @Test
    void addArtistShouldSaveAndReturnPainting() {
        Mockito.when(paintingRepository.save(any(PaintingEntity.class))).thenReturn(paintingEntity);
        Mockito.when(restArtistClient.getArtistById(artistId)).thenReturn(artistJson);
        Mockito.when(restMuseumClient.getMuseumById(museumId)).thenReturn(museumJson);

        PaintingResponseJson result = paintingService.addPainting(paintingRequestJson);

        assertThat(result)
                .isNotNull()
                .extracting(
                        PaintingResponseJson::id,
                        PaintingResponseJson::title,
                        PaintingResponseJson::content,
                        PaintingResponseJson::description,
                        PaintingResponseJson::artist,
                        PaintingResponseJson::museum
                ).containsExactly(
                        paintingId,
                        paintingTitle,
                        paintingContent,
                        paintingDescription,
                        artistJson,
                        museumJson
                );

        ArgumentCaptor<PaintingEntity> captor = ArgumentCaptor.forClass(PaintingEntity.class);
        Mockito.verify(paintingRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(null);
        assertThat(captor.getValue().getTitle()).isEqualTo(paintingTitle);
        assertThat(captor.getValue().getContent()).isEqualTo(paintingContent.getBytes(StandardCharsets.UTF_8));
        assertThat(captor.getValue().getDescription()).isEqualTo(paintingDescription);
        assertThat(captor.getValue().getArtist()).isEqualTo(artistId);
        assertThat(captor.getValue().getMuseum()).isEqualTo(museumId);
    }

    @Test
    void addPaintingShouldValidateContent() {
        PaintingRequestJson nonExistentContent = new PaintingRequestJson
                (
                        paintingId,
                        paintingTitle,
                        paintingDescription,
                        null,
                        artistRef,
                        museumRef
                );
        assertThatThrownBy(() -> paintingService.addPainting(nonExistentContent))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Контент должен начинаться на data:image или не может быть пустым");
    }

    @Test
    void addPaintingShouldValidateContentFormat() {
        PaintingRequestJson nonExistentContent = new PaintingRequestJson
                (
                        paintingId,
                        paintingTitle,
                        paintingDescription,
                        "dsfdEES$",
                        artistRef,
                        museumRef
                );
        assertThatThrownBy(() -> paintingService.addPainting(nonExistentContent))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Контент должен начинаться на data:image или не может быть пустым");
    }

    @Test
    void addPaintingShouldValidateArtistFormat() {
        PaintingRequestJson nonExistentContent = new PaintingRequestJson
                (
                        paintingId,
                        paintingTitle,
                        paintingDescription,
                        paintingContent,
                        null,
                        museumRef
                );
        assertThatThrownBy(() -> paintingService.addPainting(nonExistentContent))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Художник или id художника не может быть null");
    }

    @Test
    void addPaintingShouldValidateArtistUUIDFormat() {
        PaintingRequestJson nonExistentContent = new PaintingRequestJson
                (
                        paintingId,
                        paintingTitle,
                        paintingDescription,
                        paintingContent,
                        new ArtistRef(null),
                        museumRef
                );
        assertThatThrownBy(() -> paintingService.addPainting(nonExistentContent))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Художник или id художника не может быть null");
    }

    @Test
    void addPaintingShouldValidateMuseumFormat() {
        PaintingRequestJson nonExistentContent = new PaintingRequestJson
                (
                        paintingId,
                        paintingTitle,
                        paintingDescription,
                        paintingContent,
                        artistRef,
                        null
                );
        assertThatThrownBy(() -> paintingService.addPainting(nonExistentContent))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Музей или id музея не может быть null");
    }

    @Test
    void addPaintingShouldValidateMuseumUUIDFormat() {
        PaintingRequestJson nonExistentContent = new PaintingRequestJson
                (
                        paintingId,
                        paintingTitle,
                        paintingDescription,
                        paintingContent,
                        artistRef,
                        new MuseumRef(null)
                );
        assertThatThrownBy(() -> paintingService.addPainting(nonExistentContent))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Музей или id музея не может быть null");
    }

    @Test
    void updatePantingAndArtistAndMuseumShouldModifyAndReturnPainting() {
        paintingEntity2.setArtist(artistId2);
        paintingEntity2.setMuseum(museumId2);

        Mockito.when(paintingRepository.findById(paintingId)).thenReturn(Optional.of(paintingEntity));
        Mockito.when(restArtistClient.getArtistById(artistId2)).thenReturn(artistJson2);
        Mockito.when(restMuseumClient.getMuseumById(museumId2)).thenReturn(museumJson2);
        Mockito.when(paintingRepository.save(any(PaintingEntity.class))).thenReturn(paintingEntity2);

        PaintingResponseJson result = paintingService.updatePainting(PaintingRequestJson.fromEntity(paintingEntity2));

        assertThat(result)
                .isNotNull()
                .extracting(
                        PaintingResponseJson::id,
                        PaintingResponseJson::title,
                        PaintingResponseJson::content,
                        PaintingResponseJson::description,
                        PaintingResponseJson::artist,
                        PaintingResponseJson::museum
                ).containsExactly(
                        paintingId,
                        paintingUpdateTitle,
                        paintingUpdateContent,
                        paintingUpdateDescription,
                        artistJson2,
                        museumJson2
                );

        Mockito.verify(paintingRepository).save(any(PaintingEntity.class));
    }

    @Test
    void updatePantingAndMuseumShouldModifyAndReturnPainting() {
        paintingEntity2.setArtist(artistId);
        paintingEntity2.setMuseum(museumId2);

        Mockito.when(paintingRepository.findById(paintingId)).thenReturn(Optional.of(paintingEntity));
        Mockito.when(restArtistClient.getArtistById(artistId)).thenReturn(artistJson);
        Mockito.when(restMuseumClient.getMuseumById(museumId2)).thenReturn(museumJson2);
        Mockito.when(paintingRepository.save(any(PaintingEntity.class))).thenReturn(paintingEntity2);

        PaintingResponseJson result = paintingService.updatePainting(PaintingRequestJson.fromEntity(paintingEntity2));

        assertThat(result)
                .isNotNull()
                .extracting(
                        PaintingResponseJson::id,
                        PaintingResponseJson::title,
                        PaintingResponseJson::content,
                        PaintingResponseJson::description,
                        PaintingResponseJson::artist,
                        PaintingResponseJson::museum
                ).containsExactly(
                        paintingId,
                        paintingUpdateTitle,
                        paintingUpdateContent,
                        paintingUpdateDescription,
                        artistJson,
                        museumJson2
                );

        Mockito.verify(paintingRepository).save(any(PaintingEntity.class));
    }

    @Test
    void updatePantingAndArtistShouldModifyAndReturnPainting() {
        paintingEntity2.setArtist(artistId2);
        paintingEntity2.setMuseum(museumId);

        Mockito.when(paintingRepository.findById(paintingId)).thenReturn(Optional.of(paintingEntity));
        Mockito.when(restArtistClient.getArtistById(artistId2)).thenReturn(artistJson2);
        Mockito.when(restMuseumClient.getMuseumById(museumId)).thenReturn(museumJson);
        Mockito.when(paintingRepository.save(any(PaintingEntity.class))).thenReturn(paintingEntity2);

        PaintingResponseJson result = paintingService.updatePainting(PaintingRequestJson.fromEntity(paintingEntity2));

        assertThat(result)
                .isNotNull()
                .extracting(
                        PaintingResponseJson::id,
                        PaintingResponseJson::title,
                        PaintingResponseJson::content,
                        PaintingResponseJson::description,
                        PaintingResponseJson::artist,
                        PaintingResponseJson::museum
                ).containsExactly(
                        paintingId,
                        paintingUpdateTitle,
                        paintingUpdateContent,
                        paintingUpdateDescription,
                        artistJson2,
                        museumJson
                );

        Mockito.verify(paintingRepository).save(any(PaintingEntity.class));
    }

    @Test
    void updatePaintingShouldCheckArtistExistence() {
        PaintingRequestJson request = PaintingRequestJson.fromEntity(paintingEntity);
        Mockito.when(paintingRepository.findById(paintingId)).thenReturn(Optional.of(paintingEntity));
        Mockito.when(restArtistClient.getArtistById(any())).thenThrow(NotFoundException.class);

        assertThatThrownBy(() -> paintingService.updatePainting(request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updatePaintingShouldCheckMuseumExistence() {
        PaintingRequestJson request = PaintingRequestJson.fromEntity(paintingEntity);
        Mockito.when(paintingRepository.findById(paintingId)).thenReturn(Optional.of(paintingEntity));
        Mockito.when(restMuseumClient.getMuseumById(any())).thenThrow(NotFoundException.class);

        assertThatThrownBy(() -> paintingService.updatePainting(request))
                .isInstanceOf(NotFoundException.class);
    }

}