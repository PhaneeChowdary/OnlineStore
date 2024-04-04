package client;

import model.Product;
import rmi.AdminManagement;
import rmi.ProductManagement;
import rmi.ShoppingCartManagement;
import rmi.UserManagement;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ProductManagement productManagement = (ProductManagement) registry.lookup("ProductManagement");
            UserManagement userManagement = (UserManagement) registry.lookup("UserManagement");
            ShoppingCartManagement shoppingCartManagement = (ShoppingCartManagement) registry.lookup("ShoppingCartManagement");
            AdminManagement adminManagement = (AdminManagement) registry.lookup("AdminManagement");


            // Prompt for login or signup
            String action = "";
            while (!action.equals("login") && !action.equals("signup")) {
                System.out.print("Do you want to Login or Signup? (login/signup): ");
                action = scanner.nextLine().trim().toLowerCase();
                if (!action.equals("login") && !action.equals("signup")) {
                    System.out.println("Invalid input. Please type 'login' or 'signup'.");
                }
            }

            String role = "";
            while (!role.equals("user") && !role.equals("admin")) {
                System.out.print("Are you a User or an Admin? (user/admin): ");
                role = scanner.nextLine().trim().toLowerCase();
                if (!role.equals("user") && !role.equals("admin")) {
                    System.out.println("Invalid input. Please type 'user' or 'admin'.");
                }
            }

            String username, password, feedback, id = null;

            // Signup
            if (action.equals("signup")) {
                System.out.println("\nLet's setup your account");
                System.out.print("Enter username: ");
                username = scanner.nextLine();
                System.out.print("Enter password: ");
                password = scanner.nextLine();
                if (role.equals("user")) {
                    feedback = userManagement.registerUser(username, password);
                    id = userManagement.loginUser(username, password);
                } else {
                    feedback = adminManagement.registerAdmin(username, password);
                    id = adminManagement.loginAdmin(username, password);
                }
                System.out.println(feedback);
                System.out.println("Logged in as `" + username + "`");
                System.out.println("-----------------------------------------");
            }

            // Login
            if (action.equals("login")) {
                System.out.print("Enter username: ");
                username = scanner.nextLine();
                System.out.print("Enter password: ");
                password = scanner.nextLine();
                if (role.equals("user")) {
                    id = userManagement.loginUser(username, password);
                } else {
                    id = adminManagement.loginAdmin(username, password);
                }
                if(id != null) {
                    System.out.println("\nLogged in as `" + username + "`");
                    System.out.println("-----------------------------------------");
                }
            }

            if ("user".equals(role) && id != null) { // User is logged in
                boolean exit = false;
                while (!exit) {
                    System.out.print("\nChoose an action...\n(1) Browse Products (2) Purchase Products (3) Cart (4) Exit: ");
                    String choice = scanner.nextLine().trim();

                    switch (choice) {
                        case "1": // Browse Products
                            List<Product> products = productManagement.browseProducts();
                            System.out.println("\nProducts: ");
                            if(!products.isEmpty()) {
                                for (Product product : products) {
                                    if(product.getQuantityAvailable() >= 1) {
                                        System.out.println(product.getName() + " - $" + product.getPrice() + ", quantity - " + product.getQuantityAvailable() + ", ID: " + product.getProductId());
                                    }
                                }
                                System.out.println("-----------------------------------------");
                            }
                            else System.out.println("\nStore is empty.");
                            break;

                        case "2": // Purchase Products
                            System.out.print("\nEnter the Product ID you wish to add to cart: ");
                            String productId = scanner.nextLine().trim();

                            // Check product availability
                            Product product = productManagement.getProductDetails(productId);
                            if (product == null) {
                                System.out.println("\nProduct with ID " + productId + " does not exist.");
                                break;
                            }

                            System.out.println("\nProduct available: " + product.getName() + " - Stock: " + product.getQuantityAvailable());
                            System.out.print("Enter the quantity you wish to add: ");
                            int quantity;
                            try {
                                quantity = Integer.parseInt(scanner.nextLine().trim());
                                if (quantity < 1) {
                                    System.out.println("\nQuantity must be at least 1.");
                                    break;
                                }
                                if (quantity > product.getQuantityAvailable()) {
                                    System.out.println("\nOnly " + product.getQuantityAvailable() + " units available. Please enter a lower quantity.");
                                    break;
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("\nInvalid quantity. Please enter a valid number.");
                                break;
                            }

                            // Add to cart
                            String addToCartFeedback = shoppingCartManagement.addToCart(id, productId, quantity);
                            System.out.println(addToCartFeedback);

                            // Step 2: Ask if the user wants to check out
                            System.out.print("\nDo you want to proceed to checkout? (yes/no) ");
                            String proceedToCheckout = scanner.nextLine().trim().toLowerCase();
                            if ("yes".equals(proceedToCheckout)) { // Step 3: Checkout
                                String checkoutFeedback = shoppingCartManagement.checkout(id);
                                System.out.println(checkoutFeedback);
                            } else {
                                System.out.println("\nYou can continue shopping or choose another action.");
                            }
                            System.out.println("-----------------------------------------");
                            break;

                        case "3": // View cart items
                            String cartContents = shoppingCartManagement.viewCart(id);
                            System.out.println(cartContents);

                            System.out.println("\nDo you want to (1) Remove item(s) or (2) Update item(s)? ");
                            String cartAction = scanner.nextLine().trim();

                            if ("1".equals(cartAction)){ // Remove an item from the cart
                                System.out.print("\nEnter the Product ID of the item you wish to remove from the cart: ");
                                String productIdToRemove = scanner.nextLine().trim();
                                String removeFeedback = shoppingCartManagement.removeFromCart(id, productIdToRemove);
                                System.out.println(removeFeedback);
                            }
                            else if("2".equals(cartAction)){ // Update an item in the cart
                                System.out.print("\nEnter the Product ID of the item you wish to update: ");
                                String productIdToUpdate = scanner.nextLine().trim();
                                System.out.print("\nEnter the new quantity: ");
                                int newQuantity;
                                try {
                                    newQuantity = Integer.parseInt(scanner.nextLine().trim());
                                    if (newQuantity < 1) {
                                        throw new NumberFormatException("\nQuantity must be at least 1.");
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.print("\nInvalid quantity. Please enter a valid number: ");
                                    break;
                                }
                                String updateFeedback = shoppingCartManagement.updateCartItem(id, productIdToUpdate, newQuantity);
                                System.out.println(updateFeedback);
                            }

                                break;

                        case "4": // Exit
                            exit = true;
                            System.out.println("\nYou choose to exit the application.");
                            System.out.println("-----------------------------------------");
                            break;

                        default:
                            System.out.println("\nInvalid option, please try again.");
                            System.out.println("-----------------------------------------");
                    }
                }
            } else if ("admin".equals(role) && id != null) { // Admin is logged in
                boolean exit = false;
                while (!exit) {
                    System.out.print("\nChoose an action...\n(1) Add/Remove Products (2) Add/Remove Users (3) Update Products (4) Browse Products (5)Exit: ");
                    String choice = scanner.nextLine().trim();

                    switch (choice) {
                        case "1": // Add/Remove Products
                            System.out.print("\nDo you want to (1) Add a new product or (2) Remove an existing product? ");
                            String productAction = scanner.nextLine().trim();

                            if ("1".equals(productAction)) { // Add a new product
                                System.out.print("\nEnter the new Product ID: ");
                                String newProductId = scanner.nextLine().trim();
                                System.out.print("Enter the Product Name: ");
                                String productName = scanner.nextLine().trim();
                                System.out.print("Enter the Product Description: ");
                                String productDescription = scanner.nextLine().trim();
                                System.out.print("Enter the Product Price: ");
                                double productPrice = Double.parseDouble(scanner.nextLine().trim());
                                System.out.print("Enter the Product Quantity: ");
                                int productQuantity = Integer.parseInt(scanner.nextLine().trim());

                                Product newProduct = new Product(newProductId, productName, productDescription, productPrice, productQuantity);
                                String addProductFeedback = adminManagement.addProduct(id, newProduct);
                                System.out.println(addProductFeedback);
                            } else if ("2".equals(productAction)) { // Remove an existing product
                                System.out.print("\nEnter the Product ID of the product you wish to remove: ");
                                String productIdToRemove = scanner.nextLine().trim();

                                String removeProductFeedback = adminManagement.removeProduct(id, productIdToRemove);
                                System.out.println(removeProductFeedback);
                            } else {
                                System.out.println("\nInvalid option, please try again.");
                            }
                            System.out.println("-----------------------------------------");
                            break;

                        case "2": // Add/Remove Users
                            System.out.print("\nDo you want to (1) Add a new user or (2) Remove an existing user? ");
                            String userAction = scanner.nextLine().trim();

                            if ("1".equals(userAction)) { // Add a new user
                                System.out.print("\nEnter the username for the new user: ");
                                String newUsername = scanner.nextLine().trim();
                                System.out.print("Enter the password for the new user: ");
                                String newUserPassword = scanner.nextLine().trim();

                                String addUserFeedback = adminManagement.addUser(id, newUsername, newUserPassword);
                                System.out.println(addUserFeedback);
                            }
                            else if ("2".equals(userAction)) { // Remove an existing user
                                System.out.print("\nEnter the username of the user you wish to remove: ");
                                String usernameToRemove = scanner.nextLine().trim();

                                String removeUserFeedback = adminManagement.removeUser(id, usernameToRemove);
                                System.out.println(removeUserFeedback);
                            }
                            else {
                                System.out.println("\nInvalid option, please try again.");
                            }
                            System.out.println("-----------------------------------------");
                            break;

                        case "3": // Update Products
                            System.out.print("\nEnter the Product ID of the product you wish to update: ");
                            String productIdToUpdate = scanner.nextLine().trim();

                            System.out.print("Enter the new name for the product (or press Enter to skip): ");
                            String newName = scanner.nextLine().trim();
                            System.out.print("Enter the new description for the product (or press Enter to skip): ");
                            String newDescription = scanner.nextLine().trim();
                            System.out.print("Enter the new price for the product (or press Enter to skip): ");
                            String newPriceStr = scanner.nextLine().trim();
                            System.out.print("Enter the new quantity for the product (or press Enter to skip): ");
                            String newQuantityStr = scanner.nextLine().trim();

                            // Assuming defaults for skipped fields
                            double newPrice = newPriceStr.isEmpty() ? -1 : Double.parseDouble(newPriceStr);
                            int newQuantity = newQuantityStr.isEmpty() ? -1 : Integer.parseInt(newQuantityStr);

                            // Assuming an updateProduct method that allows partial updates and skips fields with default values (-1 for numbers)
                            String updateProductFeedback = adminManagement.updateProduct(id, productIdToUpdate, newName, newDescription, newPrice, newQuantity);
                            System.out.println(updateProductFeedback);
                            System.out.println("-----------------------------------------");
                            break;

                        case "4": // Browse products
                            List<Product> products = productManagement.browseProducts();
                            System.out.println("\nProducts: ");
                            if(!products.isEmpty()) {
                                for (Product product : products) {
                                    if(product.getQuantityAvailable() >= 1) {
                                        System.out.println(product.getName() + " - $" + product.getPrice() + ", quantity - " + product.getQuantityAvailable() + ", ID: " + product.getProductId());
                                    }
                                }
                                System.out.println("-----------------------------------------");
                            }
                            else System.out.println("\nStore is empty.");
                            break;

                        case "5": // Exit
                            exit = true;
                            System.out.println("\nYou choose to exit the application");
                            System.out.println("-----------------------------------------");
                            break;

                        default:
                            System.out.println("\nInvalid option, please try again.");
                            System.out.println("-----------------------------------------");
                    }
                }
            } else {
                System.out.println("\nLogin or role not recognized.");
                System.out.println("-----------------------------------------");
            }
            scanner.close();
        } catch (Exception e) {
            System.err.println("\nClient exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
