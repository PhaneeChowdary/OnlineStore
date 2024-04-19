# Directory structure
CLIENT_DIR = client
RMI_DIR = rmi
MODEL_DIR = model

# Java compiler
JAVAC = javac

# Compilation flags
JAVAC_FLAGS = -d bin

# Default target
all: compile

# Create the bin directory if it doesn't exist
$(shell mkdir -p bin)

# Compile all Java files
compile:
	$(JAVAC) $(JAVAC_FLAGS) $(CLIENT_DIR)/*.java
	$(JAVAC) $(JAVAC_FLAGS) $(RMI_DIR)/*.java
	$(JAVAC) $(JAVAC_FLAGS) $(MODEL_DIR)/*.java

# Clean the compiled files and the bin directory
clean:
	rm -rf bin

# Run the server
run-server:
	java -cp bin rmi.Server

# Run the client
run-client:
	java -cp bin client.Client