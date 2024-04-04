package rmi;

import model.Product;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ProductManagementImpl extends UnicastRemoteObject implements ProductManagement {
    public static ConcurrentHashMap<String, Product> products = new ConcurrentHashMap<>();

    public ProductManagementImpl() throws RemoteException {
        super();
    }

    @Override
    public void addProduct(Product product) throws RemoteException {
        products.put(product.getProductId(), product);
    }

    @Override
    public boolean updateProduct(String productId, Product updatedProduct) throws RemoteException {
        Product existingProduct = products.get(productId);
        if (existingProduct == null) {
            return false;
        }

        StringBuilder updates = new StringBuilder("Updated fields for Product ID " + productId + ":");
        boolean updated = false;

        if (!existingProduct.getName().equals(updatedProduct.getName())) {
            existingProduct.setName(updatedProduct.getName());
            updates.append("\nName");
            updated = true;
        }
        if (!existingProduct.getDescription().equals(updatedProduct.getDescription())) {
            existingProduct.setDescription(updatedProduct.getDescription());
            updates.append("\nDescription");
            updated = true;
        }
        if (existingProduct.getPrice() != updatedProduct.getPrice()) {
            existingProduct.setPrice(updatedProduct.getPrice());
            updates.append("\nPrice");
            updated = true;
        }
        if (existingProduct.getQuantityAvailable() != updatedProduct.getQuantityAvailable()) {
            existingProduct.setQuantityAvailable(updatedProduct.getQuantityAvailable());
            updates.append("\nQuantity Available");
            updated = true;
        }

        if (!updated) {
            return false;
        }

        products.replace(productId, existingProduct);
        return true;
    }

    @Override
    public String removeProduct(String productId) throws RemoteException {
        if(products.remove(productId) != null){
            return "\n`" + productId + "` removed.";
        };
        return "\n`" + productId + "` not found.";
    }

    @Override
    public List<Product> browseProducts() throws RemoteException {
        return new ArrayList<>(products.values());
    }

    @Override
    public String purchaseProduct(String productId, int quantity) throws RemoteException {
        Product product = products.get(productId);
        if (product == null) {
            return "\nProduct not found: " + productId;
        }

        synchronized (product) { // Synchronize on the product to handle concurrent purchases safely
            int currentQuantity = product.getQuantityAvailable();
            if (quantity > currentQuantity) {
                return "\nInsufficient quantity available for Product ID " + productId + ". Available: " + currentQuantity;
            }

            product.setQuantityAvailable(currentQuantity - quantity);
            return "\nPurchase successful for Product ID " + productId + ". Quantity purchased: " + quantity;
        }
    }

    @Override
    public Product getProductDetails(String productId) throws RemoteException {
        return products.get(productId);
    }
}
