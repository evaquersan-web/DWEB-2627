package es.mariana.dweb.tienda.model.entities.repositories;

import es.mariana.dweb.tienda.model.entities.Customer;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Repository
public class CustomerRepository {

    private final SqliteConnectionProvider connectionProvider;

    public CustomerRepository(final SqliteConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public List<Customer> findAll() {
        final List<Customer> customers = new ArrayList<Customer>();
        final String sql = "SELECT id, name, email, customer_since FROM customers ORDER BY id";

        try (Connection connection = this.connectionProvider.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                customers.add(EntityMapper.mapCustomer(resultSet));
            }
            return customers;
        } catch (final Exception e) {
            throw new IllegalStateException("Could not find customers", e);
        }
    }

    public Customer findById(final Integer id) {
        final String sql = "SELECT id, name, email, customer_since FROM customers WHERE id = ?";

        try (Connection connection = this.connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return EntityMapper.mapCustomer(resultSet);
            }
        } catch (final Exception e) {
            throw new IllegalStateException("Could not find customer with id " + id, e);
        }
    }

    public Customer save(final Customer customer) {
        final String sql = "INSERT INTO customers (name, email, customer_since) VALUES (?, ?, ?)";
        final Calendar customerSince = customer.getCustomerSince() == null ? Calendar.getInstance() : customer.getCustomerSince();

        try (Connection connection = this.connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getEmail());
            statement.setString(3, formatCalendar(customerSince));
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    customer.setId(generatedKeys.getInt(1));
                }
            }
            customer.setCustomerSince(customerSince);
            return customer;
        } catch (final Exception e) {
            throw new IllegalStateException("Could not save customer", e);
        }
    }

    public void update(final Customer customer) {
        final String sql = "UPDATE customers SET name = ?, email = ? WHERE id = ?";

        try (Connection connection = this.connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getEmail());
            statement.setInt(3, customer.getId());
            statement.executeUpdate();
        } catch (final Exception e) {
            throw new IllegalStateException("Could not update customer with id " + customer.getId(), e);
        }
    }

    public void deleteById(final Integer id) {
        try (Connection connection = this.connectionProvider.getConnection()) {
            connection.setAutoCommit(false);
            try {
                deleteOrderLinesForCustomer(connection, id);
                executeDelete(connection, "DELETE FROM orders WHERE customer_id = ?", id);
                executeDelete(connection, "DELETE FROM customers WHERE id = ?", id);
                connection.commit();
            } catch (final Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (final Exception e) {
            throw new IllegalStateException("Could not delete customer with id " + id, e);
        }
    }

    private void deleteOrderLinesForCustomer(final Connection connection, final Integer customerId) throws Exception {
        final String sql = """
                DELETE FROM order_lines
                WHERE order_id IN (
                    SELECT id FROM orders WHERE customer_id = ?
                )
                """;
        executeDelete(connection, sql, customerId);
    }

    private void executeDelete(final Connection connection, final String sql, final Integer id) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    private String formatCalendar(final Calendar calendar) {
        return LocalDate.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId()).toString();
    }

}
