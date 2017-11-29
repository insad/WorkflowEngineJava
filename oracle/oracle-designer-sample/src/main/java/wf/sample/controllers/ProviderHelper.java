package wf.sample.controllers;

import optimajet.workflow.core.provider.WorkflowDocumentProvider;
import optimajet.workflow.oracle.OracleProvider;
import oracle.jdbc.pool.OracleDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

final class ProviderHelper {

    private ProviderHelper() {
    }

    static WorkflowDocumentProvider getProvider() {
        DataSource dataSource = getDataSource();
        return new OracleProvider(dataSource);
    }

    private static DataSource getDataSource() {
        try (InputStream resourceAsStream = ProviderHelper.class.getResourceAsStream("/application.properties")) {
            Properties properties = new Properties();
            properties.load(resourceAsStream);

            Class.forName(properties.get("driverClass").toString());

            OracleDataSource dataSource = new OracleDataSource();
            dataSource.setURL(properties.get("jdbcUrl").toString());
            dataSource.setUser(properties.get("user").toString());
            dataSource.setPassword(properties.get("password").toString());
            return dataSource;
        } catch (SQLException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
