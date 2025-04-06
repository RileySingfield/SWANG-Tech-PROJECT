# SWANG-Tech Beauty Catalog
A Java SWING desktop application that allows users to manage beauty products. Includes functionality such as: 
- Resister new users
- Login/logout functionality
- Add Products to the catalog (logged in users)
- Edit catalog products (logged in users)
- Delete catalog products (logged in users)
- Add products to wishlist (logged in users)
- Delete products from wishlist (logged in users)
- View items in wishlist - with product details (logged in users)
- Search Producucts based on keywords (clear search)
- Apply filters (category, price, rating)
- Clear filters
- Filter & search together
- View specifc product descriptions
- Scrollable view of all catalog products
- Unit & integration tests with JUnit 5
- 📄 [User Guide](https://docs.google.com/document/d/1rAz3nZ8IyznTzIgEenUde7T2FgYvx7yiR-c_I1MMFsg/edit?usp=sharing)

# Build & Run Instructions

## Prerequisites
Before building and running the project, ensure you have:
- **Java 17+** installed and set in your system's PATH
- **Maven** installed (`mvn -version` should confirm this)

## Building the Project
There are various different ways to run our project 

### Option 1 - Run with maven 
For development or testing:

```bash
mvn clean compile exec:java
```

### Option 2 -  Run the Fat JAR
Build the project, first: 
```sh
mvn clean package
```

Then run with: 
```sh
java -jar target/BeautyCatalog-1.0-SNAPSHOT-jar-with-dependencies.jar
```

This will clean, compile, package, and run the Java application.

## Option 3 -  Windows Batch File (Double-Click)
Double click build.bat and then run.bat

## Option 4 - Mac/Linux Shell Script
To make executable, run: 
```bash
chmod +x run.sh
```
Build the project: 
```bash
.\build.sh
```
Run the project: 
```bash
.\run.sh
```

## Running Tests
All tests are written with JUnit 5 and Mockito
Run all unit and integration tests:
```bash
mvn test
```
View test reports in:
```
target/surefire-reports/
```

## Troubleshooting
- If Maven is not found, install it from [Maven Apache](https://maven.apache.org/download.cgi).
- Ensure Java 17+ is installed (`java -version`).
- Check for missing dependencies using `mvn dependency:resolve`.


