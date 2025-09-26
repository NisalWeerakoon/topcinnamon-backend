package Project.CinnamonProducts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import Project.CinnamonProducts.repository.ProductRepository;

@Controller
public class WebController {

    @Autowired
    private ProductRepository repo;

    @GetMapping("/manage") //http://localhost:8081/manage
    public String managePage(Model model) {
        model.addAttribute("products", repo.findAll());
        return "products";
    }
}