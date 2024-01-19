import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Pair;

public class App extends Application {

    private boolean isLoggedIn = false;
    private String loggedInUser = "";
    private List<Product> cartItems = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    private String shippingAddress;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("E-Commerce App");
        primaryStage.getIcons().add(new Image("download.jpg"));

        // Menu Bar
        MenuBar menuBar = createMenuBar();

        // Product Grid
        ScrollPane productScrollPane = createProductGrid();

        // Shopping Cart
        ListView<String> cartListView = new ListView<>();
        Button clearCartButton = new Button("Clear Cart");
        VBox cartBox = new VBox(new Label("Shopping Cart"), cartListView, clearCartButton);
        cartBox.setPadding(new Insets(10, 10, 10, 10));
        cartBox.setSpacing(10);
        cartBox.setVisible(false); // Initially, hide the cart

        // Layout
        VBox rootLayout = new VBox(menuBar, productScrollPane);

        // Event handlers
        clearCartButton.setOnAction(event -> cartListView.getItems().clear());

        Scene scene = new Scene(rootLayout, 800, 600);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // Create main menus
        Menu menuFile = new Menu("File");
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(e -> Platform.exit());
        menuFile.getItems().add(exitMenuItem);

        Menu menuShop = new Menu("Shop");
        MenuItem shopAllProductsMenuItem = new MenuItem("All Products");
        MenuItem shopHandbagsMenuItem = new MenuItem("HANDBAGS");
        MenuItem shopFootwearMenuItem = new MenuItem("FOOTWARE");
        MenuItem shopMobilephonesMenuItem = new MenuItem("MOBILE PHONES");
        MenuItem shopLeatherwearMenuItem = new MenuItem("LEATHER WARE");
        MenuItem shopElectricalitemsMenuItem = new MenuItem("ELECTRICAL ITEMS");
        

        menuShop.getItems().addAll(shopAllProductsMenuItem, shopHandbagsMenuItem, shopFootwearMenuItem,
                shopMobilephonesMenuItem, shopLeatherwearMenuItem, shopElectricalitemsMenuItem);

        Menu menuAccount = new Menu("Account");
        MenuItem loginMenuItem = new MenuItem("Login");
        MenuItem signupMenuItem = new MenuItem("Sign Up");
        MenuItem profileMenuItem = new MenuItem("My Profile");
        menuAccount.getItems().addAll(loginMenuItem, signupMenuItem, profileMenuItem);

        Menu menuCart = new Menu("Cart");
        MenuItem viewCartMenuItem = new MenuItem("View Cart");
        MenuItem checkoutMenuItem = new MenuItem("Checkout");
        menuCart.getItems().addAll(viewCartMenuItem, checkoutMenuItem);
         // Create Orders menu
    Menu ordersMenu = new Menu("Orders");
    MenuItem userOrdersMenuItem = new MenuItem("User Orders");
    userOrdersMenuItem.setOnAction(event -> showUserOrders());
    ordersMenu.getItems().add(userOrdersMenuItem);


        // Add menus to the menu bar
        menuBar.getMenus().addAll(menuFile, menuShop, menuAccount, menuCart,ordersMenu);

        // Event handlers
        loginMenuItem.setOnAction(event -> showLoginDialog());
        signupMenuItem.setOnAction(event -> showSignupDialog());
        profileMenuItem.setOnAction(event -> showProfileDialog());
        viewCartMenuItem.setOnAction(event -> showCartDialog(cartItems));
        checkoutMenuItem.setOnAction(event -> showPaymentDialog());

        return menuBar;
    }

    private ScrollPane createProductGrid() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        // Sample products (replace with your actual product data)
        retrieveProductsFromDatabase(); // Uncomment this line to retrieve products from the database

        // Add products to the grid
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            String imgpath = "download" + (i + 1) + ".jpg";
            gridPane.add(createProductBox(product, imgpath), i % 2, i / 2);
        }

        // Create three VBox elements for details lists
        VBox detailsBox1 = createVerticalBox(
                "Get to Know Us\nCareers\nBlog\nAbout Amazon\nInvestor Relations\nAmazon Devices\nAmazon Science");
        VBox detailsBox2 = createVerticalBox(
                "Get to Know Us\nCareers\nBlog\nAbout Amazon\nInvestor Relations\nAmazon Devices\nAmazon Science");
        VBox detailsBox3 = createVerticalBox(
                "Get to Know Us\nCareers\nBlog\nAbout Amazon\nInvestor Relations\nAmazon Devices\nAmazon Science");

        // Set the width of each details box as 1/3rd of the whole screen
        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        detailsBox1.setPrefWidth(screenWidth / 3);
        detailsBox2.setPrefWidth(screenWidth / 3);
        detailsBox3.setPrefWidth(screenWidth / 3);

        // Combine the details boxes horizontally
        HBox detailsHBox = new HBox(detailsBox1, detailsBox2, detailsBox3);
        detailsHBox.setStyle("-fx-background-color: #D3D3D3;"); // Set gray background

        // Combine the product grid and details boxes
        VBox combinedBox = new VBox(gridPane, detailsHBox);

        ScrollPane productScrollPane = new ScrollPane(combinedBox);
        productScrollPane.setFitToWidth(true);
        productScrollPane.setFitToHeight(true);

        return productScrollPane;
    }

    private VBox createVerticalBox(String details) {
        VBox verticalBox = new VBox();

        // Split details by newline and create a label for each item
        Arrays.stream(details.split("\n"))
                .map(Label::new)
                .forEach(label -> {
                    verticalBox.getChildren().add(label);
                });

        verticalBox.setPadding(new Insets(10));

        return verticalBox;
    }
    private void showUserOrders() {
        if (isLoggedIn) {
            // Retrieve orders for the current user from the database
            List<Order> userOrders = retrieveUserOrders(loggedInUser);
    
            if (!userOrders.isEmpty()) {
                // Display the orders
                StringBuilder ordersInfo = new StringBuilder("Your Orders:\n");
                for (Order order : userOrders) {
                    ordersInfo.append(String.format("Order ID: %s, Date: %s, Total Amount: $%.2f\n",
                            order.getOrderID(), order.getOrderDate(), order.getTotalAmount()));
                }
                showAlert("User Orders", ordersInfo.toString());
            } else {
                showAlert("User Orders", "No orders found for the current user.");
            }
        } else {
            showAlert("User Orders", "Not logged in. Please log in first.");
        }
    }
    private List<Order> retrieveUserOrders(String loggedInUser) {
        List<Order> userOrders = new ArrayList<>();
    
        // Replace with your actual database connection details
        String url = "jdbc:mysql://localhost:3306/final";
        String user = "root";
        String password = "root";
    
        // SQL query to retrieve user orders
        String query = "SELECT * FROM Orders WHERE UserID = ?";
    
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    
            int userId = getUserId(loggedInUser);
            preparedStatement.setInt(1, userId);
    
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    // Retrieve order details from the result set
                    String orderID = resultSet.getString("OrderID");
                    LocalDateTime orderDate = resultSet.getTimestamp("OrderDate").toLocalDateTime();
                    double totalAmount = resultSet.getDouble("TotalAmount");
                    String shippingAddress = resultSet.getString("ShippingAddress");
                    String paymentMethodID = resultSet.getString("PaymentMethodID");
                    String paymentStatus = resultSet.getString("PaymentStatus");
                    int isShipped = resultSet.getInt("IsShipped");
                    String regionID = resultSet.getString("RegionID");
    
                    // Create an Order object and add it to the list
                    Order order = new Order(orderID, orderDate, totalAmount, shippingAddress,
                            paymentMethodID, paymentStatus, isShipped, regionID);
                    userOrders.add(order);
                }
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return userOrders;
    }
    
    
    private void showPaymentDialog() {
        if (isLoggedIn && !cartItems.isEmpty()) {
            // Display the bill
            double totalAmount = cartItems.stream().mapToDouble(Product::getPrice).sum();
            String billMessage = String.format("Total Amount: $%.2f\n\n", totalAmount);
    
            TextInputDialog addressDialog = new TextInputDialog();
            addressDialog.setTitle("Shipping Address");
            addressDialog.setHeaderText("Enter your shipping address:");
            addressDialog.setContentText("Address:");
    
            Optional<String> addressResult = addressDialog.showAndWait();
    
            addressResult.ifPresent(address -> {
                String userAddress = address;
    
                // Select payment method
                ChoiceDialog<String> paymentMethodDialog = new ChoiceDialog<>("Credit Card", "Credit Card", "PayPal", "Other");
                paymentMethodDialog.setTitle("Payment Method");
                paymentMethodDialog.setHeaderText("Select a payment method:");
                paymentMethodDialog.setContentText("Payment Method:");
    
                Optional<String> paymentMethodResult = paymentMethodDialog.showAndWait();
    
                paymentMethodResult.ifPresent(paymentMethod -> {
                    // Get payment credentials
                    Dialog<Pair<String, String>> credentialsDialog = createCredentialsDialog();
                    Optional<Pair<String, String>> credentialsResult = credentialsDialog.showAndWait();
                    String paymentMethodid = "PM5";
                    credentialsResult.ifPresent(credentials -> {
                        // Validate inputs
                        if (isValidCreditCard(credentials.getKey()) && isValidExpirationDate(credentials.getValue())) {
                            // Confirm payment and generate transaction ID
                            List<String> productIds = getProductIdsFromCart(cartItems);
                            String OrderID = generateOrderID();
                            double TotalAmount = totalAmount;
    
                            storeOrderDetails(OrderID, TotalAmount, productIds, userAddress, paymentMethodid);
    
                            // Display success message
                            String paymentSuccessMessage = String.format("Thank you for your purchase! Your transaction ID is: %s", OrderID);
                            showAlert("Payment Success", paymentSuccessMessage);
    
                            // Clear the cart after successful payment
                            cartItems.clear();
                        } else {
                            showAlert("Payment Error", "Invalid credit card information. Please check your inputs.");
                        }
                    });
                });
            });
        } else {
            showAlert("Checkout and Payment", "Please log in and add items to your cart before checking out.");
        }
    }
    
    
    
    private List<String> getProductIdsFromCart(List<Product> cartItems) {
        List<String> productIds = new ArrayList<>();
    
        for (Product item : cartItems) {
            // Assuming there's a method in your Product class to get the product ID
            String productId = item.getId();
    
            // Add the productId to the list
            productIds.add(productId);
        }
    
        return productIds;
    }
    // Additional methods for credit card validation
    private boolean isValidCreditCard(String cardNumber) {
        // Implement your credit card validation logic here
        // For simplicity, let's assume that a valid card number has 16 digits
        return cardNumber.matches("\\d{16}");
    }

    private boolean isValidExpirationDate(String expirationDate) {
        // Implement your expiration date validation logic here
        // For simplicity, let's assume that a valid expiration date has the format
        // MM/YY
        return expirationDate.matches("(0[1-9]|1[0-2])/(\\d{2})");
    }

    private void storeOrderDetails(String OrderID, double TotalAmount, String productId, String paymentMethod) {
        String query = "INSERT INTO Orders (UserID, OrderID, OrderDate, TotalAmount, ShippingAddress, PaymentMethodID,PaymentStatus,IsShipped,RegionID) VALUES (?, ?, ?, ?, ?, ?,'PAID',0,'RE5' )";
    
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/final", "root", "root");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    
            int userId = getUserId(loggedInUser);
            String address = getUserAddress(userId); // Fetch user address
    
            preparedStatement.setInt(1, userId);
            
            preparedStatement.setString(2, OrderID);
            preparedStatement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setDouble(4, TotalAmount);
            preparedStatement.setString(5, address);
            preparedStatement.setString(6, paymentMethod);
    
            preparedStatement.executeUpdate();
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private String getUserAddress(int userId) {
        String query = "SELECT Address FROM User WHERE UserID = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/final", "root", "root");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    
            preparedStatement.setInt(1, userId);
    
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("Address");
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return "Address not found"; // You can handle this case appropriately in your application
    }
    

  
    private String getPaymentMethodIdByName(String paymentMethodName) {
        // Replace this method with the actual logic to get the payment method ID based
        // on the name
        // You may need a database query to fetch the payment method ID.
        return "PM2"; // Dummy value, replace with actual logic
    }

    private String generateOrderID() {
        // Implement a logic to generate a unique transaction ID
        return "TXN" + System.currentTimeMillis();
    }

    private void retrieveProductsFromDatabase() {
        // JDBC URL, username, and password of MySQL server
        String url = "jdbc:mysql://localhost:3306/final";
        String user = "root";
        String password = "root";

        // SQL query to retrieve products
        String query = "SELECT * FROM product";

        try (Connection connection = DriverManager.getConnection(url, user, password);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                // Retrieve product details from the result set
                String productID = resultSet.getString("ProductID");
                String name = resultSet.getString("Name");
                String description = resultSet.getString("Description");
                double price = resultSet.getDouble("Price");
                int stockQuantity = resultSet.getInt("StockQuantity");
                LocalDateTime createdDate = resultSet.getTimestamp("CreatedDate").toLocalDateTime();
                String manufacturer = resultSet.getString("Manufacturer");
                double weight = resultSet.getDouble("Weight");
                String sku = resultSet.getString("SKU");
                double taxRate = resultSet.getDouble("TaxRate");
                double discountPercentage = resultSet.getDouble("DiscountPercentage");
                boolean isActive = resultSet.getBoolean("IsActive");
                String adminID = resultSet.getString("AdminID");

                // Create a Product object and add it to the list
                Product product = new Product(productID, name, description, price, stockQuantity, createdDate,
                        manufacturer, weight, sku, taxRate, discountPercentage, isActive, adminID);
                products.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createProductBox(Product product, String imgPath) {
        ImageView productImage = new ImageView(new Image(imgPath));
        productImage.setFitHeight(150);
        productImage.setFitWidth(150);

        Label nameLabel = new Label("Name: " + product.getName());
        Label descriptionLabel = new Label("Description: " + product.getDescription());
        Label priceLabel = new Label("Price: $" + product.getPrice());

        // Add to Cart Button
        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setStyle("-fx-background-color: #0073e6; -fx-text-fill: white;");
        addToCartButton.setOnAction(event -> addToCartButtonHandler(product, null));

        // Buy Now Button
        Button buyNowButton = new Button("Buy Now");
        buyNowButton.setStyle("-fx-background-color: #0073e6; -fx-text-fill: white;");
        buyNowButton.setOnAction(event -> buyNow(product));


        VBox productBox = new VBox(productImage, nameLabel, descriptionLabel, priceLabel, addToCartButton,
                buyNowButton);
        productBox.setSpacing(10);
        productBox.setPadding(new Insets(10));

        // Set background color to lightest gray possible
        BackgroundFill backgroundFill = new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(backgroundFill);
        productBox.setBackground(background);

        return productBox;
    }

    private void addToCartButtonHandler(Product product, ListView<String> cartListView) {
        if (cartListView != null) {
            Platform.runLater(() -> {
                cartListView.getItems().add(product.getName() + " - $" + product.getPrice());
            });
        } else {
            System.out.println("Product added to cart: " + product.getName());
        }
        cartItems.add(product);
    }


    private void buyNow(Product product) {
        if (isLoggedIn) {
            // Display the bill before payment
        String billContent = String.format("Product: %s\nPrice: $%.2f\n\n", product.getName(), product.getPrice());

        Alert billAlert = new Alert(Alert.AlertType.CONFIRMATION);
        billAlert.setTitle("Order Summary");
        billAlert.setHeaderText("Please review your order:");
        billAlert.setContentText(billContent);

        ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        billAlert.getButtonTypes().setAll(confirmButtonType, ButtonType.CANCEL);

        Optional<ButtonType> result = billAlert.showAndWait();

        result.ifPresent(buttonType -> {
            if (buttonType == confirmButtonType) {
            // Select payment method
            ChoiceDialog<String> paymentMethodDialog = new ChoiceDialog<>("Credit Card", "Credit Card", "PayPal", "Other");
            paymentMethodDialog.setTitle("Payment Method");
            paymentMethodDialog.setHeaderText("Select a payment method:");
            paymentMethodDialog.setContentText("Payment Method:");
    
            Optional<String> paymentMethodResult = paymentMethodDialog.showAndWait();
    
            paymentMethodResult.ifPresent(paymentMethod -> {
                // Get payment credentials
                Dialog<Pair<String, String>> credentialsDialog = createCredentialsDialog();
                Optional<Pair<String, String>> credentialsResult = credentialsDialog.showAndWait();
    
                credentialsResult.ifPresent(credentials -> {
                    // Get the user's address
                    String userAddress = getUserAddress(loggedInUser);
    
                    // Confirm payment and generate transaction ID
                    String orderID = generateOrderID();
    
                    // Display success message
                    String paymentSuccessMessage = String.format("Thank you for your purchase! Your transaction ID is: %s", orderID);
                    showAlert("Payment Success", paymentSuccessMessage);
    
                   // Store order details with user address
                   storeOrderDetails(orderID, product.getPrice(), userAddress,"PM5");
                });
            });
        }
    });
} else {
    showAlert("User Profile", "Not logged in. Please log in first.");
}
}
    private void checkout() {
        if (isLoggedIn && !cartItems.isEmpty()) {
            // Display the bill
            displayBillForCart(cartItems);

            // Prompt for confirmation
            if (confirmAction("Do you want to proceed with the purchase?")) {
                // Proceed with entering shipping address and payment
                TextInputDialog addressDialog = new TextInputDialog();
                addressDialog.setTitle("Shipping Address");
                addressDialog.setHeaderText("Enter your shipping address:");
                addressDialog.setContentText("Address:");

                Optional<String> addressResult = addressDialog.showAndWait();

                addressResult.ifPresent(address -> {
                    shippingAddress = address;
                    showPaymentDialogForCart(cartItems);
                    cartItems.clear(); // Clear the cart after successful payment
                });
            }
        } else {
            showAlert("Checkout and Payment", "Please log in and add items to your cart before checking out.");
        }
    }

    private void displayBillForSingleItem(Product product) {
        double totalAmount = product.getPrice();
        String billMessage = String.format("Bill Summary:\n\n%s - $%.2f\n\nTotal Amount: $%.2f", product.getName(),
                product.getPrice(), totalAmount);
        showAlertWithConfirm("Bill Summary", billMessage);
    }

    private void displayBillForCart(List<Product> cartItems) {
        double totalAmount = cartItems.stream().mapToDouble(Product::getPrice).sum();
        StringBuilder billMessage = new StringBuilder("Bill Summary:\n\n");
        for (Product item : cartItems) {
            billMessage.append(String.format("%s - $%.2f\n", item.getName(), item.getPrice()));
        }
        billMessage.append(String.format("\nTotal Amount: $%.2f", totalAmount));
        showAlertWithConfirm("Bill Summary", billMessage.toString());
    }

    private boolean confirmAction(String content) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText(content);

        Optional<ButtonType> result = confirmAlert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void showAlertWithConfirm(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        // Add a Confirm button
        ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == confirmButtonType) {
            // User clicked Confirm
            // You can handle additional actions if needed
        }
    }

    private void showCartDialog(List<Product> cartItems) {
        if (!cartItems.isEmpty()) {
            Alert cartAlert = new Alert(Alert.AlertType.INFORMATION);
            cartAlert.setTitle("Shopping Cart");
            cartAlert.setHeaderText("Items in your cart:");

            // Create a new ListView for cart items
            ListView<String> cartListView = new ListView<>();
            for (Product item : cartItems) {
                cartListView.getItems().add(item.getName() + " - $" + item.getPrice());
            }

            cartAlert.getDialogPane().setContent(cartListView);
            cartAlert.showAndWait();
        } else {
            showAlert("Shopping Cart", "Your cart is empty.");
        }
    }

    private void showLoginDialog() {
        Dialog<Pair<String, String>> loginDialog = new Dialog<>();
        loginDialog.setTitle("Login");
        loginDialog.setHeaderText("Enter your credentials:");

        // Set the button types
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        loginDialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = loginDialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        loginDialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(username::requestFocus);

        // Convert the result to a username-password-pair when the login button is
        // clicked.
        loginDialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = loginDialog.showAndWait();

        result.ifPresent(credentials -> {
            if (validateLogin(credentials.getKey(), credentials.getValue())) {
                isLoggedIn = true;
                loggedInUser = credentials.getKey();
                showAlert("Login Successful", "Welcome, " + loggedInUser + "!");
            } else {
                showAlert("Login Failed", "Invalid username or password. Please try again.");
            }
        });
    }

    private boolean validateLogin(String enteredUsername, String enteredPassword) {
        String query = "SELECT * FROM user WHERE username = ? AND password = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/final", "root", "root");
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, enteredUsername);
            preparedStatement.setString(2, enteredPassword);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // If there is a result, the login is successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showSignupDialog() {
        Dialog<Pair<String, String>> signupDialog = new Dialog<>();
        signupDialog.setTitle("Sign Up");
        signupDialog.setHeaderText("Enter your information:");
    
        // Set the icon (if needed)
        signupDialog.setGraphic(new ImageView(new Image("download.jpg")));
    
        // Set the button types
        ButtonType signupButtonType = new ButtonType("Sign Up", ButtonBar.ButtonData.OK_DONE);
        signupDialog.getDialogPane().getButtonTypes().addAll(signupButtonType, ButtonType.CANCEL);
    
        // Create the username, password, email, and additional fields labels and fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
    
        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        TextField email = new TextField();
        email.setPromptText("Email");
        TextField firstName = new TextField();
        firstName.setPromptText("First Name");
        TextField lastName = new TextField();
        lastName.setPromptText("Last Name");
        TextField address = new TextField();
        address.setPromptText("Address");
        TextField phoneNumber = new TextField();
        phoneNumber.setPromptText("Phone Number");
    
        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(email, 1, 2);
        grid.add(new Label("First Name:"), 0, 3);
        grid.add(firstName, 1, 3);
        grid.add(new Label("Last Name:"), 0, 4);
        grid.add(lastName, 1, 4);
        grid.add(new Label("Address:"), 0, 5);
        grid.add(address, 1, 5);
        grid.add(new Label("Phone Number:"), 0, 6);
        grid.add(phoneNumber, 1, 6);
    
        // Enable/Disable signup button depending on whether all fields are entered.
        Node signupButton = signupDialog.getDialogPane().lookupButton(signupButtonType);
        signupButton.setDisable(true);
    
        // Do some validation (using the Java 8 lambda syntax).
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            signupButton.setDisable(newValue.trim().isEmpty() || password.getText().trim().isEmpty() || email.getText().trim().isEmpty());
        });
    
        password.textProperty().addListener((observable, oldValue, newValue) -> {
            signupButton.setDisable(newValue.trim().isEmpty() || username.getText().trim().isEmpty() || email.getText().trim().isEmpty());
        });
    
        email.textProperty().addListener((observable, oldValue, newValue) -> {
            signupButton.setDisable(newValue.trim().isEmpty() || username.getText().trim().isEmpty() || password.getText().trim().isEmpty());
        });
    
        signupDialog.getDialogPane().setContent(grid);
    
        // Request focus on the username field by default.
        Platform.runLater(username::requestFocus);
    
        // Convert the result to a username-password-pair when the signup button is clicked.
        signupDialog.setResultConverter(dialogButton -> {
            if (dialogButton == signupButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });
    
        Optional<Pair<String, String>> result = signupDialog.showAndWait();
    
        result.ifPresent(signupResult -> {
            // Additional code to handle the sign-up result
            handleSignUpResult(
                    signupResult.getKey(),
                    signupResult.getValue(),
                    email.getText(),
                    firstName.getText(),
                    lastName.getText(),
                    address.getText(),
                    phoneNumber.getText()
            );
        });
    }
    
    private void handleSignUpResult(String username, String password, String email,
    String firstName, String lastName,
    String address, String phoneNumber) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/final", "root", "root");
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO user (UserID,Username, Password, Email, FirstName, LastName, Address, PhoneNumber, RegistrationDate, LastLoginDate, IsAdmin) VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            long UserID=107;
            statement.setLong(1,UserID);
            UserID+=1;
            statement.setString(2, username);
            statement.setString(3, password);
            statement.setString(4, email);
           
            // PhoneNumber
            statement.setString(5, firstName);
            statement.setString(6, lastName);
            statement.setString(7, address);
            statement.setString(8, phoneNumber);
            statement.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            statement.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            statement.setInt(11, 0); // Set IsAdmin to 0 (false)

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                showAlert("Sign Up Successful", "Welcome, " + username + "!");
            } else {
                showAlert("Sign Up Failed", "Failed to sign up. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showProfileDialog() {
        if (isLoggedIn) {
            showAlert("User Profile", "Logged in as: " + loggedInUser);
        } else {
            showAlert("User Profile", "Not logged in. Please log in first.");
        }
    }

    private void showPaymentDialogForSingleItem(Product product) {
        // Select payment method
        ChoiceDialog<String> paymentMethodDialog = new ChoiceDialog<>("Credit Card", "Credit Card", "PayPal", "Other");
        paymentMethodDialog.setTitle("Payment Method");
        paymentMethodDialog.setHeaderText("Select a payment method:");
        paymentMethodDialog.setContentText("Payment Method:");
    
        Optional<String> paymentMethodResult = paymentMethodDialog.showAndWait();
    
        paymentMethodResult.ifPresent(paymentMethod -> {
            // Get payment credentials
            Dialog<Pair<String, String>> credentialsDialog = createCredentialsDialog();
            Optional<Pair<String, String>> credentialsResult = credentialsDialog.showAndWait();
    
            credentialsResult.ifPresent(credentials -> {
                // Get the user's address
                String userAddress = getUserAddress(loggedInUser);
    
                // Confirm payment and generate transaction ID
                String OrderID = generateOrderID();
    
                // Display success message
                String paymentSuccessMessage = String.format("Thank you for your purchase! Your transaction ID is: %s", OrderID);
                showAlert("Payment Success", paymentSuccessMessage);
    
                // Store order details with user address
                storeOrderDetails(OrderID, product.getPrice(), userAddress, paymentMethod);
            });
        });
    }
    
    private void showPaymentDialogForCart(List<Product> cartItems) {
        // Select payment method
        ChoiceDialog<String> paymentMethodDialog = new ChoiceDialog<>("Credit Card", "Credit Card", "PayPal", "Other");
        paymentMethodDialog.setTitle("Payment Method");
        paymentMethodDialog.setHeaderText("Select a payment method:");
        paymentMethodDialog.setContentText("Payment Method:");
    
        Optional<String> paymentMethodResult = paymentMethodDialog.showAndWait();
    
        paymentMethodResult.ifPresent(paymentMethod -> {
            // Get payment credentials
            Dialog<Pair<String, String>> credentialsDialog = createCredentialsDialog();
            Optional<Pair<String, String>> credentialsResult = credentialsDialog.showAndWait();
    
            credentialsResult.ifPresent(credentials -> {
                // Get the user's address
                String userAddress = getUserAddress(loggedInUser);
    
                // Confirm payment and generate transaction ID
                String OrderID = generateOrderID();
                double TotalAmount = cartItems.stream().mapToDouble(Product::getPrice).sum();
    
                // Display success message
                String paymentSuccessMessage = String.format("Thank you for your purchase! Your transaction ID is: %s", OrderID);
                showAlert("Payment Success", paymentSuccessMessage);
    
                // Clear the cart after successful payment
                cartItems.clear();
    
                // Store order details with user address
                storeOrderDetails(OrderID, TotalAmount, userAddress, paymentMethod);
            });
        });
    }
    
    // Method to get the user's address from the database
    private String getUserAddress(String username) {
        String query = "SELECT Address FROM user WHERE Username = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/final", "root", "root");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
    
            ResultSet resultSet = preparedStatement.executeQuery();
    
            if (resultSet.next()) {
                return resultSet.getString("Address");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return null;
    }
    
        
    private void storeOrderDetails(String OrderID, double TotalAmount, List<String> productIds, String userAddress, String paymentMethod) {
        String orderItemQuery = "INSERT INTO OrderItem (OrderItemID, OrderID, ProductID, Quantity, Subtotal) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/final", "root", "root");
             PreparedStatement  preparedStatement = connection.prepareStatement(
            "INSERT INTO Orders (UserID, OrderID, OrderDate, TotalAmount, ShippingAddress, PaymentMethodID,PaymentStatus,IsShipped,RegionID) VALUES (?, ?, ?, ?, ?, ?,'PAID',0,'RE5' )")) {

            String productIdsString = String.join(",", productIds);
                int userId=getUserId(loggedInUser);
            preparedStatement.setInt(1, userId);
            
            preparedStatement.setString(2, OrderID);
            preparedStatement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setDouble(4, TotalAmount);
            preparedStatement.setString(5, userAddress);
            preparedStatement.setString(6, paymentMethod);
    
            int affectedRows = preparedStatement.executeUpdate();
    
            if (affectedRows > 0) {
                showAlert("Order Placed", "Your order has been placed successfully!");
            } else {
                showAlert("Order Failed", "Failed to place the order. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
        
        private int getUserId(String username) {
            String query = "SELECT UserID FROM user WHERE username = ?";
            
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/final", "root", "root");
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
        
                if (resultSet.next()) {
                    return resultSet.getInt("UserID");
                }
        
            } catch (SQLException e) {
                e.printStackTrace();
            }
        
            return -1; // Return a default value or handle this case appropriately
        }

    private Dialog<Pair<String, String>> createCredentialsDialog() {
        Dialog<Pair<String, String>> credentialsDialog = new Dialog<>();
        credentialsDialog.setTitle("Payment Credentials");
        credentialsDialog.setHeaderText("Enter your payment credentials:");

        // Set the button types
        ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        credentialsDialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        // Create the card number and expiration date labels and fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField cardNumber = new TextField();
        cardNumber.setPromptText("Card Number");
        TextField expirationDate = new TextField();
        expirationDate.setPromptText("Expiration Date (MM/YY)");

        grid.add(new Label("Card Number:"), 0, 0);
        grid.add(cardNumber, 1, 0);
        grid.add(new Label("Expiration Date:"), 0, 1);
        grid.add(expirationDate, 1, 1);

        credentialsDialog.getDialogPane().setContent(grid);

        // Request focus on the card number field by default.
        Platform.runLater(cardNumber::requestFocus);

        // Convert the result to a card number-expiration date-pair when the confirm
        // button is clicked.
        credentialsDialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return new Pair<>(cardNumber.getText(), expirationDate.getText());
            }
            return null;
        });

        return credentialsDialog;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private Node createMainLayout() {
        BorderPane mainLayout = new BorderPane();

        // Center
        mainLayout.setCenter(createGridPane());

        // You can add other components to top, bottom, left, or right if needed

        return new ScrollPane(mainLayout);
    }

    private GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Your existing code for creating the GridPane...

        return grid;
    }

    private static class Product {
        private final String name;
        private final String description;
        private final double price;
        private final int stockQuantity;
        private final LocalDateTime createdDate;
        private final String manufacturer;
        private final double weight;
        private final String sku;
        private final double taxRate;
        private final double discountPercentage;
        private final boolean isActive;
        private final String adminID;
        private String Id;

        public Product(String Id, String name, String description, double price, int stockQuantity,
                LocalDateTime createdDate,
                String manufacturer, double weight, String sku, double taxRate, double discountPercentage,
                boolean isActive, String adminID) {
            this.Id = Id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.stockQuantity = stockQuantity;
            this.createdDate = createdDate;
            this.manufacturer = manufacturer;
            this.weight = weight;
            this.sku = sku;
            this.taxRate = taxRate;
            this.discountPercentage = discountPercentage;
            this.isActive = isActive;
            this.adminID = adminID;
        }
        public String getId() {
            return Id;
        }
        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public double getPrice() {
            return price;
        }

        // Other getters for remaining fields

        public int getStockQuantity() {
            return stockQuantity;
        }

        public LocalDateTime getCreatedDate() {
            return createdDate;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public double getWeight() {
            return weight;
        }

        public String getSku() {
            return sku;
        }

        public double getTaxRate() {
            return taxRate;
        }

        public double getDiscountPercentage() {
            return discountPercentage;
        }

        public boolean isActive() {
            return isActive;
        }

        public String getAdminID() {
            return adminID;
        }
    }
    public class Order {
        private final String orderID;
        private final LocalDateTime orderDate;
        private final double totalAmount;
        private String shippingAddress;
        private String paymentMethodID;
        private String paymentStatus;
        private int isShipped;
        private String regionID;
    
        public Order(String orderID, LocalDateTime orderDate, double totalAmount) {
            this.orderID = orderID;
            this.orderDate = orderDate;
            this.totalAmount = totalAmount;
        }
        public Order(String orderID, LocalDateTime orderDate, double totalAmount,
                 String shippingAddress, String paymentMethodID, String paymentStatus,
                 int isShipped, String regionID) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.paymentMethodID = paymentMethodID;
        this.paymentStatus = paymentStatus;
        this.isShipped = isShipped;
        this.regionID = regionID;
    }
    
        public String getOrderID() {
            return orderID;
        }
    
        public LocalDateTime getOrderDate() {
            return orderDate;
        }
    
        public double getTotalAmount() {
            return totalAmount;
        }
    }

}
