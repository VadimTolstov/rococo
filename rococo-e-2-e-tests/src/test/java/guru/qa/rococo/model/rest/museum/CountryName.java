package guru.qa.rococo.model.rest.museum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CountryName {
  AUSTRALIA("Австралия"),
  AUSTRIA("Австрия"),
  AZERBAIJAN("Азербайджан"),
  ALBANIA("Албания"),
  ALGERIA("Алжир"),
  ANGOLA("Ангола"),
  ANDORRA("Андорра"),
  ARGENTINA("Аргентина"),
  ARMENIA("Армения"),
  AFGHANISTAN("Афганистан"),
  BANGLADESH("Бангладеш"),
  BARBADOS("Барбадос"),
  BAHRAIN("Бахрейн"),
  BELIZE("Белиз"),
  BELARUS("Белоруссия"),
  BELGIUM("Бельгия"),
  BENIN("Бенин"),
  BULGARIA("Болгария"),
  BOLIVIA("Боливия");

  private final String country;
}
