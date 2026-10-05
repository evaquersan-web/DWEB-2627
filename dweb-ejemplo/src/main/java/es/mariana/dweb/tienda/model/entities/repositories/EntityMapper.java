package es.mariana.dweb.tienda.model.entities.repositories;

import es.mariana.dweb.tienda.model.entities.Comment;
import es.mariana.dweb.tienda.model.entities.Customer;
import es.mariana.dweb.tienda.model.entities.Product;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.GregorianCalendar;

final class EntityMapper {

    private EntityMapper() {
    }

    static Product mapProduct(final ResultSet resultSet) throws SQLException {
        return new Product(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getInt("in_stock") == 1,
                new BigDecimal(resultSet.getString("price")));
    }

    static Comment mapComment(final ResultSet resultSet) throws SQLException {
        return new Comment(resultSet.getInt("id"), resultSet.getString("text"));
    }

    static Customer mapCustomer(final ResultSet resultSet) throws SQLException {
        final Customer customer = new Customer();
        customer.setId(resultSet.getInt("id"));
        customer.setName(resultSet.getString("name"));
        customer.setEmail(resultSet.getString("email"));
        customer.setCustomerSince(mapCalendar(resultSet.getString("customer_since")));
        return customer;
    }

    static Calendar mapCalendar(final String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        final LocalDateTime dateTime = value.contains("T")
                ? LocalDateTime.parse(value)
                : LocalDateTime.of(LocalDate.parse(value), LocalTime.MIDNIGHT);
        return GregorianCalendar.from(dateTime.atZone(ZoneId.systemDefault()));
    }

}
