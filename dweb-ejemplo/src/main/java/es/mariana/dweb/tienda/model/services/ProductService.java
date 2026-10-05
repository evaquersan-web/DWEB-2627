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


import es.mariana.dweb.tienda.model.entities.Product;
import es.mariana.dweb.tienda.model.entities.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    public ProductService() {
        super();
    }
    
    
    
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(final Integer id) {
        return productRepository.findById(id);
    }

    public Product create(final String name, final boolean inStock, final BigDecimal price) {
        final Product product = new Product();
        product.setName(name);
        product.setInStock(inStock);
        product.setPrice(price);
        return this.productRepository.save(product);
    }

    public void update(final Integer id, final String name, final boolean inStock, final BigDecimal price) {
        final Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setInStock(inStock);
        product.setPrice(price);
        this.productRepository.update(product);
    }

    public void deleteById(final Integer id) {
        this.productRepository.deleteById(id);
    }
    
}
