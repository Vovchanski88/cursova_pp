package database;

import gemstones.Gemstone;
import necklace.Necklace;
import utils.LoggerUtil;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:h2:./db/necklace_db;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASS = "";

    static {
        try {
            Class.forName("org.h2.Driver");
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("H2 JDBC Driver not found", e);
        }
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Створення таблиць
            stmt.execute("CREATE TABLE IF NOT EXISTS necklaces (" +
                       "id INT AUTO_INCREMENT PRIMARY KEY)");

            stmt.execute("CREATE TABLE IF NOT EXISTS gemstones (" +
                       "id INT AUTO_INCREMENT PRIMARY KEY, " +
                       "necklace_id INT, " +
                       "name VARCHAR(50), " +
                       "weight DOUBLE, " +
                       "price DOUBLE, " +
                       "transparency DOUBLE, " +
                       "type VARCHAR(20), " +
                       "FOREIGN KEY (necklace_id) REFERENCES necklaces(id))");

        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static void saveNecklace(Necklace necklace) {
        try (Connection conn = getConnection()) {
            LoggerUtil.logInfo("Початок збереження намиста з ID: " + necklace.getId());
            conn.setAutoCommit(false);

            // Збереження намиста
            if (necklace.getId() == 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO necklaces DEFAULT VALUES", Statement.RETURN_GENERATED_KEYS)) {
                    ps.executeUpdate();
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        necklace.setId(rs.getInt(1));
                    }
                }
            }

            // Видалення старих каменів
            try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM gemstones WHERE necklace_id = ?")) {
                ps.setInt(1, necklace.getId());
                ps.executeUpdate();
            }

            // Додавання нових каменів
            try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO gemstones (necklace_id, name, weight, price, transparency, type) " +
                "VALUES (?, ?, ?, ?, ?, ?)")) {

                for (Gemstone gem : necklace.getGemstones()) {
                    ps.setInt(1, necklace.getId());
                    ps.setString(2, gem.getName());
                    ps.setDouble(3, gem.getWeight());
                    ps.setDouble(4, gem.getPrice());
                    ps.setDouble(5, gem.getTransparency());
                    ps.setString(6, gem.getType());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            LoggerUtil.logInfo("Успішно збережено намисто з ID: " + necklace.getId());
        } catch (SQLException e) {
            LoggerUtil.logError("Помилка при збереженні намиста", e);
            throw new RuntimeException("Failed to save necklace", e);
        }
    }

    public static Necklace loadNecklace() {
        Necklace necklace = new Necklace();
        try (Connection conn = getConnection()) {
            // Завантаження намиста
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id FROM necklaces LIMIT 1")) {

                if (rs.next()) {
                    necklace.setId(rs.getInt("id"));

                    // Завантаження каменів
                    try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT * FROM gemstones WHERE necklace_id = ?")) {

                        ps.setInt(1, necklace.getId());
                        ResultSet gemRs = ps.executeQuery();

                        while (gemRs.next()) {
                            Gemstone gem = new Gemstone();
                            gem.setId(gemRs.getInt("id"));
                            gem.setName(gemRs.getString("name"));
                            gem.setWeight(gemRs.getDouble("weight"));
                            gem.setPrice(gemRs.getDouble("price"));
                            gem.setTransparency(gemRs.getDouble("transparency"));
                            gem.setType(gemRs.getString("type"));

                            necklace.addGemstone(gem);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load main.necklace", e);
        }
        return necklace;
    }

    public static void close() {
    }
}