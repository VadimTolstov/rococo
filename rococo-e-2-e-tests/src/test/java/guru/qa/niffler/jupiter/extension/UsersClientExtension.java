package guru.qa.niffler.jupiter.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;

//внедрение зависимостей для Junit урок 7.2
public class UsersClientExtension implements TestInstancePostProcessor {
    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        for (Field field : testInstance.getClass().getDeclaredFields()) {
            if (field.getType().isAssignableFrom(UsersClient.class)) {
                field.setAccessible(true);
//                if ("db".equals(System.getProperty("users.client"))) {
                field.set(testInstance, new UsersDbClient());
//                }
            }
        }
    }
}