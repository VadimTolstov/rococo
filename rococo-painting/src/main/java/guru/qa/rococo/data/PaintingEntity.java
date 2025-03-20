package guru.qa.rococo.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Класс, представляющий сущность картины (painting) в базе данных.
 * Этот класс используется для хранения информации о картинах.
 * <p>
 * Аннотация @Entity указывает, что этот класс является JPA-сущностью, которая будет
 * отображаться на таблицу в базе данных.
 * <p>
 * Аннотация @Table(name = "painting") указывает имя таблицы в базе данных.
 */
@Getter
@Setter
@Entity
@Table(name = "painting",schema = "public")
public class PaintingEntity implements Serializable {

    /**
     * Уникальный идентификатор картины.
     * Аннотация @Id указывает, что это поле является первичным ключом.
     * Аннотация @GeneratedValue(strategy = GenerationType.AUTO) указывает, что значение
     * этого поля будет автоматически генерироваться базой данных.
     * Аннотация @Column определяет параметры столбца в таблице базы данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    private UUID id;

    /**
     * Название картины.
     * Аннотация @Column(nullable = false) указывает, что это поле не может быть null.
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Описание картины.
     * Аннотация @Column указывает, что это поле будет отображаться на столбец в таблице.
     */
    @Column(name = "description", length = 1000, columnDefinition = "TEXT")
    private String description;

    /**
     * Идентификатор художника, создавшего картину.
     * Аннотация @ManyToOne указывает на связь "многие к одному" между картиной и художником.
     * Аннотация @JoinColumn(name = "artist_id") указывает, что это поле будет отображаться
     * на столбец "artist_id" в таблице painting, который является внешним ключом к таблице artist.
     */
//    @ManyToOne todo
//    @JoinColumn(name = "artist_id", nullable = false, referencedColumnName = "id")
    @Column(name = "artist_id", nullable = false)
    private UUID artist;

    /**
     * Идентификатор музея, в котором находится картина.
     * Аннотация @ManyToOne указывает на связь "многие к одному" между картиной и музеем.
     * Аннотация @JoinColumn(name = "museum_id") указывает, что это поле будет отображаться
     * на столбец "museum_id" в таблице painting, который является внешним ключом к таблице museum.
     */
//    @ManyToOne todo
//    @JoinColumn(name = "museum_id", referencedColumnName = "id")
    @Column(name = "museum_id", nullable = false)
    private UUID museum;

    /**
     * Изображение картины.
     * Аннотация @Lob указывает, что это поле будет храниться как большой объект (BLOB) в базе данных.
     */
    @Column(name = "content", columnDefinition = "bytea", nullable = false)
    private byte[] content;

    /**
     * Переопределение метода equals для сравнения объектов PaintingEntity.
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
        PaintingEntity that = (PaintingEntity) o;
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