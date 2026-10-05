package es.mariana.dweb.tienda.model.entities.repositories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class SqliteConnectionProvider {

    private final String databasePath;

    public SqliteConnectionProvider(@Value("${tienda.database.path:data/tienda.db}") final String databasePath) {
        this.databasePath = databasePath;
    }

    public Connection getConnection() throws SQLException {
        ensureDatabaseDirectory();
        return DriverManager.getConnection("jdbc:sqlite:" + this.databasePath);
    }

    private void ensureDatabaseDirectory() {
        final Path parent = Path.of(this.databasePath).toAbsolutePath().getParent();
        if (parent == null) {
            return;
        }
        try {
            Files.createDirectories(parent);
        } catch (final Exception e) {
            throw new IllegalStateException("Could not create SQLite database directory: " + parent, e);
        }
    }

}
