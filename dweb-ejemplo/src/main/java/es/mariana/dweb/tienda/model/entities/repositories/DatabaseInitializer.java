package es.mariana.dweb.tienda.model.entities.repositories;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Component
public class DatabaseInitializer {

    private final SqliteConnectionProvider connectionProvider;

    public DatabaseInitializer(final SqliteConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @PostConstruct
    public void initialize() {
        try (Connection connection = this.connectionProvider.getConnection()) {
            executeScript(connection, "db/schema.sql");
            if (isEmpty(connection, "products")) {
                executeScript(connection, "db/data.sql");
            }
        } catch (final Exception e) {
            throw new IllegalStateException("Could not initialize SQLite database", e);
        }
    }

    private boolean isEmpty(final Connection connection, final String tableName) throws Exception {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            return resultSet.next() && resultSet.getInt(1) == 0;
        }
    }

    private void executeScript(final Connection connection, final String resourcePath) throws Exception {
        final ClassPathResource resource = new ClassPathResource(resourcePath);
        final String script = resource.getContentAsString(StandardCharsets.UTF_8);
        try (Statement statement = connection.createStatement()) {
            for (final String sql : script.split(";")) {
                final String trimmedSql = sql.trim();
                if (!trimmedSql.isEmpty()) {
                    statement.execute(trimmedSql);
                }
            }
        }
    }

}
