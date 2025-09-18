package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SpendEntityRowMapper implements RowMapper<SpendEntity> {
    public static final SpendEntityRowMapper instance = new SpendEntityRowMapper();

    private SpendEntityRowMapper() {
    }

    @Override
    public @Nonnull SpendEntity mapRow(@Nonnull ResultSet rs, int rowNum) throws SQLException {
        return new SpendEntity(
                rs.getObject("id", UUID.class),
                rs.getString("username"),
                CurrencyValues.valueOf(rs.getString("currency")),
                rs.getDate("spend_date"),
                rs.getDouble("amount"),
                rs.getString("description"),
                new CategoryEntity(rs.getObject("category_id", UUID.class))
        );
    }
}
