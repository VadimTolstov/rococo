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
@Table(name = "\"museum\"")
public class MuseumEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "city")
    private String city;

    @Lob
    @Column(name = "photo")
    private byte[] photo;

    /**
     * Идентификатор страны, в которой находится музей.
     * Аннотация @ManyToOne указывает на связь "многие к одному" между музеем и страной.
     * Аннотация @JoinColumn(name = "country_id") указывает, что это поле будет отображаться
     * на столбец "country_id" в таблице museum, который является внешним ключом к таблице country.
     */
    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private CountryEntity country;

    @OneToMany(mappedBy = "museum", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<PaintingEntity> paintings;

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
