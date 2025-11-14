package guru.qa.rococo.service.db;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.painting.PaintingEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.data.repository.PaintingRepository;
import guru.qa.rococo.data.tpl.XaTransactionTemplate;
import guru.qa.rococo.ex.RepositoryException;
import guru.qa.rococo.mapper.artist.ArtistMapper;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import guru.qa.rococo.service.ArtistClient;
import io.qameta.allure.Step;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ArtistDbClient implements ArtistClient {
  private static final Config CFG = Config.getInstance();
  private final ArtistRepository artistRepository = new ArtistRepository();
  private final PaintingRepository paintingRepository = new PaintingRepository();
  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.artistJdbcUrl(),
      CFG.paintingJdbcUrl()
  );

  @Override
  @Step("Получаем художника по id = {id}")
  public @Nullable ArtistJson getArtist(@NonNull UUID id) {
    return xaTransactionTemplate.execute(() ->
        artistRepository.findById(id)
            .map(ArtistMapper::mapToJson)
            .orElse(null)
    );
  }

  @Override
  @Step("Создаем художника {artist}")
  public ArtistJson createArtist(@NonNull ArtistJson artist) {
    return xaTransactionTemplate.execute(() ->
        ArtistMapper.mapToJson(
            artistRepository.create(ArtistMapper.mapToEntity(artist)))
    );
  }

  @Override
  @Step("Обновляем данные художника на {artist}")
  public @NonNull ArtistJson updateArtist(@NonNull ArtistJson artist) {
    return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
          artistRepository.findById(artist.id())
              .orElseThrow(() -> new RepositoryException("updateArtist: Ошибка обновления художника с id {" + artist.id() + "}"));
          return ArtistMapper.mapToJson(
              artistRepository.update(ArtistMapper.mapToEntity(artist)));
        }
    ));
  }

  @Override
  @Step("Получаем списка художников по name = {name}")
  public RestResponsePage<ArtistJson> getPageListArtists(@Nullable String name, @Nullable Integer page, @Nullable Integer size, @Nullable String sort) {
    if (name != null) {
      final List<ArtistJson> artistJsonList = artistRepository.findByName(name)
          .stream()
          .map(ArtistMapper::mapToJson)
          .toList();
      return new RestResponsePage<>(artistJsonList);
    }
    return new RestResponsePage<>();
  }

  @Override
  @Step("Получаем список художников по списку id {uuidList}")
  public List<ArtistJson> getListArtists(@NonNull List<UUID> uuidList) {
    return xaTransactionTemplate.execute(() ->
        artistRepository.findAllById(uuidList)
            .stream()
            .map(ArtistMapper::mapToJson)
            .toList()
    );
  }

  @Override
  @Step("Удаляем художника {artist}")
  public void remove(@NonNull ArtistJson artist) {
    xaTransactionTemplate.execute(() -> {
      paintingRepository.removeByUuidList(
          paintingRepository.findByArtistId(artist.id())
              .stream()
              .map(PaintingEntity::getId)
              .toList()
      );
      artistRepository.remove(ArtistMapper.mapToEntity(artist));
      return null;
    });
  }

  @Override
  @Step("Удаляем художников по списку id {uuidList}")
  public void removeList(@NonNull List<UUID> uuidList) {
    xaTransactionTemplate.execute(() -> {
      paintingRepository.removeByUuidList(
          uuidList
              .stream()
              .map(paintingRepository::findByArtistId)
              .flatMap(List::stream)
              .map(PaintingEntity::getId)
              .distinct()
              .toList());

      artistRepository.removeByUuidList(uuidList);
      return null;
    });
  }

  @Step("Полное удаление данных")
  public void removeAll() {
    xaTransactionTemplate.execute(() -> {
      artistRepository.removeAll();
      return null;
    });
  }
}
