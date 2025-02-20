//package guru.qa.rococo.data;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//import org.hibernate.proxy.HibernateProxy;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import java.util.UUID;
//
///**
// * Класс, представляющий сущность художника (artist) в базе данных.
// * Этот класс используется для хранения информации о художниках.
// * <p>
// * Аннотация @Entity указывает, что этот класс является JPA-сущностью, которая будет
// * отображаться на таблицу в базе данных.
// * <p>
// * Аннотация @Table(name = "artist") указывает имя таблицы в базе данных.
// */
//@Getter
//@Setter
//@Entity
//@Table(name = "\"artist\"")
//public class ArtistEntity implements Serializable {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    @Column(name = "id", unique = true, nullable = false, columnDefinition = "BINARY(16)")
//    private UUID id;
//
//    @Column(name = "name", unique = true, nullable = false)
//    private String name;
//
//    @Column(name = "biography", nullable = false, length = 2000)
//    private String biography;
//
//    @Lob
//    @Column(name = "photo")
//    private byte[] photo;
//
//    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL)
//    private List<PaintingEntity> paintings = new ArrayList<>();
//
//    @Override
//    public final boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null) return false;
//        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
//        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
//        if (thisEffectiveClass != oEffectiveClass) return false;
//        ArtistEntity that = (ArtistEntity) o;
//        return getId() != null && Objects.equals(getId(), that.getId());
//    }
//
//    @Override
//    public final int hashCode() {
//        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
//    }
//}
