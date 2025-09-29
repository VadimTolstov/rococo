package guru.qa.rococo.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.config.Config;
import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@Getter
@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {
    protected static final Config CFG = Config.getInstance();

    protected final SelenideElement alert = $(".MuiAlert-message");

    @SuppressWarnings("unchecked")
    public T checkAlert(String message) {
        alert.shouldHave(Condition.text(message));
        return (T) this;
    }

    public abstract T checkThatPageLoaded();

 //   private final Header headerComponent = new Header();
}