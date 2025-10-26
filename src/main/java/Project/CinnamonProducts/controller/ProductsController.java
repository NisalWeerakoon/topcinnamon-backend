package Project.CinnamonProducts.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import Project.CinnamonProducts.repository.ProductRepository;
import Project.CinnamonProducts.model.Product;
import org.springframework.web.bind.annotation.*;



@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})

@RestController
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductRepository repo;

    // GET all products
    @GetMapping
    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    // GET products by category
    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        return repo.findByCategory(category);
    }

    // GET test endpoint
    @GetMapping("/test")
    public String test() {
        return "Controller is working!";
    }

    

    // GET single product by ID
    @GetMapping("/{id}")
    public Product getName(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    // POST new product
    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return repo.save(product);
    }

    // PUT update product
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        return repo.save(product);
    }

    // DELETE product
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
