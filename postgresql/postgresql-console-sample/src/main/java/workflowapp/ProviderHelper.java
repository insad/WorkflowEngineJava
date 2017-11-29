package workflowapp;

import optimajet.workflow.core.provider.WorkflowDocumentProvider;
import optimajet.workflow.postgresql.PostgreSqlProvider;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

final class ProviderHelper {

    private ProviderHelper() {
    }

    static WorkflowDocumentProvider getProvider() {
        DataSource dataSource = getDataSource();
        return new PostgreSqlProvider(dataSource);
    }

    private static DataSource getDataSource() {
        try (InputStream resourceAsStream = ProviderHelper.class.getResourceAsStream("/application.properties")) {
            Properties properties = new Properties();
            properties.load(resourceAsStream);

            Class.forName(properties.get("driverClass").toString());

            PGSimpleDataSource dataSource = new PGSimpleDataSource();
            dataSource.setUrl(properties.get("jdbcUrl").toString());
            dataSource.setUser(properties.get("user").toString());
            dataSource.setPassword(properties.get("password").toString());
            return dataSource;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
