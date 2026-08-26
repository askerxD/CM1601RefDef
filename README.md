# CM1601RefDef
Please take into consideration that there is another repository for this project and i had to move the repository due to issues regarding maven and javaFX dependancies. I appreciate your understanding and sincerely apologize for the  inconvenience caused.
Link for the first repo - https://github.com/askerxD/JavaCoursework.git

---

## Project Overview

This project is a JavaFX application designed to manage an inventory of parts, handle point-of-sale (POS) transactions, manage dealer selections, and maintain an audit log. It follows a structured architectural pattern to ensure maintainability and separation of concerns.

### Technologies Used
*   **Java**: Core programming language.
*   **JavaFX**: UI framework for the desktop application.
*   **Maven**: Build automation tool for dependency management and project compilation.

### Project Structure and Responsibilities

The application is organized into several packages, each with distinct responsibilities:

*   **`objects`**:
    *   **Purpose**: Carries the blueprints for storing data. These are simple Plain Old Java Objects (POJOs) that represent the core entities of the application (e.g., `Part`, `Dealer`, `CartItem`, `AuditLogEntry`).
    *   **Implementation**: Contains private fields with public getters and setters for data encapsulation.

*   **`parsers`**:
    *   **Purpose**: Responsible for extracting and parsing data from external files (e.g., text files).
    *   **Implementation**: Contains static methods to read and interpret data, converting raw file content into `objects`.

*   **`utils`**:
    *   **Purpose**: Provides support to services and holds reusable utility methods that don't belong to a specific business domain.
    *   **Implementation**: Includes classes for validation (`ValidationUtil`), sorting (`SortUtil`), searching (`SearchUtil`), and scene navigation (`SceneNavigationUtil`).

*   **`service`**:
    *   **Purpose**: Encapsulates the core business logic and functionality of the application. Services interact with parsers and objects to perform operations.
    *   **Implementation**: Includes `InventoryService` (manages parts inventory, persistence), `PartService` (handles part-specific operations like validation and adding), `CartService` (manages POS cart logic and discounts), `DealerService` (manages dealer data), and `AuditService` (logs application events).

*   **`controllers`**:
    *   **Purpose**: Maps the service functionality to the application's user interface. These classes handle user input, update the view, and delegate business logic to the services.
    *   **Implementation**: FXML controllers (e.g., `HelloController`, `ANPController`, `UPDController`, `POSCheckoutController`, `DealerSelectionController`, `AuditLogController`) manage specific UI scenes and interact with the `service` layer.

### How to Run the Application

1.  **Prerequisites**:
    *   Java Development Kit (JDK) 11 or higher.
    *   Maven installed.

2.  **Clone the Repository**:
    ```bash
    git clone https://github.com/askerxD/JavaCoursework.git
    cd JAVA_Coursework_V2
    ```

3.  **Build the Project**:
    Use Maven to build the project. This will download dependencies and compile the source code.
    ```bash
    mvn clean javafx:run
    ```
    Alternatively, you can build a JAR file:
    ```bash
    mvn clean install
    ```
    Then run the JAR from the `target` directory:
    ```bash
    java -jar target/JAVA_Coursework_V2-1.0-SNAPSHOT.jar
    ```

4.  **IDE Setup (e.g., IntelliJ IDEA)**:
    *   Open the project in your IDE.
    *   Ensure Maven is configured correctly and dependencies are imported.
    *   Run the `HelloApplication.java` file (or `Launcher.java` if present) as a JavaFX Application.

### Data Files

The application uses plain text files for data storage, located in `src/main/java/com/example/javacw/data/`:
*   `inventory_legacy.txt`: Stores part inventory data.
*   `dealers_legacy.txt`: Stores dealer information.
*   `audit_log.txt`: Records all audit events.

Ensure these files exist and are correctly formatted for the application to function as expected.
