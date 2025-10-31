package com.microservice.product.service;

import ch.qos.logback.core.util.StringUtil;
import com.microservice.product.VOs.ProductVo;
import com.microservice.product.exceptions.ResourceNotFoundException;
import com.microservice.product.model.Product;
import com.microservice.product.repo.ProductRepository;
import com.microservice.product.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Response list() {
        List<Product> all = productRepository.findAll();
        if(all.isEmpty()){
            throw new ResourceNotFoundException("No products found");
        }
        List<ProductVo> mapped = all.stream().map(this::mapToVo).collect(Collectors.toList());
        return Response.setResponse(true, "data fetched successfully", mapped);
    }

    @Transactional
    public Response create(ProductVo productVo) {
        boolean isUpdate = productVo.getId() != null && productVo.getId() > 0;
        validations(productVo, isUpdate);
        Product product = getOrCreateProduct(productVo.getId());
        mapToEntity(productVo, product);
        Product savedProduct = productRepository.save(product);
        ProductVo mapped = mapToVo(savedProduct);
        return Response.setResponse(true, "Product created successfully", mapped);
    }

    @Transactional
    public Response getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        ProductVo mapped = mapToVo(product);
        return Response.setResponse(true, "Product fetched successfully", mapped);
    }

    @Transactional
    public Response deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
        return Response.setResponse(true, "Product deleted successfully", null);
    }

    private Product getOrCreateProduct(Long id) {
        if (id == null) {
            return new Product();
        }
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found for ID: " + id));
    }


    private void validations(ProductVo productVo, boolean isUpdate) {
        if(Objects.isNull(productVo)){
            throw new ResourceNotFoundException("Product data is missing");
        }
        if(!isUpdate){
            if(StringUtil.notNullNorEmpty(productVo.getName())){
                Product byName = productRepository.findByName(productVo.getName());
                if(byName != null){
                    throw new ResourceNotFoundException("Product name already exists");
                }
            }
        }else{
            if(productVo.getId() == null || productVo.getId() <= 0) {
                throw new IllegalArgumentException("Product ID is required for update");
            }
        }
    }


    private void mapToEntity(ProductVo productVo, Product product) {
        product.setName(productVo.getName());
        product.setDescription(productVo.getDescription());
        product.setPrice(productVo.getPrice());
    }

    private ProductVo mapToVo(Product product) {
        ProductVo productVo = new ProductVo();
        productVo.setId(product.getId());
        productVo.setName(product.getName());
        productVo.setDescription(product.getDescription());
        productVo.setPrice(product.getPrice());
        return productVo;
    }


}
