package rmi;

import model.Product;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.List;

public class FrontControllerImpl extends UnicastRemoteObject implements FrontController {
    private ProductManagement productManagement;
    private UserManagement userManagement;
    private ShoppingCartManagement shoppingCartManagement;
    private AdminManagement adminManagement;

    public FrontControllerImpl(ProductManagement pm, UserManagement um, ShoppingCartManagement scm, AdminManagement am) throws RemoteException {
        super();
        this.productManagement = pm;
        this.userManagement = um;
        this.shoppingCartManagement = scm;
        this.adminManagement = am;
    }

    @Override
    public Object processRequest(String requestType, Object... parameters) throws RemoteException {
        switch (requestType) {
            case "loginUser":
                return userManagement.loginUser((String) parameters[0], (String) parameters[1]);

            case "registerUser":
                return userManagement.registerUser((String) parameters[0], (String) parameters[1]);

            case "loginAdmin":
                return adminManagement.loginAdmin((String) parameters[0], (String) parameters[1]);

            case "browseProducts":
                List<Product> products = productManagement.browseProducts();
                if (products.isEmpty()) {
                    return "Store is empty.";
                }
                return products;

            case "updateProduct":
                String productId = (String) parameters[0];
                Product updatedProduct = (Product) parameters[1];
                return productManagement.updateProduct(productId, updatedProduct);

            case "getProductDetails":
                Product product = productManagement.getProductDetails((String) parameters[0]);
                if (product != null) {
                    return product;
                }
                return "\nProduct with ID " + parameters[0] + " does not exist.";

            case "viewCart":
                return shoppingCartManagement.viewCart((String) parameters[0]);

            case "removeFromCart":
                return shoppingCartManagement.removeFromCart((String) parameters[0], (String) parameters[1]);

            case "updateCartItem":
                return shoppingCartManagement.updateCartItem((String) parameters[0], (String) parameters[1], (Integer) parameters[2]);

            case "addToCart":
                String addToCartFeedback = shoppingCartManagement.addToCart((String) parameters[0], (String) parameters[1], (int) parameters[2]);
                return addToCartFeedback;

            case "checkout":
                String checkoutFeedback = shoppingCartManagement.checkout((String) parameters[0]);
                return checkoutFeedback;

            case "addProduct":
                Product productToAdd = new Product((String) parameters[0], (String) parameters[1], (String) parameters[2], (Double) parameters[3], (Integer) parameters[4]);
                return productManagement.addProduct(productToAdd);

            case "removeProduct":
                return productManagement.removeProduct((String) parameters[0]);

            case "productsCount":
                return productManagement.productsCount();

            case "addUser":
                return adminManagement.addUser((String) parameters[0], (String) parameters[1], (String) parameters[2]);

            case "removeUser":
                return adminManagement.removeUser((String) parameters[0], (String) parameters[1]);

            case "usersCount":
                return adminManagement.usersCount();

            case "registerAdmin":
                return adminManagement.registerAdmin((String) parameters[0], (String) parameters[1], (String) parameters[2]);

            case "checkAdmin":
                return adminManagement.isAdmin((String) parameters[0]);


            default:
                return "Invalid request type";
        }
    }
}
