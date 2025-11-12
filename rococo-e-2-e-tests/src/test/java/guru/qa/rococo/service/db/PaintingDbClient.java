package guru.qa.rococo.service.db;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.painting.PaintingEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.data.repository.PaintingRepository;
import guru.qa.rococo.data.tpl.XaTransactionTemplate;
import guru.qa.rococo.ex.RepositoryException;
import guru.qa.rococo.mapper.artist.ArtistMapper;
import guru.qa.rococo.mapper.museum.MuseumMapper;
import guru.qa.rococo.mapper.painting.PaintingMapper;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.painting.PaintingJson;
import guru.qa.rococo.service.PaintingClient;
import io.qameta.allure.Step;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PaintingDbClient implements PaintingClient {
  private static final Config CFG = Config.getInstance();
  private final ArtistRepository artistRepository = new ArtistRepository();
  private final PaintingRepository paintingRepository = new PaintingRepository();
  private final MuseumRepository museumRepository = new MuseumRepository();
  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.artistJdbcUrl(),
      CFG.paintingJdbcUrl()
  );

  @Override
  public RestResponsePage<PaintingJson> getPaintings(@Nullable Integer page, @Nullable Integer size, @Nullable String sort, @Nullable String title) {
    throw new UnsupportedOperationException("Can`t getPaintings painting using DB");
  }

  @Override
  public RestResponsePage<PaintingJson> getPaintingsByAuthorId(@Nullable Integer page, @Nullable Integer size, @Nullable String sort, @NonNull UUID authorId) {
    throw new UnsupportedOperationException("Can`t getPaintingsByAuthorId painting using DB");
  }

  @Override
  @Step("Создание картины {painting}")
  public PaintingJson createPainting(@NonNull PaintingJson painting) {
    return xaTransactionTemplate.execute(() ->
        PaintingMapper.mapToJson(
            paintingRepository.create(PaintingMapper.mapToEntity(painting)),
            artistRepository.findById(painting.artist().id()).map(ArtistMapper::mapToJson).orElse(null),
            museumRepository.findById(painting.museum().id()).map(MuseumMapper::mapToJson).orElse(null)
        )
    );
  }

  @Override
  @Step("Обновление картины {painting}")
  public @NonNull PaintingJson updatePainting(@NonNull PaintingJson paintingJson) {
    return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
          final PaintingEntity oldPainting = paintingRepository
              .findById(paintingJson.id())
              .orElse(null);
          if (oldPainting != null) {
            return PaintingMapper.mapToJson(
                paintingRepository.update(PaintingMapper.mapToEntity(paintingJson)),
                paintingJson.artist(),
                paintingJson.museum()
            );
          }
          throw new RepositoryException("updatePainting: Ошибка обновления картины с id {" + paintingJson.id() + "}");
        }
    ));
  }

  @Override
  @Step("Удаляем картину {painting}")
  public void remove(@NonNull PaintingJson painting) {
    xaTransactionTemplate.execute(() -> {
      paintingRepository.removeByUuidList(
          paintingRepository.findById(painting.id())
              .stream()
              .map(PaintingEntity::getId)
              .toList()
      );
      return null;
    });
  }

  @Override
  @Step("Удаляем картины по списку id {uuidList}")
  public void removeList(@NonNull List<UUID> uuidList) {
    xaTransactionTemplate.execute(() -> {
      paintingRepository.removeByUuidList(uuidList);
      return null;
    });
  }
}
