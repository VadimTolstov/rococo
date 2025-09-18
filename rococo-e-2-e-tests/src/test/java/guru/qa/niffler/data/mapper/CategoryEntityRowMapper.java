package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CategoryEntityRowMapper implements RowMapper<CategoryEntity> {

    public static final CategoryEntityRowMapper instance = new CategoryEntityRowMapper();

    private CategoryEntityRowMapper() {
    }

    @Override
    public @Nonnull CategoryEntity mapRow(@Nonnull ResultSet rs, int rowNum) throws SQLException {
        return new CategoryEntity(
                rs.getObject("id", UUID.class),
                rs.getString("name"),
                rs.getString("username"),
                rs.getBoolean("archived")
        );
    }
}
