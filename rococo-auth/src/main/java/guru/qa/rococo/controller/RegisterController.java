package guru.qa.rococo.controller;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import guru.qa.rococo.model.RegistrationModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import guru.qa.rococo.service.UserService;

/**
 * Контроллер для обработки запросов, связанных с регистрацией новых пользователей.
 * Этот класс отвечает за отображение страницы регистрации и обработку данных,
 * введенных пользователем при регистрации.
 */
@Controller
public class RegisterController {

    // Логгер для записи событий и ошибок
    private static final Logger LOG = LoggerFactory.getLogger(RegisterController.class);

    // Имя представления (view) для страницы регистрации
    private static final String REGISTRATION_VIEW_NAME = "register";

    // Атрибут модели для хранения имени пользователя
    private static final String MODEL_USERNAME_ATTR = "username";

    // Атрибут модели для хранения формы регистрации
    private static final String MODEL_REG_FORM_ATTR = "registrationModel";

    // Атрибут модели для хранения URI фронтенд-приложения
    private static final String MODEL_FRONT_URI_ATTR = "frontUri";

    // Имя атрибута модели для хранения ошибок валидации формы регистрации
    private static final String REG_MODEL_ERROR_BEAN_NAME = "org.springframework.validation.BindingResult.registrationModel";

    // Сервис для работы с пользователями
    private final UserService userService;

    // URI фронтенд-приложения, используемый для перенаправления
    private final String rococoFrontUri;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param userService    Сервис для работы с пользователями.
     * @param rococoFrontUri URI фронтенд-приложения, который указывает на главную страницу.
     *                       Значение берется из конфигурационного файла (например, application.properties).
     */
    @Autowired
    public RegisterController(UserService userService,
                              @Value("${rococo-front.base-uri}") String rococoFrontUri) {
        this.userService = userService;
        this.rococoFrontUri = rococoFrontUri;
    }

    /**
     * Обрабатывает GET-запрос на /register.
     * Этот метод отображает страницу регистрации, передавая в модель пустую форму регистрации
     * и URI фронтенд-приложения.
     *
     * @param model Объект модели, который используется для передачи данных в представление.
     * @return Имя представления для страницы регистрации.
     */
    @GetMapping("/register")
    public String getRegisterPage(@Nonnull Model model) {
        // Добавляем пустую форму регистрации в модель
        model.addAttribute(MODEL_REG_FORM_ATTR, new RegistrationModel(null, null, null));
        // Добавляем URI фронтенд-приложения в модель
        model.addAttribute(MODEL_FRONT_URI_ATTR, rococoFrontUri + "/main");
        return REGISTRATION_VIEW_NAME;
    }

    /**
     * Обрабатывает POST-запрос на /register.
     * Этот метод регистрирует нового пользователя на основе данных, введенных в форму регистрации.
     * Если данные не прошли валидацию или произошла ошибка при регистрации, пользователю возвращается
     * страница регистрации с соответствующими сообщениями об ошибках.
     *
     * @param registrationModel Модель данных, введенных пользователем в форму регистрации.
     * @param errors            Объект для хранения ошибок валидации.
     * @param model             Объект модели, который используется для передачи данных в представление.
     * @param response          Объект HttpServletResponse для установки статуса ответа.
     * @return Имя представления для страницы регистрации.
     */
    @PostMapping(value = "/register")
    public String registerUser(@Valid @ModelAttribute RegistrationModel registrationModel,
                               Errors errors,
                               Model model,
                               HttpServletResponse response) {
        // Проверяем, есть ли ошибки валидации
        if (!errors.hasErrors()) {
            final String registeredUserName;
            try {
                // Регистрируем пользователя через сервис
                registeredUserName = userService.registerUser(
                        registrationModel.username(),
                        registrationModel.password()
                );
                // Устанавливаем статус ответа "201 Created"
                response.setStatus(HttpServletResponse.SC_CREATED);
                // Добавляем имя зарегистрированного пользователя в модель
                model.addAttribute(MODEL_USERNAME_ATTR, registeredUserName);
            } catch (DataIntegrityViolationException e) {
                // Логируем ошибку, если пользователь с таким именем уже существует
                LOG.error("### Error while registration user", e);
                // Устанавливаем статус ответа "400 Bad Request"
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                // Добавляем ошибку в модель
                addErrorToRegistrationModel(
                        registrationModel,
                        model,
                        "username", "Username `" + registrationModel.username() + "` already exists"
                );
            }
        } else {
            // Если есть ошибки валидации, устанавливаем статус ответа "400 Bad Request"
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        // Добавляем URI фронтенд-приложения в модель
        model.addAttribute(MODEL_FRONT_URI_ATTR, rococoFrontUri + "/main");
        return REGISTRATION_VIEW_NAME;
    }

    /**
     * Вспомогательный метод для добавления ошибки в модель регистрации.
     *
     * @param registrationModel Модель данных, введенных пользователем в форму регистрации.
     * @param model             Объект модели, который используется для передачи данных в представление.
     * @param fieldName         Имя поля, к которому относится ошибка.
     * @param error             Сообщение об ошибке.
     */
    private void addErrorToRegistrationModel(@Nonnull RegistrationModel registrationModel,
                                             @Nonnull Model model,
                                             @Nonnull String fieldName,
                                             @Nonnull String error) {
        // Получаем объект Errors из модели
        BeanPropertyBindingResult errorResult = (BeanPropertyBindingResult) model.getAttribute(REG_MODEL_ERROR_BEAN_NAME);
        if (errorResult == null) {
            // Если объект Errors отсутствует, создаем новый
            errorResult = new BeanPropertyBindingResult(registrationModel, "registrationModel");
        }
        // Добавляем ошибку в объект Errors
        errorResult.addError(new FieldError("registrationModel", fieldName, error));
    }
}