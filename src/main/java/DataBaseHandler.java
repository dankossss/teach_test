import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class DataBaseHandler {

    protected Connection dbConnection;
    private String password = new String();

    protected DataBaseHandler() {
        this.dbConnection = getDbConnection();
    }

    public Connection getDbConnection() {
        if (dbConnection == null) {
            try {
                Properties connectionProperties = new Properties();
                connectionProperties.load(new FileReader("C:\\Users\\colibri\\IdeaProjects\\store\\local.properties"));
                password = connectionProperties.getProperty("dbPass");
                String connectionString = "jdbc:mysql://" +
                        connectionProperties.getProperty("dbHost") + ":" +
                        connectionProperties.getProperty("dbPort") + "/" +
                        connectionProperties.getProperty("dbName");
                Class.forName("com.mysql.cj.jdbc.Driver");
                dbConnection = DriverManager.getConnection(
                        connectionString,
                        connectionProperties.getProperty("dbUser"),
                        connectionProperties.getProperty("dbPass"));
                Statement statement = dbConnection.createStatement();
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS products (" +
                        "id             INTEGER     NOT NULL    AUTO_INCREMENT  PRIMARY KEY," +
                        "name           VARCHAR(45) NOT NULL," +
                        "price          INTEGER     NOT NULL," +
                        "status         ENUM('out_of_stock', 'in_stock', 'running_low')," +
                        "created_at     DATETIME    NOT NULL);");
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS orders (" +
                        "id             INTEGER     NOT NULL    AUTO_INCREMENT  PRIMARY KEY," +
                        "user_id        INTEGER     NOT NULL," +
                        "status         VARCHAR(45) NOT NULL," +
                        "created_at     VARCHAR(45) NOT NULL);");
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS order_items (" +
                        "order_id       INTEGER     NOT NULL," +
                        "product_id     INTEGER," +
                        "quantity       INTEGER     NOT NULL    DEFAULT 1," +
                        "FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE ON UPDATE CASCADE," +
                        "FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE SET NULL ON UPDATE SET NULL);");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return dbConnection;
    }

    public void createProduct(String[] args) {
        try (Statement statement = getDbConnection().createStatement()) {
            String insert = "INSERT INTO products(name, price, status, created_at) " +
                    "VALUES('"
                    + args[1].trim() + "', " +
                    Integer.parseInt(args[2].trim()) + ", " +
                    Integer.parseInt(args[3].trim()) + ", '" +
                    new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()) + "')";
            statement.executeUpdate(insert);
            System.out.println(" Successfully");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("There are not enough arguments, Check your query please.");
        }
    }

    public void createOrder(String[] args) {
        try (Statement statementInsert = getDbConnection().createStatement();
             Statement statementSelect = getDbConnection().createStatement()) {
            int orderId = Integer.parseInt(args[1].trim());
            int user_id = (int) Math.round(Math.random() * 100);
            String status = "processing";
            String insertOrder = "INSERT INTO orders(user_id, status, created_at) VALUES(" +
                    user_id + ", '" +
                    status.trim() + "', '" +
                    new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()) + "')";
            statementInsert.executeUpdate(insertOrder);
            ResultSet resultSet = statementSelect.executeQuery("SELECT id FROM orders WHERE user_id = " + user_id);
            while (resultSet.next()) {
                orderId = resultSet.getInt(1);
            }
            String existProducts = "";
            int count = 0;
            for (int i = 1; i < args.length; i++) {
                ResultSet set = statementSelect.executeQuery("SELECT COUNT(id) FROM products WHERE id = " + args[i].trim());
                set.next();
                if (set.getInt(1) == 0) {
                    existProducts = existProducts + args[i].trim() + ", ";
                    count++;
                } else {
                    String insertOrderItems = "INSERT INTO order_items(order_id, product_id, quantity) VALUES(" +
                            orderId + ", " +
                            args[i] + ", " +
                            1 + ");";
                    statementInsert.executeUpdate(insertOrderItems);
                }
            }
            if (count == 1) System.out.println("Product " + existProducts + "is not exist in the table products");
            if (count > 1) System.out.println("Products " + existProducts + "are not exist in the table products");
            else
                resultSet.close();
            System.out.println(" Successfully");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("You didn't enter any product");
        }
    }

    public void updateOrder(String[] args) {
        if (args.length < 4) System.out.println("There are not enough arguments");
        else {
            try (Statement statementInsert = getDbConnection().createStatement();
                 Statement statementQuery = getDbConnection().createStatement()) {
                ResultSet order_id = statementQuery.executeQuery("SELECT COUNT(order_id) FROM order_items WHERE order_id = " + args[1].trim() + ";");
                order_id.next();
                if (order_id.getInt(1) != 0) {
                    ResultSet product_id = statementQuery.executeQuery("SELECT COUNT(product_id) FROM order_items WHERE product_id = " + args[2].trim() + " AND order_id = " + args[1] + ";");
                    product_id.next();
                    if (product_id.getInt(1) != 0) {
                        statementInsert.executeUpdate("UPDATE order_items SET quantity = " + args[3].trim() + " WHERE order_id = " + args[1].trim() + " AND product_id = " + args[2].trim() + ";");
                        System.out.println(" Successfully");
                    } else System.out.println("There is not not exist that product");
                } else System.out.println("There is not not exist that order");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("There are not enough arguments, Check your query please.");
            }
        }
    }

    public void allProducts() {
        try (Statement selectMaxLengthLine = getDbConnection().createStatement();
             Statement selectAllProducts = getDbConnection().createStatement()) {
            String[] columnName = {"Product name", "Product price", "Product status"};
            ResultSet maxLengthLine = selectMaxLengthLine.executeQuery("SELECT MAX(CHAR_LENGTH(name)), MAX(CHAR_LENGTH(price)), MAX(CHAR_LENGTH(status)) FROM products;");
            ResultSet allProductsSet = selectAllProducts.executeQuery("SELECT name, price, status FROM Products;");
            CreationTable.drawTableView(columnName, allProductsSet, maxLengthLine);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void orderedProducts() {
        try (Statement selectMaxLengthLine = getDbConnection().createStatement();
             Statement selectOrderedProducts = getDbConnection().createStatement()) {
            String[] columnName = {"Ordered Products"};
            String selectList = "SELECT DISTINCT name FROM order_items, products WHERE order_items.product_id = products.id;";
            ResultSet orderedProductsSet = selectOrderedProducts.executeQuery(selectList);
            ResultSet maxLengthlLineSet = selectMaxLengthLine.executeQuery("SELECT MAX(LENGTH(name)) FROM products;");
            CreationTable.drawTableView(columnName, orderedProductsSet, maxLengthlLineSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void allOrder() {
        try (Statement maxLengthlLine = getDbConnection().createStatement();
             Statement allOrders = getDbConnection().createStatement()) {
            String[] columnName = {"Order ID", "Product total Price", "Product Name", "Products Quantity in order Entry", "Order created Date",};
            ResultSet maxLengthlLineSet = maxLengthlLine.executeQuery("SELECT MAX(LENGTH(orders.id)), MAX(LENGTH(price)), MAX(LENGTH(name)), MAX(LENGTH(quantity)), MAX(LENGTH(orders.created_at)) FROM products, orders, order_items WHERE order_items.order_id = orders.id AND order_items.product_id = products.id;");
            ResultSet allOrdersSet = allOrders.executeQuery("SELECT orders.id, price*quantity AS total_price, name, quantity, orders.created_at " +
                    "FROM orders, products, order_items " +
                    "WHERE order_items.order_id = orders.id AND order_items.product_id = products.id " +
                    "ORDER BY id;");
            CreationTable.drawTableView(columnName, allOrdersSet, maxLengthlLineSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void removeProductByID(String[] args) {
        try (Statement statement = getDbConnection().createStatement()) {
            String removeQuery = "DELETE FROM Products WHERE id = " + args[1] + ";";
            statement.executeUpdate(removeQuery);
            System.out.println(" Successfully");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("You didn't enter id product which must be removed");
        }
    }

    public void removeAllProducts(String[] args) {
        try (Statement statement = getDbConnection().createStatement()) {
            if (!args[1].trim().equals(password)) System.out.println("Password isn't correct");
            else {
                statement.executeUpdate("DELETE FROM products;");
                System.out.println(" Successfully");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("You didn't enter password");
        }
    }
}
