package com.microservice.product.controller;

import com.microservice.product.VOs.ProductVo;
import com.microservice.product.customAnnotations.CurrentUser;
import com.microservice.product.repo.ProductRepository;
import com.microservice.product.response.Response;
import com.microservice.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository repo;

    @Autowired
    private ProductService productService;

    @GetMapping("/get-all")
    public ResponseEntity<?> list(){
        Response response = productService.list();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<?> create(@CurrentUser("name") String username,
            @CurrentUser("id") Long userId,
            @Valid @RequestBody ProductVo productVo){
        System.out.println("Current User Name: " + username);
        System.out.println("Current User Id: " + userId);

        Response response = productService.create(productVo, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id){
        Response response = productService.getProductById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody ProductVo productVo, @CurrentUser("id") Long userId){
        Response response = productService.create(productVo, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Response response = productService.deleteProduct(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
