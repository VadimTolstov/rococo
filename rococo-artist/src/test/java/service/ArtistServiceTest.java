package service;

import guru.qa.rococo.data.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.ex.BadRequestException;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.service.ArtistService;
import org.junit.jupiter.api.Assertions;
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
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ArtistServiceTest {
    @Mock
    private ArtistRepository artistRepository;
    private ArtistService artistService;

    private UUID artistId;
    private ArtistEntity artistEntity;
    private final String photo = "data:image/png;base64,iVBORw0KGgoANSUhEUgAEABCAYAfFcSJADUlEQVR42mNkYPhfDwAChwGA60e6kgABJRU5ErkJg==";
    private final String name = "Александр Пушкин";
    private final String biography = "В 1834 году поэт прочитал сказку Петра Ершова «Конёк-Горбунок» и был так восхищён ей, что решил больше никогда не писать сказок.";

    private ArtistEntity artistEntity2;
    private final String photo2 = "data:image/png;base64,GA60e6kgABJRU5ErkJg==";
    private final String name2 = "Леонардо да Винчи";
    private final String biography2 = "Не имел фамилии. « Да Винчи» означает место, где художник родился.";

    @BeforeEach
    void setUp() {
        artistService = new ArtistService(artistRepository);
        artistId = UUID.randomUUID();
        artistEntity = new ArtistEntity();
        artistEntity.setId(artistId);
        artistEntity.setName(name);
        artistEntity.setBiography(biography);
        artistEntity.setPhoto(photo.getBytes(StandardCharsets.UTF_8));

        UUID artistId2 = UUID.randomUUID();
        artistEntity2 = new ArtistEntity();
        artistEntity2.setId(artistId2);
        artistEntity2.setName(name2);
        artistEntity2.setBiography(biography2);
        artistEntity2.setPhoto(photo2.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void getArtistByIdShouldReturnArtist() {
        Mockito.when(artistRepository.findById(artistId)).thenReturn(Optional.of(artistEntity));

        ArtistJson result = artistService.getArtistById(artistId);

        Mockito.verify(artistRepository, Mockito.times(1)).findById(artistId);
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(artistId);
        assertThat(result.name()).isEqualTo(name);
        assertThat(result.biography()).isEqualTo(biography);
        assertThat(result.photo()).isEqualTo(photo);
    }

    @Test
    void getArtistByIdShouldThrowExceptionForInvalidId() {
        UUID nonExistentId = UUID.randomUUID();
        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> artistService.getArtistById(nonExistentId)
        );

        Assertions.assertEquals(
                "Artist not found id:" + nonExistentId,
                exception.getMessage()
        );
    }

    @Test
    void getAllArtistsShouldReturnFilteredResults() {
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        Mockito.when(artistRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(artistEntity, artistEntity2), pageable, 2));
        Page<ArtistJson> result = artistService.getAllArtists(pageable, null);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().getFirst().name()).isEqualTo(name);
        assertThat(result.getContent().getLast().name()).isEqualTo(name2);
    }

    @Test
    void getAllArtistsShouldSearchByName() {
        PageRequest pageable = PageRequest.of(0, 10);

        Mockito.when(artistRepository.findByNameContainingIgnoreCase(name, pageable))
                .thenReturn(new PageImpl<>(List.of(artistEntity), pageable, 1));
        Page<ArtistJson> result = artistService.getAllArtists(pageable, name);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().name()).isEqualTo(name);
    }

    @Test
    void addArtistShouldSaveAndReturnArtist() {
        Mockito.when(artistRepository.save(any(ArtistEntity.class))).thenReturn(artistEntity2);

        ArtistJson result = artistService.addArtist(ArtistJson.fromEntity(artistEntity2));

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(name2);
        assertThat(result.biography()).isEqualTo(biography2);
        assertThat(result.photo()).isEqualTo(photo2);

        ArgumentCaptor<ArtistEntity> captor = ArgumentCaptor.forClass(ArtistEntity.class);
        Mockito.verify(artistRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo(name2);
        assertThat(captor.getValue().getBiography()).isEqualTo(biography2);
        assertThat(captor.getValue().getPhoto()).isEqualTo(photo2.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void updateArtistShouldModifyAndReturnArtist() {
        artistEntity2.setId(artistId);
        Mockito.when(artistRepository.findById(artistId)).thenReturn(Optional.of(artistEntity));
        Mockito.when(artistRepository.save(any(ArtistEntity.class))).thenReturn(artistEntity2);

        ArtistJson result = artistService.updateArtist(ArtistJson.fromEntity(artistEntity2));

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(artistId);
        assertThat(result.name()).isEqualTo(name2);
        assertThat(result.biography()).isEqualTo(biography2);
        assertThat(result.photo()).isEqualTo(photo2);

        ArgumentCaptor<ArtistEntity> captor = ArgumentCaptor.forClass(ArtistEntity.class);
        Mockito.verify(artistRepository).save(captor.capture());

        assertThat(captor.getValue())
                .isNotNull()
                .extracting(
                        ArtistEntity::getId,
                        ArtistEntity::getName,
                        ArtistEntity::getBiography,
                        ArtistEntity::getPhoto
                )
                .containsExactly(
                        artistId,
                        name2,
                        biography2,
                        photo2.getBytes(StandardCharsets.UTF_8)
                );
    }

    @Test
    void updateArtistShouldThrowNotFoundException() {
        Mockito.when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> artistService.updateArtist(ArtistJson.fromEntity(artistEntity)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Artist not found id:" + artistId);
    }

    @Test
    void updateArtistShouldThrowBadRequestExceptionIfIdIsNull() {
        artistEntity.setId(null);
        assertThatThrownBy(() -> artistService.updateArtist(ArtistJson.fromEntity(artistEntity)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("id: ID художника обязателен для заполнения");
    }
}
