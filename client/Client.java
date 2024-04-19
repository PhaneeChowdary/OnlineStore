package client;

import model.Product;
import rmi.FrontController;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            String host = "in-csci-rrpc01.cs.iupui.edu";
            int port = 2025;

            Registry registry = LocateRegistry.getRegistry(host, port);
            FrontController frontController = (FrontController) registry.lookup("rmi.FrontController");


            // Prompt for login or signup
            String role = "";
            while (!role.equals("user") && !role.equals("admin")) {
                System.out.print("\nAre you a User or an Admin (type `exit` to cancel)?: ");
                role = scanner.nextLine().trim().toLowerCase();

                if ("exit".equalsIgnoreCase(role)) {
                    System.out.println("\n-----------------------------------------");
                    System.out.println("Exiting the application.");
                    System.out.println("-----------------------------------------");
                    return;
                }

                if (!role.equals("user") && !role.equals("admin")) {
                    System.out.println("\nInvalid input. Please type 'user' or 'admin'.");
                }
            }

            String action = "";
            if (role.equals("user")) {
                while (!action.equals("login") && !action.equals("signup")) {
                    System.out.print("Do you want to Login or Signup? (login/signup): ");
                    action = scanner.nextLine().trim().toLowerCase();
                    if (!action.equals("login") && !action.equals("signup")) {
                        System.out.println("\nInvalid input. Please type 'login' or 'signup'.");
                    }
                }
            } else action = "login";
            String username, password;
            Object feedback, id = null;


            // Signup
            if (action.equals("signup")) {
                System.out.println("\nLet's setup your account");
                System.out.print("Enter username: ");
                username = scanner.nextLine();
                System.out.print("Enter password: ");
                password = scanner.nextLine();
                feedback = frontController.processRequest("registerUser", username, password);
                id = frontController.processRequest("loginUser", username, password);
                System.out.println(feedback);

                System.out.println("\n-----------------------------------------");
                System.out.println("Logged in as `" + username + "`");
                System.out.println("-----------------------------------------");
            }


            //Login
            if (action.equals("login")) {
                while (true) {
                    System.out.print("\nEnter username (or type `exit` to cancel): ");
                    username = scanner.nextLine().trim();

                    //Option to exit
                    if ("exit".equalsIgnoreCase(username)) {
                        System.out.println("\n-----------------------------------------");
                        System.out.println("Login action cancelled.");
                        System.out.println("-----------------------------------------");
                        break;
                    }

                    System.out.print("Enter password: ");
                    password = scanner.nextLine().trim();
                    if (role.equals("user")) {  // User login
                        id = frontController.processRequest("loginUser", username, password);
                    } else {    //Admin login
                        id = frontController.processRequest("loginAdmin", username, password);
                    }
                    if (id != null) {
                        System.out.println("\n-----------------------------------------");
                        System.out.println("Logged in as `" + username + "`");
                        System.out.println("-----------------------------------------");
                        break;
                    } else {
                        System.out.println("\nUnauthorized: Invalid Credentials.");
                    }
                }
            }


        if ("user".equals(role) && id != null) { // User is logged in
            boolean exit = false;
            while (!exit) {
                System.out.print("\nChoose an action...\n(1) Browse Products (2) Purchase Products (3) Cart (4) Exit: ");
                String choice = scanner.nextLine().trim();
                Object productsResult = frontController.processRequest("browseProducts");

                        switch (choice) {
                    case "1": // Browse Products
                        System.out.println("\n-----------------------------------------");
                        if (productsResult instanceof String) {
                            System.out.println(productsResult);
                        } else if (productsResult instanceof List){
                            System.out.println("Products:");
                            List<Product> products = (List<Product>) productsResult;
                            for (Product product : products) {
                                System.out.println(product.getName() + " - $" + product.getPrice() + ", quantity - " + product.getQuantityAvailable() + ", description - " + product.getDescription() + ", ID: " + product.getProductId());
                            }
                        }
                        System.out.println("-----------------------------------------");
                        break;

                    case "2": // Purchase Products
                        productsResult = frontController.processRequest("browseProducts");

                        if(productsResult instanceof List){
                            System.out.print("\nEnter the model.Product ID you wish to add to cart (or type 'quit' to cancel): ");
                            String productId = scanner.nextLine().trim();

                            // Providing an option to exit the loop
                            if ("exit".equalsIgnoreCase(productId)) {
                                System.out.println("Purchase action cancelled.");
                                break;
                            }

                            // Check product availability
                            Object productResponse = frontController.processRequest("getProductDetails", productId);
                            if (productResponse instanceof String) {
                                System.out.println(productResponse);
                                break;
                            }

                            Product product = (Product) productResponse;
                            System.out.println("\n`" + product.getName() + "` stock available: " + product.getQuantityAvailable());
                            int quantity = -1;
                            while(quantity > product.getQuantityAvailable() || quantity < 1){
                                try {
                                    System.out.print("\nEnter the quantity you wish to add: ");
                                    quantity = Integer.parseInt(scanner.nextLine().trim());
                                    if (quantity < 1) {
                                        System.out.println("\nQuantity must be at least 1.");
                                    }
                                    if (quantity > product.getQuantityAvailable()) {
                                        System.out.println("\nOnly " + product.getQuantityAvailable() + " units available. Please enter a lower quantity.");
                                    }
                                }
                                catch (NumberFormatException e) {
                                    System.out.println("\nInvalid quantity. Please enter a valid number.");
                                }
                            }

                            // Add to cart
                            Object addToCartFeedback = frontController.processRequest("addToCart", id, productId, quantity);
                            System.out.println(addToCartFeedback);

                            // Step 2: Ask if the user wants to check out
                            System.out.print("\nDo you want to proceed to checkout? (yes/no) ");
                            String proceedToCheckout = scanner.nextLine().trim().toLowerCase();
                            if ("yes".equals(proceedToCheckout)) { // Step 3: Checkout
                                Object checkoutFeedback = frontController.processRequest("checkout", id);
                                System.out.println(checkoutFeedback);
                                break;
                            } else {
                                System.out.println("\nYou can continue shopping or choose another action.");
                            }
                        } else {
                            System.out.println("\n-----------------------------------------");
                            System.out.println("Store is empty.");
                            System.out.println("-----------------------------------------");
                        }
                        break;

                    case "3": // View cart items
                        Object cartResponse = frontController.processRequest("viewCart", id);
                        if (cartResponse instanceof String) {
                            String cartContents = (String) cartResponse;
                            System.out.println(cartContents);
                            if (!cartContents.contains("empty")) {
                                System.out.print("\nDo you want to (1) Remove item(s), (2) Update item(s), (3)Checkout, (4) Go Back ? ");
                                String cartAction = scanner.nextLine().trim();
                                switch (cartAction) {
                                    case "1": // Remove an item from the cart
                                        System.out.print("\nEnter the ID of the item you wish to remove from the cart: ");
                                        String productIdToRemove = scanner.nextLine().trim();
                                        String removeFeedback = (String) frontController.processRequest("removeFromCart", id, productIdToRemove);
                                        System.out.println(removeFeedback);
                                        break;
                                    case "2": // Update an item in the cart
                                        System.out.print("\nEnter the Product ID of the item you wish to update: ");
                                        String productIdToUpdate = scanner.nextLine().trim();
                                        System.out.print("\nEnter the new quantity: ");
                                        try {
                                            int newQuantity = Integer.parseInt(scanner.nextLine().trim());
                                            if (newQuantity < 1)
                                                throw new NumberFormatException("Quantity must be at least 1.");
                                            String updateFeedback = (String) frontController.processRequest("updateCartItem", id, productIdToUpdate, newQuantity);
                                            System.out.println(updateFeedback);
                                        } catch (NumberFormatException e) {
                                            System.out.println("\nInvalid quantity. Please enter a valid number.");
                                        }
                                        break;
                                    case "3": // Checkout
                                        String checkoutFeedback = (String) frontController.processRequest("checkout", id);
                                        System.out.println(checkoutFeedback);
                                        break;
                                    case "4": // Go Back
                                        break;
                                    default:
                                        System.out.println("\nInvalid option, please choose again.");
                                        break;
                                }
                            }
                        }
                        break;


                    case "4": // Exit
                        exit = true;
                        System.out.println("\n-----------------------------------------");
                        System.out.println("Exiting the application.");
                        System.out.println("-----------------------------------------");
                        break;

                    default:
                        System.out.println("\nInvalid option, please try again.");
                }
            }
        }
        else if ("admin".equals(role) && id != null) { // Admin is logged in
                boolean exit = false;
                while (!exit) {
                    System.out.print("\nChoose an action...\n(1) Add/Remove Products (2) Add/Remove Users (3) Add an Admin (4) Update Products (5) Browse Products (6) Exit: ");
                    String choice = scanner.nextLine().trim();

                    switch (choice) {
                        case "1": // Add/Remove Products
                            System.out.print("\nDo you want to (1) Add a new product (2) Remove an existing product (3) Go Back ? ");
                            String productAction = scanner.nextLine().trim();

                            if ("1".equals(productAction)) { // Add a new product
                                System.out.print("\nEnter the new product ID: ");
                                String newProductId = scanner.nextLine().trim();
                                System.out.print("Enter the product Name: ");
                                String productName = scanner.nextLine().trim();
                                System.out.print("Enter the product Description: ");
                                String productDescription = scanner.nextLine().trim();
                                System.out.print("Enter the product Price: ");
                                double productPrice = 0.0;
                                while (true) {
                                    try {
                                        productPrice = Double.parseDouble(scanner.nextLine().trim());
                                        break;
                                    } catch (NumberFormatException e) {
                                        System.out.print("\nInvalid input. Please enter a valid number: ");
                                    }
                                }
                                System.out.print("Enter the product quantity: ");
                                int productQuantity = 0;
                                while (true) {
                                    try {
                                        productQuantity = Integer.parseInt(scanner.nextLine().trim());
                                        break;
                                    }
                                    catch (NumberFormatException e) {
                                        System.out.print("\nInvalid input. Please enter a valid number: ");
                                    }
                                }
                                Object addProductFeedback = frontController.processRequest("addProduct", newProductId, productName, productDescription, productPrice, productQuantity);
                                System.out.println(addProductFeedback);
                            } else if ("2".equals(productAction)) { // Remove an existing product
                                if((int) frontController.processRequest("productsCount") > 0) {
                                    System.out.print("\nEnter the product ID of the product you wish to remove: ");
                                    String productIdToRemove = scanner.nextLine().trim();
                                    String removeProductFeedback = (String) frontController.processRequest("removeProduct", productIdToRemove);
                                    System.out.println(removeProductFeedback);
                                }
                                else {
                                    System.out.println("\n-----------------------------------------");
                                    System.out.println("Store is empty.");
                                    System.out.println("-----------------------------------------");
                                }
                            } else if("3".equals(productAction)){
                                continue;
                            } else {
                                System.out.println("\nInvalid option, please try again.");
                            }
                            break;


                        case "2": // Add/Remove Users
                            System.out.print("\nDo you want to (1) Add a new user or (2) Remove an existing user (3) Go Back? ");
                            String userAction = scanner.nextLine().trim();

                            if ("1".equals(userAction)) { // Add a new user
                                System.out.print("\nEnter the username for the new user: ");
                                String newUsername = scanner.nextLine().trim();
                                System.out.print("Enter the password for the new user: ");
                                String newUserPassword = scanner.nextLine().trim();

                                String addUserFeedback = (String) frontController.processRequest("addUser", id, newUsername, newUserPassword);
                                System.out.println(addUserFeedback);
                            }
                            else if ("2".equals(userAction)) { // Remove an existing user
                                if((int)frontController.processRequest("usersCount") != 0) {
                                    System.out.print("\nEnter the username of the user you wish to remove: ");
                                    String usernameToRemove = scanner.nextLine().trim();

                                    String removeUserFeedback = (String) frontController.processRequest("removeUser", id, usernameToRemove);
                                    System.out.println(removeUserFeedback);
                                }
                                else System.out.println("\nUsers count: 0.");
                            }else if("3".equals(userAction)){
                                continue;
                            } else {
                                System.out.println("\nInvalid option, please try again.");
                            }
                            break;


                        case "3":   // Adding a new admin
                            // Ensure the admin is logged in
                            if (!(Boolean) frontController.processRequest("checkAdmin", id)) {
                                System.out.println("\nUnauthorized: You must be logged in as an admin to perform this action.");
                                break;
                            }

                            while(true) {
                                System.out.print("\nEnter the username for the new admin (or type 'exit' to cancel): ");
                                String newAdminUsername = scanner.nextLine().trim();

                                // Providing an option to exit the loop
                                if ("exit".equalsIgnoreCase(newAdminUsername)) {
                                    System.out.println("Admin addition action cancelled.");
                                    break;
                                }

                                System.out.print("Enter the password for the new admin: ");
                                String newAdminPassword = scanner.nextLine().trim();

                                String registerAdminFeedback = (String) frontController.processRequest("registerAdmin", id, newAdminUsername, newAdminPassword);

                                // Check if the admin was added successfully or if already exists
                                if (registerAdminFeedback.contains("successfully")) {
                                    System.out.println(registerAdminFeedback);
                                    break;
                                }
                                else if (registerAdminFeedback.contains("already exists")) {
                                    System.out.println("\nThat username already exists. Please try a different username.");
                                }
                                else {
                                    System.out.println(registerAdminFeedback);
                                    break;
                                }
                            }
                            break;


                        case "4": // Update Products
                            int productsCount = (int) frontController.processRequest("productsCount");
                            if(productsCount > 0) {
                                Object productsResult = frontController.processRequest("browseProducts");

                                System.out.println("\n-----------------------------------------");
                                if (productsResult instanceof String) {
                                    System.out.println(productsResult);
                                } else if (productsResult instanceof List){
                                    System.out.println("Products:");
                                    List<Product> products = (List<Product>) productsResult;
                                    for (Product product : products) {
                                        System.out.println(product.getName() + " - $" + product.getPrice() + ", quantity - " + product.getQuantityAvailable() + ", description - " + product.getDescription() + ", ID: " + product.getProductId());
                                    }
                                }
                                System.out.println("-----------------------------------------");

                                System.out.print("\nEnter the ID of the product you wish to update (or type 'exit' to cancel): ");
                                String productIdToUpdate = scanner.nextLine().trim();

                                // Providing an option to exit the loop
                                if ("exit".equalsIgnoreCase(productIdToUpdate)) {
                                    System.out.println("\nUpdate action cancelled.");
                                    break;
                                }
                                System.out.print("Enter the new name for the product (or press Enter to skip): ");
                                String newName = scanner.nextLine().trim();

                                System.out.print("Enter the new description for the product (or press Enter to skip): ");
                                String newDescription = scanner.nextLine().trim();


                                Double newPrice = -1.0;
                                while (true) {
                                    System.out.print("Enter the new price for the product (or press Enter to skip): ");
                                    String priceInput = scanner.nextLine().trim();
                                    if (priceInput.isEmpty()) {
                                        break;
                                    }
                                    try {
                                        newPrice = Double.parseDouble(priceInput);
                                        if (newPrice >= 0) {
                                            break;
                                        } else {
                                            System.out.println("\nPrice must be a non-negative number. Please enter a valid price.");
                                        }
                                    } catch (NumberFormatException e) {
                                        System.out.println("\nInvalid format.");
                                    }
                                }

                                Integer newQuantity = -1;
                                while (true) {
                                    System.out.print("Enter the new quantity for the product (or press Enter to skip): ");
                                    String quantityInput = scanner.nextLine().trim();
                                    if (quantityInput.isEmpty()) {
                                        break;
                                    }
                                    try {
                                        newQuantity = Integer.parseInt(quantityInput);
                                        if (newQuantity >= 0) {
                                            break;
                                        } else {
                                            System.out.println("\nQuantity must be non-negative. Please enter a valid quantity.");
                                        }
                                    } catch (NumberFormatException e) {
                                        System.out.println("\nInvalid format.");
                                    }
                                }

                                // model.Product object with the new details
                                Product updatedProduct = new Product(productIdToUpdate, newName, newDescription, newPrice, newQuantity);
                                StringBuilder updateProductFeedback = (StringBuilder) frontController.processRequest("updateProduct", productIdToUpdate, updatedProduct);
                                System.out.println(updateProductFeedback.toString());
                            }
                            else {
                                System.out.println("\n-----------------------------------------");
                                System.out.println("Store is empty.");
                                System.out.println("-----------------------------------------");
                            }
                            break;


                        case "5": // Browse products
                            Object productsResult = frontController.processRequest("browseProducts");
                            System.out.println("\n-----------------------------------------");
                            if (productsResult instanceof String) {
                                System.out.println(productsResult);
                            } else if (productsResult instanceof List){
                                System.out.println("Products:");
                                List<Product> products = (List<Product>) productsResult;
                                for (Product product : products) {
                                    System.out.println(product.getName() + " - $" + product.getPrice() + ", quantity - " + product.getQuantityAvailable() + ", description - " + product.getDescription() + ", ID: " + product.getProductId());
                                }
                            }
                            System.out.println("-----------------------------------------");
                            break;


                        case "6": // Exit
                            exit = true;
                            System.out.println("\n-----------------------------------------");
                            System.out.println("Exiting the application");
                            System.out.println("-----------------------------------------");
                            break;


                        default:
                            System.out.println("\n-----------------------------------------");
                            System.out.println("Invalid option, please try again.");
                            System.out.println("-----------------------------------------");
                    }
                }
            } scanner.close();
        } catch (Exception e) {
            System.err.println("\nClient exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
