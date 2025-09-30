package guru.qa.rococo.page;

import guru.qa.rococo.page.component.CardsMain;
import guru.qa.rococo.page.component.Header;
import io.qameta.allure.Step;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {
  protected CardsMain cardsMain = new CardsMain();
  protected Header header = new Header();
//    protected final SearchField searchField = new SearchField();
//    protected final StatComponent statComponent = new StatComponent();
//    protected final SpendingTable spendingTable = new SpendingTable();

  public static final String URL = CFG.frontUrl();

  @Step("Перейти на страницу 'Картины'")

  @Step("Перейти на страницу 'Художники'")

  @Step("Перейти на страницу 'Музей'")
//    @Nonnull
//    public StatComponent getStatComponent() {
//        statComponent.getSelf().scrollIntoView(true);
//        return statComponent;
//    }
//
//    @Nonnull
//    public SearchField getSearchField() {
//        statComponent.getSelf().scrollIntoView(true);
//        return searchField;
//    }
//
//    @Nonnull
//    public SpendingTable getSpendingTable() {
//        spendingTable.getSelf().scrollIntoView(true);
//        return spendingTable;
//    }

  @NonNull
  @Step("Проверяем, что загрузилась главная страница")
  @Override
  public MainPage checkThatPageLoaded() {
    cardsMain.checkDisplayMain();
    return this;
  }
}
