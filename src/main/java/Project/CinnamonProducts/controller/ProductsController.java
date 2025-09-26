package Project.CinnamonProducts.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import Project.CinnamonProducts.repository.ProductRepository;
import Project.CinnamonProducts.models.Product;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/products")

public class ProductsController {

    @Autowired
    private ProductRepository repo;

    // GET all products: http://localhost:8081/products
    @GetMapping
    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    // GET single product by ID: http://localhost:8081/products/1
    @GetMapping("/{id}")
    public Product getName(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    // POST new product: http://localhost:8081/products
    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return repo.save(product);
    }

    // PUT update product: http://localhost:8081/products/1
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        return repo.save(product);
    }

    // DELETE product: http://localhost:8081/products/1
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        repo.deleteById(id);
    }

    @GetMapping("/test")
    public String test() {
        return "Controller is working!";
    }

    @GetMapping("/manage")
    public String managePage() {
        return "products";
    }
}
