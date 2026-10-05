package es.mariana.dweb.tienda.model.entities.repositories;

import es.mariana.dweb.tienda.model.entities.Order;
import es.mariana.dweb.tienda.model.entities.OrderLine;
import es.mariana.dweb.tienda.model.entities.Product;
import es.mariana.dweb.tienda.model.entities.Customer;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Repository
public class OrderRepository {

    private final SqliteConnectionProvider connectionProvider;

    public OrderRepository(final SqliteConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public List<Order> findAll() {
        final List<Order> orders = new ArrayList<Order>();
        final String sql = """
                SELECT o.id, o.order_date, c.id AS customer_id, c.name, c.email, c.customer_since
                FROM orders o
                JOIN customers c ON c.id = o.customer_id
                ORDER BY o.id
                """;

        try (Connection connection = this.connectionProvider.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                orders.add(mapOrder(connection, resultSet));
            }
            return orders;
        } catch (final Exception e) {
            throw new IllegalStateException("Could not find orders", e);
        }
    }

    public Order findById(final Integer id) {
        final String sql = """
                SELECT o.id, o.order_date, c.id AS customer_id, c.name, c.email, c.customer_since
                FROM orders o
                JOIN customers c ON c.id = o.customer_id
                WHERE o.id = ?
                """;

        try (Connection connection = this.connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return mapOrder(connection, resultSet);
            }
        } catch (final Exception e) {
            throw new IllegalStateException("Could not find order with id " + id, e);
        }
    }

    public List<Order> findByCustomerId(final Integer customerId) {
        final List<Order> orders = new ArrayList<Order>();
        final String sql = """
                SELECT o.id, o.order_date, c.id AS customer_id, c.name, c.email, c.customer_since
                FROM orders o
                JOIN customers c ON c.id = o.customer_id
                WHERE c.id = ?
                ORDER BY o.id
                """;

        try (Connection connection = this.connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    orders.add(mapOrder(connection, resultSet));
                }
            }
            return orders;
        } catch (final Exception e) {
            throw new IllegalStateException("Could not find orders for customer " + customerId, e);
        }
    }

    public Order save(final Order order) {
        final String sql = "INSERT INTO orders (customer_id, order_date) VALUES (?, ?)";

        try (Connection connection = this.connectionProvider.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, order.getCustomer().getId());
                statement.setString(2, formatCalendar(order.getDate()));
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        order.setId(generatedKeys.getInt(1));
                    }
                }
                insertOrderLines(connection, order);
                connection.commit();
                return order;
            } catch (final Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (final Exception e) {
            throw new IllegalStateException("Could not save order", e);
        }
    }

    public void update(final Order order) {
        final String sql = "UPDATE orders SET customer_id = ?, order_date = ? WHERE id = ?";

        try (Connection connection = this.connectionProvider.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, order.getCustomer().getId());
                statement.setString(2, formatCalendar(order.getDate()));
                statement.setInt(3, order.getId());
                statement.executeUpdate();
                deleteOrderLines(connection, order.getId());
                insertOrderLines(connection, order);
                connection.commit();
            } catch (final Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (final Exception e) {
            throw new IllegalStateException("Could not update order with id " + order.getId(), e);
        }
    }

    public void deleteById(final Integer id) {
        try (Connection connection = this.connectionProvider.getConnection()) {
            connection.setAutoCommit(false);
            try {
                deleteOrderLines(connection, id);
                try (PreparedStatement statement = connection.prepareStatement("DELETE FROM orders WHERE id = ?")) {
                    statement.setInt(1, id);
                    statement.executeUpdate();
                }
                connection.commit();
            } catch (final Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (final Exception e) {
            throw new IllegalStateException("Could not delete order with id " + id, e);
        }
    }

    private Order mapOrder(final Connection connection, final ResultSet resultSet) throws Exception {
        final Customer customer = new Customer();
        customer.setId(resultSet.getInt("customer_id"));
        customer.setName(resultSet.getString("name"));
        customer.setEmail(resultSet.getString("email"));
        customer.setCustomerSince(EntityMapper.mapCalendar(resultSet.getString("customer_since")));

        final Order order = new Order();
        order.setId(resultSet.getInt("id"));
        order.setDate(EntityMapper.mapCalendar(resultSet.getString("order_date")));
        order.setCustomer(customer);
        order.getOrderLines().addAll(findOrderLines(connection, order.getId()));
        return order;
    }

    private List<OrderLine> findOrderLines(final Connection connection, final Integer orderId) throws Exception {
        final List<OrderLine> orderLines = new ArrayList<OrderLine>();
        final String sql = """
                SELECT
                    ol.amount,
                    ol.purchase_price,
                    p.id,
                    p.name,
                    p.in_stock,
                    p.price
                FROM order_lines ol
                JOIN products p ON p.id = ol.product_id
                WHERE ol.order_id = ?
                ORDER BY ol.id
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, orderId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    final Product product = EntityMapper.mapProduct(resultSet);
                    final OrderLine orderLine = new OrderLine();
                    orderLine.setProduct(product);
                    orderLine.setAmount(resultSet.getInt("amount"));
                    orderLine.setPurchasePrice(new BigDecimal(resultSet.getString("purchase_price")));
                    orderLines.add(orderLine);
                }
            }
        }
        return orderLines;
    }

    private void insertOrderLines(final Connection connection, final Order order) throws Exception {
        final String sql = "INSERT INTO order_lines (order_id, product_id, amount, purchase_price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (final OrderLine orderLine : order.getOrderLines()) {
                statement.setInt(1, order.getId());
                statement.setInt(2, orderLine.getProduct().getId());
                statement.setInt(3, orderLine.getAmount());
                statement.setString(4, orderLine.getPurchasePrice().toPlainString());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void deleteOrderLines(final Connection connection, final Integer orderId) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM order_lines WHERE order_id = ?")) {
            statement.setInt(1, orderId);
            statement.executeUpdate();
        }
    }

    private String formatCalendar(final Calendar calendar) {
        return LocalDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId()).toString();
    }

}
