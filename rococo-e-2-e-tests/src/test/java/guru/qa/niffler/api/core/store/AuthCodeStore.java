package guru.qa.niffler.api.core.store;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Thread-safe хранилище для временного сохранения authorization code в процессе OAuth 2.0 flow.
 * <p>
 * Реализует шаблон Singleton через enum для обеспечения единственной точки доступа.
 * Использует {@link ThreadLocal} для безопасного хранения значений в многопоточной среде,
 * что критично для параллельного выполнения тестов и запросов.
 * </p>
 *
 * <p>Основные функции:
 * <ul>
 *   <li>Предоставляет thread-safe контейнер для временного хранения кода авторизации</li>
 *   <li>Обеспечивает изоляцию значений между потоками выполнения</li>
 *   <li>Автоматически очищает значение при завершении потока</li>
 * </ul>
 *
 * <p>Типичный сценарий использования:
 * <ol>
 *   <li>При перенаправлении OAuth-сервера на redirect_uri с параметром code</li>
 *   <li>Интерцептор {@link guru.qa.niffler.api.core.interceptor.AuthorizedCodeInterceptor} извлекает код</li>
 *   <li>Интерцептор сохраняет код в это хранилище через {@link #setCode(String)}</li>
 *   <li>В другом месте системы (например, при обмене кода на токен) код извлекается через {@link #getCode()}</li>
 * </ol>
 *
 * <p><b>Важно:</b> Значения хранятся только в контексте текущего потока. При работе с:
 * <ul>
 *   <li>Параллельными тестами - каждый поток имеет свою копию значения</li>
 *   <li>Асинхронными запросами - требуется дополнительная синхронизация</li>
 * </ul>
 */
@ParametersAreNonnullByDefault
public enum AuthCodeStore {
    /**
     * Единственный экземпляр хранилища (реализация Singleton через enum).
     * Гарантирует единственную точку доступа во всей JVM.
     */
    INSTANCE;

    /**
     * Thread-local хранилище для authorization code.
     * <p>
     * Каждый поток имеет свою независимую копию переменной.
     * Значение автоматически инициализируется пустой строкой.
     * </p>
     *
     * <p>Особенности:
     * <ul>
     *   <li>Автоматическое управление памятью: значения очищаются при завершении потока</li>
     *   <li>Нулевая конкуренция при доступе из одного потока</li>
     *   <li>Поддержка параллельного выполнения тестов</li>
     * </ul>
     */
    private final static ThreadLocal<String> threadSafeStore = ThreadLocal.withInitial(String::new);


    @Nullable
    public String getCode() {
        return threadSafeStore.get();
    }


    public void setCode(String code) {
        threadSafeStore.set(code);
    }

    public void clear() {
        threadSafeStore.remove();
    }
}