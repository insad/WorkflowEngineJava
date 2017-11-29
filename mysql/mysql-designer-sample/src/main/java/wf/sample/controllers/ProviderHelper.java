package wf.sample.controllers;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import optimajet.workflow.core.provider.WorkflowDocumentProvider;
import optimajet.workflow.mysql.MySqlProvider;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

final class ProviderHelper {

    private ProviderHelper() {
    }

    static WorkflowDocumentProvider getProvider() {
        DataSource dataSource = getDataSource();
        return new MySqlProvider(dataSource);
    }

    private static DataSource getDataSource() {
        try (InputStream resourceAsStream = ProviderHelper.class.getResourceAsStream("/application.properties")) {
            Properties properties = new Properties();
            properties.load(resourceAsStream);

            Class.forName(properties.get("driverClass").toString());

            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setURL(properties.get("jdbcUrl").toString());
            dataSource.setDatabaseName(properties.get("database").toString());
            dataSource.setUser(properties.get("user").toString());
            dataSource.setPassword(properties.get("password").toString());
            return dataSource;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
