package necklace;

import gemstones.Gemstone;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Necklace {
    private int id;
    private List<Gemstone> gemstones = new ArrayList<>();

    public Necklace() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public void addGemstone(Gemstone gemstone) {
        gemstones.add(gemstone);
    }

    public List<Gemstone> getGemstones() {
        return gemstones;
    }

    public double calculateTotalWeight() {
        return gemstones.stream().mapToDouble(Gemstone::getWeight).sum();
    }

    public double calculateTotalPrice() {
        return gemstones.stream().mapToDouble(Gemstone::getPrice).sum();
    }

    public List<Gemstone> getSortedByTransparency() {
        return gemstones.stream()
                .sorted(Comparator.comparingDouble(Gemstone::getTransparency))
                .toList();
    }
    public boolean removeGemstone(int id) {
        return gemstones.removeIf(gem -> gem.getId() == id);
    }

    public List<Gemstone> findGemstonesByTransparency(double min, double max) {
        return gemstones.stream()
                .filter(g -> g.getTransparency() >= min && g.getTransparency() <= max)
                .toList();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Намисто (ID: " + id + "):\n");
        gemstones.forEach(g -> sb.append(g).append("\n"));
        return sb.toString();
    }
}