/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package es.mariana.dweb.tienda.model.services;

import es.mariana.dweb.tienda.model.entities.Order;
import es.mariana.dweb.tienda.model.entities.OrderLine;
import es.mariana.dweb.tienda.model.entities.Product;
import es.mariana.dweb.tienda.model.entities.Customer;
import es.mariana.dweb.tienda.model.entities.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    public OrderService() {
        super();
    }
    
    
    
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(final Integer id) {
        return orderRepository.findById(id);
    }

    public List<Order> findByCustomerId(final Integer customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public Order create(final Integer customerId, final LocalDate date, final List<OrderLine> orderLines) {
        final Order order = buildOrder(null, customerId, date, orderLines);
        return this.orderRepository.save(order);
    }

    public void update(final Integer id, final Integer customerId, final LocalDate date, final List<OrderLine> orderLines) {
        final Order order = buildOrder(id, customerId, date, orderLines);
        this.orderRepository.update(order);
    }

    public void deleteById(final Integer id) {
        this.orderRepository.deleteById(id);
    }

    public OrderLine buildOrderLine(final Integer productId, final Integer amount, final BigDecimal purchasePrice) {
        final Product product = new Product();
        product.setId(productId);

        final OrderLine orderLine = new OrderLine();
        orderLine.setProduct(product);
        orderLine.setAmount(amount);
        orderLine.setPurchasePrice(purchasePrice);
        return orderLine;
    }

    private Order buildOrder(final Integer id, final Integer customerId, final LocalDate date, final List<OrderLine> orderLines) {
        final Customer customer = new Customer();
        customer.setId(customerId);

        final Order order = new Order();
        order.setId(id);
        order.setCustomer(customer);
        order.setDate(GregorianCalendar.from(date.atTime(LocalTime.NOON).atZone(ZoneId.systemDefault())));
        order.getOrderLines().addAll(orderLines);
        return order;
    }
    
}
