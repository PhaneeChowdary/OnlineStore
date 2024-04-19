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
    public String addProduct(Product product) throws RemoteException {
        if(products.get(product.getProductId()) == null) {
            products.put(product.getProductId(), product);
            return "\n`" + product.getName() + "` added successfully";
        }
        return "\nProduct with ID: `" + product.getProductId() + "` already exists";
    }

    @Override
    public StringBuilder updateProduct(String productId, Product updatedProduct) throws RemoteException {
        Product existingProduct = products.get(productId);
        if (existingProduct == null) {
                return new StringBuilder("\nProduct with ID: `" + productId + "` not found");
        }

        boolean updated = false;
        if(!updatedProduct.getName().isEmpty()){
            existingProduct.setName(updatedProduct.getName());
            updated = true;
        }
        if(!updatedProduct.getDescription().isEmpty()){
            existingProduct.setDescription(updatedProduct.getDescription());
            updated = true;
        }
        if(updatedProduct.getPrice() != -1.0){
            existingProduct.setPrice(updatedProduct.getPrice());
            updated = true;
        }
        if(updatedProduct.getQuantityAvailable() != -1){
            existingProduct.setQuantityAvailable(updatedProduct.getQuantityAvailable());
            updated = true;
        }
        if (!updated) {
            return new StringBuilder("\nNo updates has been done");
        }

        products.replace(productId, existingProduct);
        return new StringBuilder("\n`" + productId + "` product details updated successfully.");
    }

    @Override
    public String removeProduct(String productId) throws RemoteException {
        Product product = getProductDetails(productId);
        if(products.remove(productId) != null){
            return "\n`" + product.getName() + "` removed.";
        };
        return "\nProduct with ID: `" + productId + "` not found.";
    }

    @Override
    public List<Product> browseProducts() throws RemoteException {
        return new ArrayList<>(products.values());
    }

    @Override
    public Product getProductDetails(String productId) throws RemoteException {
        return products.get(productId);
    }

    @Override
    public int productsCount() throws RemoteException {
        return products.size();
    }
}
