package guru.qa.rococo.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Класс, представляющий сущность права доступа (authority) в базе данных.
 * Этот класс используется для хранения информации о правах доступа, которые могут быть назначены пользователям.
 * <p>
 * Аннотация @Entity указывает, что этот класс является JPA-сущностью, которая будет
 * отображаться на таблицу в базе данных.
 * <p>
 * Аннотация @Table(name = "authority") указывает имя таблицы в базе данных.
 */
@Getter
@Setter
@Entity
@Table(name = "authority")
public class AuthorityEntity implements Serializable {

    /**
     * Уникальный идентификатор права доступа.
     * Аннотация @Id указывает, что это поле является первичным ключом.
     * Аннотация @GeneratedValue(strategy = GenerationType.AUTO) указывает, что значение
     * этого поля будет автоматически генерироваться базой данных.
     * Аннотация @Column определяет параметры столбца в таблице базы данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Тип права доступа.
     * Аннотация @Column(nullable = false) указывает, что это поле не может быть null.
     * Аннотация @Enumerated(EnumType.STRING) указывает, что значение этого поля будет
     * храниться в базе данных как строка (String), соответствующая имени перечисления.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Authority authority;

    /**
     * Пользователь, которому принадлежит это право доступа.
     * Аннотация @ManyToOne указывает на связь "многие к одному" между правами доступа и пользователем.
     * Аннотация @JoinColumn(name = "user_id") указывает, что это поле будет отображаться
     * на столбец "user_id" в таблице authority, который является внешним ключом к таблице user.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    /**
     * Переопределение метода equals для сравнения объектов AuthorityEntity.
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
        AuthorityEntity that = (AuthorityEntity) o;
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