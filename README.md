# Online Store Application Overview

Online Store Application is designed using Java RMI (Remote Method Invocation). The application allows for managing products, user accounts, shopping carts, and administrative functions.

### Model

- **Product.java**: Represents a product in the online store, including attributes such as product ID, name, description, price, and quantity available.

### RMI Interfaces

- **AdminManagement.java**: Defines the remote interface for administrative actions, including managing products, users, and other admins.
- **ProductManagement.java**: Specifies the remote interface for product-related operations such as adding, updating, removing, and browsing products.
- **ShoppingCartManagement.java**: Outlines the remote interface for shopping cart operations, enabling users to add, remove, update items in their cart, view the cart, and checkout.
- **UserManagement.java**: Describes the remote interface for user management, including user registration and login.

### RMI Implementations

- **AdminManagementImpl.java**: Implements the `AdminManagement` interface, providing concrete functionalities for administrative tasks.
- **ProductManagementImpl.java**: Implements the `ProductManagement` interface, offering methods to manage the product inventory.
- **ShoppingCartManagementImpl.java**: Implements the `ShoppingCartManagement` interface, facilitating shopping cart functionalities for users.
- **UserManagementImpl.java**: Implements the `UserManagement` interface, handling user registration and authentication.

### FrontController
- **FrontController.java:** Serves as the central point of control and management for all incoming requests in the Online Store application. It encapsulates the common processing algorithm for requests, delegating specific tasks to appropriate handlers based on the type of request. This design pattern helps maintain a clean separation of concerns and promotes more manageable code by centralizing request handling logic.

### Server and Client

- **rmi.Server.java**: Sets up the RMI registry and binds instances of the remote objects, making them available for remote method invocation by clients.
- **client.Client.java**: Acts as the client application, looking up remote objects in the RMI registry and invoking their methods to perform operations like product management, user authentication, and shopping cart manipulation.


## To run this project in your local machine:

Follow these steps:
1. Navigate to the project directory.
2. In terminal run `make` command to compile the entire project.
3. Run `make run-server` to initialize and bind remote objects.
4. In another terminal run `make run-client` on client machines to access and interact with the remote objects.
