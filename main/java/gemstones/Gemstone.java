package gemstones;

import java.io.Serializable;

public class Gemstone implements Serializable {
    private int id;
    private String name;
    private double weight;
    private double price;
    private double transparency;
    private String type;

    public Gemstone() {}

    public Gemstone(String type, String name, double weight, double price, double transparency) {
        this.type = type;
        this.name = name;
        this.weight = weight;
        this.price = price;
        this.transparency = transparency;
    }

    // Гетери та сетери
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getTransparency() { return transparency; }
    public void setTransparency(double transparency) { this.transparency = transparency; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @Override
    public String toString() {
        return String.format("%s (%.2f карат, %.2f грн, %.2f%%)",
               name, weight, price, transparency);
    }
}