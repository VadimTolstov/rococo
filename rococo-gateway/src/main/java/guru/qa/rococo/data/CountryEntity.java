package guru.qa.rococo.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Класс, представляющий сущность страны (country) в базе данных.
 * Этот класс используется для хранения информации о странах.
 * <p>
 * Аннотация @Entity указывает, что этот класс является JPA-сущностью, которая будет
 * отображаться на таблицу в базе данных.
 * <p>
 * Аннотация @Table(name = "country") указывает имя таблицы в базе данных.
 */
@Getter
@Setter
@Entity
@Table(name = "\"country\"")
public class CountryEntity implements Serializable {

    /**
     * Уникальный идентификатор страны.
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
     * Название страны.
     * Аннотация @Column(nullable = false, unique = true) указывает, что это поле не может быть null и должно быть уникальным.
     */
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MuseumEntity> museums;

    /**
     * Переопределение метода equals для сравнения объектов CountryEntity.
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
        CountryEntity that = (CountryEntity) o;
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