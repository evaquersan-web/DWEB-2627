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

import es.mariana.dweb.tienda.model.entities.Customer;
import es.mariana.dweb.tienda.model.entities.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    
    public CustomerService() {
        super();
    }
    
    
    
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Customer findById(final Integer id) {
        return customerRepository.findById(id);
    }

    public Customer register(final String name, final String email) {
        final Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        return customerRepository.save(customer);
    }

    public void update(final Integer id, final String name, final String email) {
        final Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        customer.setEmail(email);
        this.customerRepository.update(customer);
    }

    public void deleteById(final Integer id) {
        this.customerRepository.deleteById(id);
    }
    
}
