# SWANG-Tech_Full_Project
SWANG Tech's repository for the whole term project


# Build & Run Instructions

## Prerequisites
Before building and running the project, ensure you have:
- **Java 17+** installed and set in your system's PATH
- **Maven** installed (`mvn -version` should confirm this)

## Building the Project
To build the project, navigate to the root directory and run:

### On Unix/Linux (MacOS, Ubuntu, etc.)
```sh
chmod +x build.sh  # Only needed the first time
./build.sh # to build
./run.sh # to run

```

### On Windows (Command Prompt)
```cmd
build.bat
run.bat
```

This will clean, compile, package, and run the Java application.

## Running the Application
If the build is successful, you can run the application manually using:
```sh
java -jar target/beauty-product-catalog-1.0-SNAPSHOT.jar
```

## Running Tests
To run unit tests:
```sh
mvn test
```

## Troubleshooting
- If Maven is not found, install it from [Maven Apache](https://maven.apache.org/download.cgi).
- Ensure Java 17+ is installed (`java -version`).
- Check for missing dependencies using `mvn dependency:resolve`.


