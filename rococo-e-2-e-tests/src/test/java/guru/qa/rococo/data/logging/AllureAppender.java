package guru.qa.rococo.data.logging;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.github.vertical_blank.sqlformatter.languages.Dialect;
import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.StdoutLogger;
import io.qameta.allure.attachment.AttachmentData;
import io.qameta.allure.attachment.AttachmentProcessor;
import io.qameta.allure.attachment.DefaultAttachmentProcessor;
import io.qameta.allure.attachment.FreemarkerAttachmentRenderer;
import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.lang3.StringUtils.isNoneEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;


public class AllureAppender extends StdoutLogger {

    @SuppressWarnings("FieldCanBeLocal")
    private final String templateName = "sql-attachment.ftl";
    private final AttachmentProcessor<AttachmentData>  attachmentProcessor = new DefaultAttachmentProcessor();

    @Override
    public void logSQL(int connectionId, String now, long elapsed, Category category, String prepared, String sql, String url) {
        if(isNoneEmpty(sql)){
            String safeSql = sql.replaceAll("(?i)'data:image[^']*'", "'<IMAGE_DATA>'");

            int maxLength = 10000;
            if (safeSql.length() > maxLength) {
                safeSql = safeSql.substring(0, maxLength) + "... <TRUNCATED>";
            }

            final SqlAttachmentData attachmentData = new SqlAttachmentData(
                safeSql.split("\\s+")[0].toUpperCase() + "query to: " + StringUtils.substringBetween(url,"5432/","?"),
                SqlFormatter.of(Dialect.PostgreSql).format(safeSql)
            );
            attachmentProcessor.addAttachment(
                attachmentData,
                new FreemarkerAttachmentRenderer(templateName)
            );
        }
    }
}
