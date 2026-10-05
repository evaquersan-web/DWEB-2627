package es.mariana.dweb.tienda.model.entities.repositories;

import es.mariana.dweb.tienda.model.entities.Comment;
import es.mariana.dweb.tienda.model.entities.Product;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductRepository {

    private final SqliteConnectionProvider connectionProvider;

    public ProductRepository(final SqliteConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public List<Product> findAll() {
        final List<Product> products = new ArrayList<Product>();
        final String sql = "SELECT id, name, in_stock, price FROM products ORDER BY id";

        try (Connection connection = this.connectionProvider.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                final Product product = EntityMapper.mapProduct(resultSet);
                product.getComments().addAll(findCommentsByProductId(connection, product.getId()));
                products.add(product);
            }
            return products;
        } catch (final Exception e) {
            throw new IllegalStateException("Could not find products", e);
        }
    }

    public Product findById(final Integer id) {
        final String sql = "SELECT id, name, in_stock, price FROM products WHERE id = ?";

        try (Connection connection = this.connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                final Product product = EntityMapper.mapProduct(resultSet);
                product.getComments().addAll(findCommentsByProductId(connection, product.getId()));
                return product;
            }
        } catch (final Exception e) {
            throw new IllegalStateException("Could not find product with id " + id, e);
        }
    }

    public Product save(final Product product) {
        final String sql = "INSERT INTO products (name, in_stock, price) VALUES (?, ?, ?)";

        try (Connection connection = this.connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setProductParameters(statement, product);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getInt(1));
                }
            }
            return product;
        } catch (final Exception e) {
            throw new IllegalStateException("Could not save product", e);
        }
    }

    public void update(final Product product) {
        final String sql = "UPDATE products SET name = ?, in_stock = ?, price = ? WHERE id = ?";

        try (Connection connection = this.connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setProductParameters(statement, product);
            statement.setInt(4, product.getId());
            statement.executeUpdate();
        } catch (final Exception e) {
            throw new IllegalStateException("Could not update product with id " + product.getId(), e);
        }
    }

    public void deleteById(final Integer id) {
        try (Connection connection = this.connectionProvider.getConnection()) {
            connection.setAutoCommit(false);
            try {
                executeDelete(connection, "DELETE FROM product_comments WHERE product_id = ?", id);
                executeDelete(connection, "DELETE FROM order_lines WHERE product_id = ?", id);
                executeDelete(connection, "DELETE FROM products WHERE id = ?", id);
                connection.commit();
            } catch (final Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (final Exception e) {
            throw new IllegalStateException("Could not delete product with id " + id, e);
        }
    }

    private void setProductParameters(final PreparedStatement statement, final Product product) throws Exception {
        statement.setString(1, product.getName());
        statement.setInt(2, product.isInStock() ? 1 : 0);
        final BigDecimal price = product.getPrice() == null ? BigDecimal.ZERO : product.getPrice();
        statement.setString(3, price.toPlainString());
    }

    private void executeDelete(final Connection connection, final String sql, final Integer id) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    private List<Comment> findCommentsByProductId(final Connection connection, final Integer productId) throws Exception {
        final List<Comment> comments = new ArrayList<Comment>();
        final String sql = "SELECT id, text FROM product_comments WHERE product_id = ? ORDER BY id";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, productId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    comments.add(EntityMapper.mapComment(resultSet));
                }
            }
        }
        return comments;
    }

}
