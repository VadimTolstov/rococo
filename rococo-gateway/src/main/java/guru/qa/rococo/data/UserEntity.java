package guru.qa.rococo.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Класс, представляющий сущность пользователя (user) в базе данных.
 * Этот класс используется для хранения информации о пользователях.
 * <p>
 * Аннотация @Entity указывает, что этот класс является JPA-сущностью, которая будет
 * отображаться на таблицу в базе данных.
 * <p>
 * Аннотация @Table(name = "user") указывает имя таблицы в базе данных.
 */
@Getter
@Setter
@Entity
@Table(name = "user")
public class UserEntity implements Serializable {

    /**
     * Уникальный идентификатор пользователя.
     * Аннотация @Id указывает, что это поле является первичным ключом.
     * Аннотация @GeneratedValue(strategy = GenerationType.AUTO) указывает, что значение
     * этого поля будет автоматически генерироваться базой данных.
     * Аннотация @Column определяет параметры столбца в таблице базы данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Имя пользователя (логин).
     * Аннотация @Column(nullable = false, unique = true) указывает, что это поле не может быть null и должно быть уникальным.
     */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * Имя пользователя.
     * Аннотация @Column указывает, что это поле будет отображаться на столбец в таблице.
     */
    @Column(name = "firstname")
    private String firstname;

    /**
     * Фамилия пользователя.
     * Аннотация @Column указывает, что это поле будет отображаться на столбец в таблице.
     */
    @Column(name = "lastname")
    private String lastname;

    /**
     * Аватар пользователя.
     * Аннотация @Lob указывает, что это поле будет храниться как большой объект (BLOB) в базе данных.
     */
    @Lob
    @Column(name = "avatar")
    private byte[] avatar;

    /**
     * Переопределение метода equals для сравнения объектов UserEntity.
     * Сравнение происходит по идентификатору (id).
     * Этот метод учитывает возможность использования Hibernate-прокси,
     * чтобы корректно сравнивать объекты, даже если они загружены через прокси.
     *
     * @param o Объект для сравнения.
     * @return true, если объекты равны, иначе false.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        UserEntity that = (UserEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    /**
     * Переопределение метода hashCode для корректной работы с коллекциями.
     * Этот метод учитывает возможность использования Hibernate-прокси,
     * чтобы корректно вычислять хэш-код для объектов, загруженных через прокси.
     *
     * @return Хэш-код объекта.
     */
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}