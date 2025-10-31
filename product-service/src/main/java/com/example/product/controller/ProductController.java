package com.example.product.controller;

import com.example.product.VOs.ProductVo;
import com.example.product.customAnnotations.CurrentUser;
import com.example.product.model.Product;
import com.example.product.repo.ProductRepository;
import com.example.product.response.Response;
import com.example.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
    public ResponseEntity<?> create(@CurrentUser String username,
            @Valid @RequestBody ProductVo productVo){
        System.out.println("Current User: " + username);
        Response response = productService.create(productVo);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id){
        Response response = productService.getProductById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody ProductVo productVo){
        Response response = productService.create(productVo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Response response = productService.deleteProduct(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
