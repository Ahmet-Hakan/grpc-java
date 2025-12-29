# How to Set Default Logger Factory to JDK in Your Project

This guide explains how to configure gRPC-Java to use JDK (java.util.logging) when you're using it as a dependency in your own project.

## Quick Answer

Add this system property when starting your application:

```bash
-Dio.netty.logger.type=JDK
```

## For Maven Projects

### Option 1: Maven Exec Plugin

Add to your pom.xml:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <version>3.1.0</version>
    <configuration>
        <mainClass>your.main.Class</mainClass>
        <systemProperties>
            <systemProperty>
                <key>io.netty.logger.type</key>
                <value>JDK</value>
            </systemProperty>
        </systemProperties>
    </configuration>
</plugin>
```

### Option 2: In Your Main Class

Add a static initializer:

```java
public class YourMainClass {
    static {
        // Force gRPC to use JDK logging
        System.setProperty("io.netty.logger.type", "JDK");
    }
    
    public static void main(String[] args) {
        // Your application code
    }
}
```

### Option 3: Command Line

When running your JAR:

```bash
java -Dio.netty.logger.type=JDK -jar your-application.jar
```

Or with Maven:

```bash
mvn exec:java -Dexec.mainClass=your.main.Class -Dio.netty.logger.type=JDK
```

## For Gradle Projects

### Option 1: In build.gradle

```gradle
run {
    systemProperty 'io.netty.logger.type', 'JDK'
}

// Or for tests
test {
    systemProperty 'io.netty.logger.type', 'JDK'
}
```

### Option 2: Command Line

```bash
./gradlew run -Dio.netty.logger.type=JDK
```

### Option 3: In Your Code

Same as Maven Option 2 above - add a static initializer.

## Configuring Log Levels

Once JDK logging is configured, control what gets logged:

### Using logging.properties File

Create `logging.properties`:

```properties
handlers=java.util.logging.ConsoleHandler

# Set gRPC to FINE to see debug logs
io.grpc.level=FINE

# Configure console output
java.util.logging.ConsoleHandler.level=ALL
java.util.logging.ConsoleHandler.formatter=java.util.logging.SimpleFormatter
```

Run with:

```bash
java -Djava.util.logging.config.file=logging.properties \
     -Dio.netty.logger.type=JDK \
     -jar your-application.jar
```

### Programmatically

```java
import java.util.logging.Logger;
import java.util.logging.Level;

public class YourApp {
    static {
        System.setProperty("io.netty.logger.type", "JDK");
        
        // Set gRPC log level
        Logger grpcLogger = Logger.getLogger("io.grpc");
        grpcLogger.setLevel(Level.FINE);
    }
    
    public static void main(String[] args) {
        // Your code
    }
}
```

## Verifying It Works

Add this to your code to verify JDK logging is being used:

```java
String loggerType = System.getProperty("io.netty.logger.type");
System.out.println("Netty logger type: " + loggerType);
// Should print: Netty logger type: JDK
```

## Common Issues

### Issue: Logs not appearing

**Solution**: gRPC logs at FINE level by default. Set the log level:

```java
Logger.getLogger("io.grpc").setLevel(Level.FINE);
```

### Issue: SLF4J/Log4J still being used

**Solution**: Make sure the system property is set BEFORE any gRPC classes are loaded. Use a static initializer or set it via command line.

### Issue: Duplicate log entries

**Solution**: Check if you're adding multiple handlers to the root logger. Only add handlers if none exist.

## Additional Resources

- See [LOGGING.md](LOGGING.md) for comprehensive documentation
- See [examples/src/main/java/io/grpc/examples/logging](examples/src/main/java/io/grpc/examples/logging) for working examples
- Run the test: `java io.grpc.examples.logging.LoggingConfigurationTest`

## Summary

The simplest solution is to add the system property when starting your application:

```bash
-Dio.netty.logger.type=JDK
```

This ensures gRPC uses JDK logging regardless of what other logging frameworks are on your classpath.
