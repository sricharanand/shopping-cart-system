import java.sql.*;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.InputMismatchException;

class BillItem
{
    String itemName;
    int quantity;
    double price;

    public BillItem(String itemName, int quantity, double price)
    {
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
    }
};

class Bill
{
    int userId;
    List<BillItem> items = new ArrayList<>();
    double total;

    public Bill(int userId)
    {
        this.userId = userId;
    }

    public void addItem(String itemName, int quantity, double price)
    {
        BillItem item = new BillItem(itemName, quantity, price);
        items.add(item);
        total += quantity * price;
    }

    public void printBill()
    {
        System.out.println("\n----- BILL -----");
        for (BillItem item : items)
            System.out.println(item.itemName + " -> Quantity = " + item.quantity + " Total Cost = " + (item.quantity * item.price));

        System.out.println("TOTAL: " + total);
        System.out.println("----------------\n");
    }
};

public class CartApp
{
    // Set JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/SHOP";

    // Database credentials
    static final String USER = "root"; // enter your MySQL user
    static final String PASSWORD = "root";// enter password

    public static void main(String[] args)
    {
        try 
        {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            Scanner s = new Scanner(System.in);
            int command, userId, cartId, productId, quantity;

            // LOG IN
            System.out.print("Enter User Name: ");
            String user_name = s.nextLine();

            userId = login(conn, user_name);
            if (userId == -1)
            {
                System.out.println("User does not exist");
                return;
            }

            String getIdStatement = "SELECT cart_id FROM Cart WHERE user_id = ?";
            PreparedStatement psCart = conn.prepareStatement(getIdStatement);
            psCart.setInt(1, userId);

            ResultSet rsCart = psCart.executeQuery();
            if (!rsCart.next())
            {
                System.out.println("Cannot find user");
                return;
            }

            cartId = rsCart.getInt("cart_id");

            while (true)
            {
                System.out.println("1. Add Item to Cart");
                System.out.println("2. Checkout");
                System.out.println("3. View Cart");
                System.out.println("4. Exit");

                System.out.println("");
                System.out.print("Choose a Menu Item Number: ");
                try 
                {
                    command = s.nextInt();
                } 
                catch (InputMismatchException e) 
                {
                    System.out.println("Enter a Number");
                    return;
                }

                // Add to Cart
                if (command == 1)
                {
                    String statement = "SELECT * FROM Product";
                    PreparedStatement showProducts = conn.prepareStatement(statement);
                    ResultSet products = showProducts.executeQuery();

                    System.out.println("\nAvailable Products:");
                    System.out.println("ID | Name | Price | Stock Available");
                    while (products.next())
                    {
                        System.out.println(products.getInt("product_id") + " | " +
                                products.getString("product_name") + " | " +
                                products.getDouble("price") + " | " +
                                products.getInt("quantity_in_stock")
                        );
                    }
                    System.out.println();

                    System.out.println("Note: Stocks are Updated only After Checkout!");
                    System.out.println("");

                    System.out.print("Choose Product_ID to Purchase: ");
                    try 
                    {
                        productId = s.nextInt();
                    } 
                    catch (InputMismatchException e) 
                    {
                        System.out.println("Enter a Number");
                        return;
                    }

                    System.out.print("Enter quantity to Purchase: ");
                    try 
                    {
                        quantity = s.nextInt();
                    } 
                    catch (InputMismatchException e) 
                    {
                        System.out.println("Enter a Number");
                        return;
                    }

                    addToCart(conn, cartId, productId, quantity);
                }

                // Checkout
                else if (command == 2) 
                {
                    checkout(conn, cartId, userId);
                    conn.close();
                    s.close();
                    return;
                }

                // View Cart
                else if (command == 3)
                    viewCart(conn, cartId);
                // Exit
                else if (command == 4)
                {
                    System.out.println("Exiting...");
                    return;
                }

                else
                {
                    System.out.println("");
                    System.out.println("Enter 1, 2, 3, or 4");
                }

            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static int login(Connection conn, String userName)
    {
        String statement = "SELECT user_id FROM User WHERE user_name = ?"; // checks if any user exists with that user_name
        try
        {
            PreparedStatement psLogIn = conn.prepareStatement(statement);
            psLogIn.setString(1, userName);

            ResultSet rs = psLogIn.executeQuery();
            if (rs.next())
                return rs.getInt("user_id");

            return -1;
        }
        catch (SQLException e)
        {
            System.out.println("User Not Found");
            return -1;
        }
    }

    public static void addToCart(Connection conn, int cartId, int productId, int quantity)
    {
        try {
            if (quantity <= 0)
                throw new SQLException("Enter a Positive Number as Quantity");

            // 1. Product Exist, Quantity not Exceeding (includes checking current cart)
            String quantityCheck = "SELECT quantity_in_stock FROM Product WHERE product_id = ?";
            PreparedStatement psQuantityCheck = conn.prepareStatement(quantityCheck);
            psQuantityCheck.setInt(1, productId);

            ResultSet rs = psQuantityCheck.executeQuery();
            if (!rs.next())
                throw new SQLException("Invalid Product ID");

            int available = rs.getInt("quantity_in_stock");

            String existingStatement = "SELECT quantity FROM Cart_Item WHERE cart_id = ? AND product_id = ?";
            PreparedStatement psExisting = conn.prepareStatement(existingStatement);
            psExisting.setInt(1, cartId);
            psExisting.setInt(2, productId);

            ResultSet existingCart = psExisting.executeQuery();

            int existingQty = 0;
            if (existingCart.next())
                existingQty = existingCart.getInt("quantity");

            int totalRequested = existingQty + quantity;

            if (totalRequested > available)
                throw new SQLException("Insufficient Stock");

            String insertStatement = "INSERT INTO Cart_Item(cart_id, product_id, quantity) VALUES (?, ?, ?) ";
            insertStatement += "ON DUPLICATE KEY UPDATE quantity = quantity + ?";

            PreparedStatement psInsert = conn.prepareStatement(insertStatement);
            psInsert.setInt(1, cartId);
            psInsert.setInt(2, productId);
            psInsert.setInt(3, quantity);
            psInsert.setInt(4, quantity);
            psInsert.executeUpdate();
            psInsert.close();
            System.out.println("Added to Cart");
        }
        catch (SQLException e)
        {
            System.out.println("Add Failed: " + e.getMessage());
        }
    }

    public static void viewCart(Connection conn, int cartId) throws SQLException
    {
        String statement = "SELECT c_item.product_id, c_item.quantity, p.price, p.product_name " +
                "FROM Cart c " +
                "JOIN Cart_Item c_item ON c.cart_id = c_item.cart_id " +
                "JOIN Product p ON c_item.product_id = p.product_id " +
                "WHERE c.cart_id = ?";
        PreparedStatement ps = conn.prepareStatement(statement);
        ps.setInt(1, cartId);

        ResultSet rs = ps.executeQuery();

        System.out.println("\n--- CURRENT CART ---");

        boolean empty = true;

        while (rs.next())
        {
            empty = false;
            System.out.println(rs.getString("product_name") + " -> Quantity = " + rs.getInt("quantity") + " Total Cost = " + (rs.getInt("quantity") * rs.getDouble("price")));
        }

        if (empty)
            System.out.println("Cart is Empty");

        System.out.println("------------------\n");
    }

    public static void checkout(Connection conn, int cartId, int userId) throws SQLException
    {
        try
        {
            conn.setAutoCommit(false);

            // Getting the user's cart items
            String getCart = "SELECT c_item.product_id, c_item.quantity, p.price, p.product_name " +
                             "FROM Cart c " + 
                             "JOIN Cart_Item c_item ON c.cart_id = c_item.cart_id " +
                             "JOIN Product p ON c_item.product_id = p.product_id " + 
                             "WHERE c.cart_id = ?";

            PreparedStatement psGetCart = conn.prepareStatement(getCart);
            psGetCart.setInt(1, cartId);

            ResultSet userCart = psGetCart.executeQuery();

            if (!userCart.next())
                throw new SQLException("Cart Empty");

            // Calculate total cost of cart
            double cost = 0;

            // .next happens, so do-while (to account for the first row)
            Bill bill = new Bill(userId);
            do
            {
                int quantity = userCart.getInt("quantity");
                double price = userCart.getDouble("price");
                cost += quantity * price;

                String itemName = userCart.getString("product_name");
                bill.addItem(itemName, quantity, price);
            } while (userCart.next());

            // Update Inventory
            PreparedStatement psAgain = conn.prepareStatement(getCart);
            psAgain.setInt(1, cartId);
            ResultSet userCart2 = psAgain.executeQuery();

            while (userCart2.next())
            {
                int productId = userCart2.getInt("product_id");
                int quantity = userCart2.getInt("quantity");

                String updateInventory = "UPDATE Product SET quantity_in_stock = quantity_in_stock - ? " +
                                         "WHERE product_id = ? AND quantity_in_stock >= ?";
                PreparedStatement psInventory = conn.prepareStatement(updateInventory);
                psInventory.setInt(1, quantity);
                psInventory.setInt(2, productId);
                psInventory.setInt(3, quantity);
                int rows = psInventory.executeUpdate();

                // insufficient stock
                if (rows == 0)
                    throw new SQLException("Insufficient Stock");
                psInventory.close();
            }

            // Deduct Wallet
            String deductWallet = "UPDATE Wallet SET balance = balance - ? " +
                                "WHERE user_id = ? AND balance >= ?";
            PreparedStatement psWallet = conn.prepareStatement(deductWallet);
            psWallet.setDouble(1, cost);
            psWallet.setInt(2, userId);
            psWallet.setDouble(3, cost);

            int rows = psWallet.executeUpdate();

            // Insufficient Balance
            if (rows == 0)
                throw new SQLException("Insufficient Balance");

            // Clear Cart_Items of relevant records
            String clearCart = "DELETE FROM Cart_Item WHERE cart_id = ?";
            PreparedStatement psCartClear = conn.prepareStatement(clearCart);
            psCartClear.setInt(1, cartId);
            psCartClear.executeUpdate();

            // Checkout is atomic. Fully happens or fails entirely.
            conn.commit();
            conn.setAutoCommit(true);

            System.out.println("Checkout successful. Find bill below");
            System.out.println("");
            bill.printBill();
        }
        catch (SQLException e)
        {
            conn.rollback(); // all modifications done
            conn.setAutoCommit(true);
            System.out.println(e.getMessage());
        }
        finally // anything apart from SQLException e
        {
            conn.setAutoCommit(true);
        }
    }
}