package guru.qa.rococo.service.db;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.data.tpl.XaTransactionTemplate;
import guru.qa.rococo.ex.RepositoryException;
import guru.qa.rococo.mapper.museum.MuseumMapper;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.museum.CountryJson;
import guru.qa.rococo.model.rest.museum.MuseumJson;
import guru.qa.rococo.service.MuseumClient;
import io.qameta.allure.Step;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MuseumDbClient implements MuseumClient {
  private static final Config CFG = Config.getInstance();
  private final MuseumRepository museumRepository = new MuseumRepository();
  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.museumJdbcUrl()
  );

  @Override
  public RestResponsePage<CountryJson> getCountries(@Nullable Integer page, @Nullable Integer size, @Nullable String sort) {
    return null;
  }

  @Override
  @Step("Получаем музея по id = {id}")
  public @Nullable MuseumJson getMuseumById(@NonNull UUID id) {
    return xaTransactionTemplate.execute(() ->
        museumRepository
            .findById(id)
            .map(MuseumMapper::mapToJson)
            .orElse(null)
    );
  }

  @Override
  @Step("Получаем списка музеев по id = {title}")
  public @NonNull RestResponsePage<MuseumJson> getMuseums(@Nullable Integer page, @Nullable Integer size, @Nullable String sort, @Nullable String title) {
    if (title != null) {
      final List<MuseumJson> museumJsonList = museumRepository.findByTitle(title)
          .stream()
          .map(MuseumMapper::mapToJson)
          .toList();
      return new RestResponsePage<>(museumJsonList);
    }
    return new RestResponsePage<>();
  }

  @Override
  @Step("Создаем музей {museumJson}")
  public @NonNull MuseumJson createMuseum(@NonNull MuseumJson museumJson) {
    return Objects.requireNonNull(xaTransactionTemplate.execute(() ->
        MuseumMapper.mapToJson(museumRepository.create(MuseumMapper.mapToEntity(museumJson)))
    ));
  }

  @Override
  @Step("Обновляем музей {museumJson}")
  public @NonNull MuseumJson updateMuseum(@NonNull MuseumJson museumJson) {
    return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
      museumRepository.findById(museumJson.id())
          .orElseThrow(() -> new RepositoryException("updateMuseum: Ошибка обновления художника с id {" + museumJson.id() + "}"));
      return MuseumMapper.mapToJson(museumRepository.update(MuseumMapper.mapToEntity(museumJson)));
    }));
  }

  @Override
  @Step("Удаляем музей {museumJson}")
  public void remove(@NonNull MuseumJson museumJson) {
    xaTransactionTemplate.execute(() -> {
      museumRepository.remove(MuseumMapper.mapToEntity(museumJson));
      return null;
    });
  }

  @Override
  @Step("Удаляем музеи по списку id {uuidList}")
  public void removeList(@NonNull List<UUID> uuidList) {
    xaTransactionTemplate.execute(() -> {
      museumRepository.removeByUuidList(uuidList);
      return null;
    });
  }
}
