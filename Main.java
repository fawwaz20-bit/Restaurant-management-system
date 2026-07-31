import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/*
 * ==============================================================
 *  RESTAURANT MANAGEMENT SYSTEM
 *  Single-file console application (Java, no external libraries)
 *  Compile : javac Main.java
 *  Run     : java Main
 * ==============================================================
 */
public class  Main {
    // ------------------------------------------------------------
    // GLOBAL DATA STRUCTURES
    // ------------------------------------------------------------

    static Scanner scanner = new Scanner(System.in);

    static ArrayList<MenuItem> menu = new ArrayList<>();
    static ArrayList<Order> orders = new ArrayList<>();

    static int nextItemId = 1;      // auto-increment id for menu items
    static int nextOrderId = 1001;  // auto-increment id for orders

    static final String ADMIN_USERNAME = "admin";
    static final String ADMIN_PASSWORD = "1234";

    // ------------------------------------------------------------
    // NESTED DATA CLASSES
    // ------------------------------------------------------------

    // Represents one item available on the restaurant menu
    static class MenuItem {
        int id;
        String name;
        String category;
        double price;

        MenuItem(int id, String name, String category, double price) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
        }
    }

    // Represents one line item inside a placed order
    static class OrderItem {
        int itemId;
        String name;
        double price;
        int quantity;

        OrderItem(int itemId, String name, double price, int quantity) {
            this.itemId = itemId;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        double subTotal() {
            return price * quantity;
        }
    }

    // Represents one customer order
    static class Order {
        int orderId;
        String customerName;
        ArrayList<OrderItem> items;
        double totalAmount;
        String status; // PLACED, PENDING, PREPARING, COMPLETED

        Order(int orderId, String customerName, ArrayList<OrderItem> items, double totalAmount) {
            this.orderId = orderId;
            this.customerName = customerName;
            this.items = items;
            this.totalAmount = totalAmount;
            this.status = "PLACED";
        }
    }

    // ==============================================================
    // MAIN METHOD
    // ==============================================================
    public static void main(String[] args) {
        preloadMenu();
        mainMenu();
        System.out.println("\nThank you for using the Restaurant Management System. Goodbye!");
        scanner.close();
    }

    // ==============================================================
    // MAIN MENU (ENTRY POINT)
    // ==============================================================
    static void mainMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n====================================");
            System.out.println("     RESTAURANT MANAGEMENT SYSTEM");
            System.out.println("====================================");
            System.out.println("1. Client Login");
            System.out.println("2. Management Login");
            System.out.println("3. Exit");

            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    clientLogin();
                    break;
                case 2:
                    managementLogin();
                    break;
                case 3:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // ==============================================================
    // CLIENT LOGIN
    // ==============================================================
    static void clientLogin() {
        System.out.println("\n---------- CLIENT LOGIN ----------");
        String username = readLine("Username: ");
        String password = readLine("Password: ");

        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            System.out.println("Username/Password cannot be empty. Login failed.");
            return;
        }

        // For a client-facing system without a persistent database,
        // any non-empty username/password pair is treated as a valid
        // customer account, and the username is used as the customer's
        // display name throughout their session.
        System.out.println("Login successful. Welcome, " + username + "!");
        clientDashboard(username);
    }

    // ==============================================================
    // CLIENT DASHBOARD
    // ==============================================================
    static void clientDashboard(String customerName) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n====================================");
            System.out.println("           CLIENT DASHBOARD");
            System.out.println("====================================");
            System.out.println("1. View Restaurant Menu");
            System.out.println("2. Place Order");
            System.out.println("3. View Order Status");
            System.out.println("4. View Bill");
            System.out.println("5. Logout");

            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    viewMenu();
                    break;
                case 2:
                    placeOrder(customerName);
                    break;
                case 3:
                    viewOrderStatus();
                    break;
                case 4:
                    viewBill();
                    break;
                case 5:
                    loggedIn = false;
                    System.out.println("Logging out... Goodbye, " + customerName + "!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // ==============================================================
    // VIEW MENU
    // ==============================================================
    static void viewMenu() {
        boolean viewing = true;
        while (viewing) {
            System.out.println("\n---------- RESTAURANT MENU ----------");
            System.out.println("1. Veg");
            System.out.println("2. Non-Veg");
            System.out.println("3. Drinks");
            System.out.println("4. Desserts");
            System.out.println("5. Back");

            int choice = readInt("Choose a category: ");
            String category;

            switch (choice) {
                case 1:
                    category = "Veg";
                    break;
                case 2:
                    category = "Non-Veg";
                    break;
                case 3:
                    category = "Drinks";
                    break;
                case 4:
                    category = "Desserts";
                    break;
                case 5:
                    viewing = false;
                    continue;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    continue;
            }
            displayCategory(category);
        }
    }

    // Displays all menu items belonging to a given category
    static void displayCategory(String category) {
        System.out.println("\n---- " + category + " ----");
        boolean found = false;
        for (MenuItem item : menu) {
            if (item.category.equalsIgnoreCase(category)) {
                System.out.println(item.id + ". " + item.name + "  Rs." + formatPrice(item.price));
                found = true;
            }
        }
        if (!found) {
            System.out.println("No items available in this category.");
        }
    }

    // ==============================================================
    // PLACE ORDER
    // ==============================================================
    static void placeOrder(String customerName) {
        System.out.println("\n---------- PLACE ORDER ----------");

        if (menu.isEmpty()) {
            System.out.println("The menu is currently empty. Cannot place an order.");
            return;
        }

        ArrayList<OrderItem> cart = new ArrayList<>();
        boolean addingItems = true;

        while (addingItems) {
            // Step 1: choose category
            System.out.println("\nChoose a category:");
            System.out.println("1. Veg");
            System.out.println("2. Non-Veg");
            System.out.println("3. Drinks");
            System.out.println("4. Desserts");

            int catChoice = readInt("Enter category number: ");
            String category;
            switch (catChoice) {
                case 1:
                    category = "Veg";
                    break;
                case 2:
                    category = "Non-Veg";
                    break;
                case 3:
                    category = "Drinks";
                    break;
                case 4:
                    category = "Desserts";
                    break;
                default:
                    System.out.println("Invalid category. Please try again.");
                    continue;
            }

            displayCategory(category);

            // Step 2: choose item by ID
            int itemId = readInt("Enter Item ID to add to order: ");
            MenuItem selected = findMenuItemById(itemId);

            if (selected == null || !selected.category.equalsIgnoreCase(category)) {
                System.out.println("Invalid Item ID for this category. Please try again.");
                continue;
            }

            // Step 3: enter quantity
            int quantity = readInt("Enter quantity: ");
            if (quantity <= 0) {
                System.out.println("Quantity must be greater than zero.");
                continue;
            }

            cart.add(new OrderItem(selected.id, selected.name, selected.price, quantity));
            System.out.println(quantity + " x " + selected.name + " added to your order.");

            // Step 4: add another item or finish
            System.out.println("\n1. Add Another Item");
            System.out.println("2. Finish Order");
            int nextStep = readInt("Enter your choice: ");

            if (nextStep == 2) {
                addingItems = false;
            } else if (nextStep != 1) {
                System.out.println("Invalid choice. Returning to item selection.");
            }
        }

        if (cart.isEmpty()) {
            System.out.println("No items were selected. Order cancelled.");
            return;
        }

        // Calculate total
        double total = 0;
        for (OrderItem oi : cart) {
            total += oi.subTotal();
        }

        int orderId = nextOrderId++;

        // Display order summary before confirmation
        System.out.println("\n---------- ORDER SUMMARY ----------");
        System.out.println("Order ID       : " + orderId);
        System.out.println("Customer Name  : " + customerName);
        System.out.println("------------------------------------");
        System.out.printf("%-20s %-10s %-10s%n", "Item", "Qty", "Price");
        for (OrderItem oi : cart) {
            System.out.printf("%-20s %-10d Rs.%-8.2f%n", oi.name, oi.quantity, oi.subTotal());
        }
        System.out.println("------------------------------------");
        System.out.println("Total Amount   : Rs." + formatPrice(total));

        System.out.println("\n1. Confirm Order (Yes)");
        System.out.println("2. Cancel Order (No)");
        int confirm = readInt("Enter your choice: ");

        if (confirm == 1) {
            Order order = new Order(orderId, customerName, cart, total);
            orders.add(order);
            System.out.println("\nOrder Placed Successfully!");
            System.out.println("Your Order ID is: " + orderId);
        } else {
            nextOrderId--; // release the unused order id
            System.out.println("Order cancelled.");
        }
    }

    // ==============================================================
    // VIEW ORDER STATUS
    // ==============================================================
    static void viewOrderStatus() {
        System.out.println("\n---------- ORDER STATUS ----------");
        int orderId = readInt("Enter Order ID: ");

        Order order = findOrderById(orderId);
        if (order == null) {
            System.out.println("Order not found. Please check the Order ID.");
            return;
        }

        System.out.println("Order ID : " + order.orderId);
        System.out.println("Status   : " + describeStatus(order.status));
    }

    // Converts a raw status code into a descriptive message
    static String describeStatus(String status) {
        switch (status) {
            case "PLACED":
                return "PLACED (Order Confirmed)";
            case "PENDING":
                return "PENDING (Order Received)";
            case "PREPARING":
                return "PREPARING (Cooking)";
            case "COMPLETED":
                return "COMPLETED (Order Ready)";
            default:
                return status;
        }
    }

    // ==============================================================
    // VIEW BILL (CLIENT SIDE)
    // ==============================================================
    static void viewBill() {
        System.out.println("\n---------- VIEW BILL ----------");
        int orderId = readInt("Enter Order ID: ");

        Order order = findOrderById(orderId);
        if (order == null) {
            System.out.println("Order not found. Please check the Order ID.");
            return;
        }

        printBill(order);
    }

    // ==============================================================
    // MANAGEMENT LOGIN
    // ==============================================================
    static void managementLogin() {
        System.out.println("\n---------- MANAGEMENT LOGIN ----------");
        String username = readLine("Username: ");
        String password = readLine("Password: ");

        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            System.out.println("Login successful. Welcome, Admin!");
            managementDashboard();
        } else {
            System.out.println("Invalid credentials. Access denied.");
        }
    }

    // ==============================================================
    // MANAGEMENT DASHBOARD
    // ==============================================================
    static void managementDashboard() {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n====================================");
            System.out.println("         MANAGEMENT DASHBOARD");
            System.out.println("====================================");
            System.out.println("1. Add Menu Item");
            System.out.println("2. Update Menu Item");
            System.out.println("3. Delete Menu Item");
            System.out.println("4. View Menu");
            System.out.println("5. View Customer Orders");
            System.out.println("6. Update Order Status");
            System.out.println("7. Generate Bill");
            System.out.println("8. View Sales Summary");
            System.out.println("9. Logout");

            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    addMenuItem();
                    break;
                case 2:
                    updateMenuItem();
                    break;
                case 3:
                    deleteMenuItem();
                    break;
                case 4:
                    viewMenu();
                    break;
                case 5:
                    viewCustomerOrders();
                    break;
                case 6:
                    updateOrderStatus();
                    break;
                case 7:
                    generateBillManagement();
                    break;
                case 8:
                    salesSummary();
                    break;
                case 9:
                    loggedIn = false;
                    System.out.println("Logging out of Management Dashboard...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }


    // ==============================================================
    // ADD MENU ITEM
    // ==============================================================


    static void addMenuItem() {
        System.out.println("\n---------- ADD MENU ITEM ----------");
        String name = readLine("Enter Food Name: ");

        String category = readCategory();
        if (category == null) {
            System.out.println("Invalid category. Item not added.");
            return;
        }

        double price = readDouble("Enter Price: ");
        if (price < 0) {
            System.out.println("Price cannot be negative. Item not added.");
            return;
        }

        MenuItem item = new MenuItem(nextItemId++, name, category, price);
        menu.add(item);

        System.out.println("Menu item added successfully with Item ID: " + item.id);
    }

    // ==============================================================
    // UPDATE MENU ITEM
    // ==============================================================
    static void updateMenuItem() {
        System.out.println("\n---------- UPDATE MENU ITEM ----------");
        int itemId = readInt("Enter Item ID to update: ");

        MenuItem item = findMenuItemById(itemId);
        if (item == null) {
            System.out.println("Item not found.");
            return;
        }

        System.out.println("Current Name     : " + item.name);
        System.out.println("Current Category : " + item.category);
        System.out.println("Current Price    : Rs." + formatPrice(item.price));

        String newName = readLine("Enter new Food Name (leave blank to keep current): ");
        if (!newName.trim().isEmpty()) {
            item.name = newName;
        }

        System.out.println("Update category?");
        System.out.println("1. Yes");
        System.out.println("2. No");
        int catUpdate = readInt("Enter your choice: ");
        if (catUpdate == 1) {
            String newCategory = readCategory();
            if (newCategory != null) {
                item.category = newCategory;
            } else {
                System.out.println("Invalid category. Category left unchanged.");
            }
        }

        System.out.println("Update price?");
        System.out.println("1. Yes");
        System.out.println("2. No");
        int priceUpdate = readInt("Enter your choice: ");
        if (priceUpdate == 1) {
            double newPrice = readDouble("Enter new Price: ");
            if (newPrice >= 0) {
                item.price = newPrice;
            } else {
                System.out.println("Invalid price. Price left unchanged.");
            }
        }

        System.out.println("Menu item updated successfully.");
    }

    // ==============================================================
    // DELETE MENU ITEM
    // ==============================================================
    static void deleteMenuItem() {
        System.out.println("\n---------- DELETE MENU ITEM ----------");
        int itemId = readInt("Enter Item ID to delete: ");

        MenuItem item = findMenuItemById(itemId);
        if (item == null) {
            System.out.println("Item not found.");
            return;
        }

        menu.remove(item);
        System.out.println("Menu item deleted successfully.");
    }

    // ==============================================================
    // VIEW CUSTOMER ORDERS (MANAGEMENT SIDE)
    // ==============================================================
    static void viewCustomerOrders() {
        System.out.println("\n---------- CUSTOMER ORDERS ----------");

        if (orders.isEmpty()) {
            System.out.println("No orders have been placed yet.");
            return;
        }

        for (Order order : orders) {
            System.out.println("------------------------------------");
            System.out.println("Order ID      : " + order.orderId);
            System.out.println("Customer Name : " + order.customerName);
            System.out.println("Items         :");
            for (OrderItem oi : order.items) {
                System.out.println("   - " + oi.name + " x " + oi.quantity + " = Rs." + formatPrice(oi.subTotal()));
            }
            System.out.println("Total Bill    : Rs." + formatPrice(order.totalAmount));
            System.out.println("Status        : " + order.status);
        }
        System.out.println("------------------------------------");
    }

    // ==============================================================
    // UPDATE ORDER STATUS (MANAGEMENT SIDE)
    // ==============================================================
    static void updateOrderStatus() {
        System.out.println("\n---------- UPDATE ORDER STATUS ----------");
        int orderId = readInt("Enter Order ID: ");

        Order order = findOrderById(orderId);
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }

        System.out.println("Current Status: " + order.status);
        System.out.println("Select new status:");
        System.out.println("1. Pending");
        System.out.println("2. Preparing");
        System.out.println("3. Completed");

        int choice = readInt("Enter your choice: ");
        switch (choice) {
            case 1:
                order.status = "PENDING";
                break;
            case 2:
                order.status = "PREPARING";
                break;
            case 3:
                order.status = "COMPLETED";
                break;
            default:
                System.out.println("Invalid choice. Status not updated.");
                return;
        }

        System.out.println("Order status updated to: " + order.status);
    }

    // ==============================================================
    // GENERATE BILL (MANAGEMENT SIDE)
    // ==============================================================
    static void generateBillManagement() {
        System.out.println("\n---------- GENERATE BILL ----------");
        int orderId = readInt("Enter Order ID: ");

        Order order = findOrderById(orderId);
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }

        printBill(order);
    }

    // ==============================================================
    // SALES SUMMARY
    // ==============================================================
    static void salesSummary() {
        System.out.println("\n---------- SALES SUMMARY ----------");

        if (orders.isEmpty()) {
            System.out.println("No orders have been placed yet.");
            return;
        }

        int totalOrders = orders.size();
        int completedOrders = 0;
        int pendingOrders = 0;
        int preparingOrders = 0;
        double totalRevenue = 0;

        // Track quantity sold per item name to find the most ordered item
        HashMap<String, Integer> itemCounts = new HashMap<>();

        for (Order order : orders) {
            switch (order.status) {
                case "COMPLETED":
                    completedOrders++;
                    break;
                case "PENDING":
                    pendingOrders++;
                    break;
                case "PREPARING":
                    preparingOrders++;
                    break;
                default:
                    // PLACED orders are not counted in any specific bucket
                    break;
            }

            totalRevenue += order.totalAmount;

            for (OrderItem oi : order.items) {
                int currentCount = itemCounts.getOrDefault(oi.name, 0);
                itemCounts.put(oi.name, currentCount + oi.quantity);
            }
        }

        // Determine most ordered item
        String mostOrderedItem = "N/A";
        int highestCount = 0;
        for (String itemName : itemCounts.keySet()) {
            int count = itemCounts.get(itemName);
            if (count > highestCount) {
                highestCount = count;
                mostOrderedItem = itemName;
            }
        }

        System.out.println("Total Orders       : " + totalOrders);
        System.out.println("Completed Orders   : " + completedOrders);
        System.out.println("Pending Orders     : " + pendingOrders);
        System.out.println("Preparing Orders   : " + preparingOrders);
        System.out.println("Total Revenue      : Rs." + formatPrice(totalRevenue));
        System.out.println("Most Ordered Item  : " + mostOrderedItem +
                (highestCount > 0 ? " (" + highestCount + " sold)" : ""));
    }

    // ==============================================================
    // HELPER METHODS
    // ==============================================================

    // Preloads the initial menu items as specified
    static void preloadMenu() {
        // Veg
        menu.add(new MenuItem(nextItemId++, "Idli", "Veg", 40));
        menu.add(new MenuItem(nextItemId++, "Dosa", "Veg", 60));
        menu.add(new MenuItem(nextItemId++, "Veg Fried Rice", "Veg", 120));
        menu.add(new MenuItem(nextItemId++, "Paneer Butter Masala", "Veg", 160));

        // Non-Veg
        menu.add(new MenuItem(nextItemId++, "Chicken Biryani", "Non-Veg", 220));
        menu.add(new MenuItem(nextItemId++, "Chicken Fried Rice", "Non-Veg", 180));
        menu.add(new MenuItem(nextItemId++, "Chicken 65", "Non-Veg", 190));
        menu.add(new MenuItem(nextItemId++, "Grill Chicken", "Non-Veg", 250));

        // Drinks
        menu.add(new MenuItem(nextItemId++, "Coke", "Drinks", 40));
        menu.add(new MenuItem(nextItemId++, "Pepsi", "Drinks", 40));
        menu.add(new MenuItem(nextItemId++, "Sprite", "Drinks", 40));
        menu.add(new MenuItem(nextItemId++, "Water Bottle", "Drinks", 20));

        // Desserts
        menu.add(new MenuItem(nextItemId++, "Ice Cream", "Desserts", 60));
        menu.add(new MenuItem(nextItemId++, "Brownie", "Desserts", 90));
        menu.add(new MenuItem(nextItemId++, "Gulab Jamun", "Desserts", 50));
        menu.add(new MenuItem(nextItemId++, "Chocolate Cake", "Desserts", 110));
    }

    // Finds a menu item by its ID; returns null if not found
    static MenuItem findMenuItemById(int id) {
        for (MenuItem item : menu) {
            if (item.id == id) {
                return item;
            }
        }
        return null;
    }

    // Finds an order by its ID; returns null if not found
    static Order findOrderById(int id) {
        for (Order order : orders) {
            if (order.orderId == id) {
                return order;
            }
        }
        return null;
    }

    // Prints a fully formatted bill/receipt for a given order
    static void printBill(Order order) {
        System.out.println("\n========================================");
        System.out.println("           SPICE HAVEN RESTAURANT");
        System.out.println("========================================");
        System.out.println("Order ID       : " + order.orderId);
        System.out.println("Customer Name  : " + order.customerName);
        System.out.println("----------------------------------------");
        System.out.printf("%-20s %-5s %-10s%n", "Item", "Qty", "Amount");
        for (OrderItem oi : order.items) {
            System.out.printf("%-20s %-5d Rs.%-8.2f%n", oi.name, oi.quantity, oi.subTotal());
        }
        System.out.println("----------------------------------------");
        System.out.println("Total Amount   : Rs." + formatPrice(order.totalAmount));
        System.out.println("Current Status : " + describeStatus(order.status));
        System.out.println("========================================");
        System.out.println("           THANK YOU! VISIT AGAIN");
        System.out.println("========================================");
    }

    // Prompts the user to select a category and returns its name, or null if invalid
    static String readCategory() {
        System.out.println("Select Category:");
        System.out.println("1. Veg");
        System.out.println("2. Non-Veg");
        System.out.println("3. Drinks");
        System.out.println("4. Desserts");

        int choice = readInt("Enter your choice: ");
        switch (choice) {
            case 1:
                return "Veg";
            case 2:
                return "Non-Veg";
            case 3:
                return "Drinks";
            case 4:
                return "Desserts";
            default:
                return null;
        }
    }

    // Formats a price to always show two decimal places
    static String formatPrice(double price) {
        return String.format("%.2f", price);
    }

    // Reads a line of text input from the user, with a prompt
    static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    // Reads an integer from the user, re-prompting on invalid input
    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    // Reads a double from the user, re-prompting on invalid input
    static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return Double.parseDouble(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}