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
 * Класс, представляющий сущность музея (museum) в базе данных.
 * Этот класс используется для хранения информации о музеях.
 * <p>
 * Аннотация @Entity указывает, что этот класс является JPA-сущностью, которая будет
 * отображаться на таблицу в базе данных.
 * <p>
 * Аннотация @Table(name = "museum") указывает имя таблицы в базе данных.
 */
@Getter
@Setter
@Entity
@Table(name = "museum", schema = "public")
public class MuseumEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    private UUID id;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT", length = 1000)
    private String description;

    @Column(columnDefinition = "bytea", nullable = false)
    private byte[] photo;

    /**
     * Геолокация, в которой находится музей.
     * Аннотация @ManyToOne указывает на связь "многие к одному" между музеем и геолокацией.
     * Аннотация @JoinColumn(name = "geo_id") указывает, что это поле будет отображаться
     * на столбец "geo_id" в таблице museum, который является внешним ключом к таблице geo.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "geo_id", nullable = false)
    private GeoEntity geo;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        MuseumEntity that = (MuseumEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
