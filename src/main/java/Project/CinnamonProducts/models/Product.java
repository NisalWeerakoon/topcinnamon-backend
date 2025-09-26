package Project.CinnamonProducts.models;

import jakarta.persistence.*;

@Entity
@Table( name = "products")



public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private String name;
    private String category;
    private double price;
    private int stock_quantity;

    @Column(columnDefinition = "TEXT")
    private String description;
    private String imageFilename;


    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStock_quantity(int stock_quantity) {
        this.stock_quantity = stock_quantity;
    }

    public void setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
    }



    public Long getId() {return id;}

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public int getStock_quantity() {
        return stock_quantity;
    }

    public String getDescription() {
        return description;
    }

    public String getImageFilename() {
        return imageFilename;
    }

}
