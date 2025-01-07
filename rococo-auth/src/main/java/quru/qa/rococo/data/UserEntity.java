package quru.qa.rococo.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static jakarta.persistence.FetchType.EAGER;

/**
 * Класс, представляющий сущность пользователя в базе данных.
 * Этот класс используется для хранения информации о пользователе, включая его учетные данные,
 * статус аккаунта и список прав доступа (authorities).
 */
@Getter
@Setter
@Entity
@Table(name = "\"user\"") // Имя таблицы в базе данных
public class UserEntity implements Serializable {

    // Уникальный идентификатор пользователя
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    // Имя пользователя (логин), должно быть уникальным
    @Column(nullable = false, unique = true)
    private String username;

    // Пароль пользователя
    @Column(nullable = false)
    private String password;

    // Флаг, указывающий, активен ли аккаунт пользователя
    @Column(nullable = false)
    private Boolean enabled;

    // Флаг, указывающий, не истек ли срок действия аккаунта
    @Column(name = "account_non_expired", nullable = false)
    private Boolean accountNonExpired;

    // Флаг, указывающий, не заблокирован ли аккаунт
    @Column(name = "account_non_locked", nullable = false)
    private Boolean accountNonLocked;

    // Флаг, указывающий, не истек ли срок действия учетных данных
    @Column(name = "credentials_non_expired", nullable = false)
    private Boolean credentialsNonExpired;

    /**
     * Список прав доступа (authorities), связанных с пользователем.
     * Аннотация @OneToMany указывает на связь "один ко многим" между пользователем и его правами доступа.
     * Параметр fetch = EAGER указывает, что права доступа будут загружаться сразу при загрузке пользователя.
     * Параметр cascade = CascadeType.ALL указывает, что все операции (сохранение, обновление, удаление)
     * будут каскадно применяться к связанным правам доступа.
     * Параметр orphanRemoval = true указывает, что при удалении права доступа из списка,
     * оно будет удалено из базы данных.
     * Параметр mappedBy = "user" указывает, что связь управляется полем "user" в классе AuthorityEntity.
     */
    @OneToMany(fetch = EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    private List<AuthorityEntity> authorities = new ArrayList<>();

    /**
     * Добавляет права доступа (authorities) пользователю.
     * Этот метод добавляет одно или несколько прав доступа в список authorities
     * и устанавливает связь между правом доступа и текущим пользователем.
     *
     * @param authorities Права доступа, которые нужно добавить.
     */
    public void addAuthorities(AuthorityEntity... authorities) {
        for (AuthorityEntity authority : authorities) {
            this.authorities.add(authority);
            authority.setUser(this); // Устанавливаем связь с текущим пользователем
        }
    }

    /**
     * Удаляет право доступа (authority) у пользователя.
     * Этот метод удаляет право доступа из списка authorities
     * и убирает связь между правом доступа и текущим пользователем.
     *
     * @param authority Право доступа, которое нужно удалить.
     */
    public void removeAuthority(AuthorityEntity authority) {
        this.authorities.remove(authority);
        authority.setUser(null); // Убираем связь с текущим пользователем
    }

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