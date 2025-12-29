# gRPC-Java Logging Configuration

## Overview

gRPC-Java uses `java.util.logging` (JDK logging) as its primary logging framework. However, when using different transport implementations, additional configuration may be needed to ensure consistent logging behavior.

## Default Logging Behavior

- **Core gRPC-Java**: Uses `java.util.logging.Logger` directly
- **Netty Transport**: Uses Netty's `InternalLoggerFactory` which auto-detects available logging frameworks in this order:
  1. SLF4J (if present on classpath)
  2. Log4J (if present on classpath)
  3. JDK logging (java.util.logging) - fallback

## Forcing JDK Logging

If you want to ensure that JDK logging is used even when SLF4J or Log4J is on your classpath, you have two options:

### Option 1: System Property (Recommended)

**This is the recommended approach** as it doesn't depend on internal Netty APIs.

Set the following system property before your application starts or when starting the JVM:

```bash
-Dio.netty.logger.type=JDK
```

Or programmatically before any gRPC code is executed:

```java
System.setProperty("io.netty.logger.type", "JDK");
```

### Option 2: Programmatic Configuration (Advanced)

**Note**: This approach uses Netty's internal APIs which may change without notice. Use the system property approach (Option 1) unless you have specific requirements.

Set the logger factory programmatically before creating any gRPC channels or servers:

```java
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.JdkLoggerFactory;

public class MyApplication {
    static {
        // Force Netty to use JDK logging
        InternalLoggerFactory.setDefaultFactory(JdkLoggerFactory.INSTANCE);
    }
    
    public static void main(String[] args) {
        // Your gRPC client/server code here
    }
}
```

**Important**: This configuration must be done before any Netty classes are loaded.

## Configuring JDK Logging Levels

Once JDK logging is configured, you can control the logging levels using a `logging.properties` file:

```properties
# logging.properties
handlers=java.util.logging.ConsoleHandler

# Set default level for all gRPC loggers
io.grpc.level=FINE

# Console handler configuration
java.util.logging.ConsoleHandler.level=ALL
java.util.logging.ConsoleHandler.formatter=java.util.logging.SimpleFormatter

# Optional: Configure specific component levels
io.grpc.netty.level=FINE
io.grpc.internal.level=FINE
```

Then start your application with:

```bash
java -Djava.util.logging.config.file=logging.properties -jar your-app.jar
```

Or configure programmatically:

```java
import java.util.logging.LogManager;
import java.io.FileInputStream;

LogManager.getLogManager().readConfiguration(new FileInputStream("logging.properties"));
```

## Using gRPC as a Dependency

When using gRPC-Java as a Maven or Gradle dependency in your project:

### Maven

Add this to your startup code or main class:

```java
public class Main {
    static {
        // Ensure JDK logging is used
        System.setProperty("io.netty.logger.type", "JDK");
    }
    
    public static void main(String[] args) {
        // Your application code
    }
}
```

Or pass the system property when running:

```bash
mvn exec:java -Dexec.mainClass=your.main.Class -Dio.netty.logger.type=JDK
```

### Gradle

```bash
./gradlew run -Dio.netty.logger.type=JDK
```

Or configure in your `build.gradle`:

```gradle
run {
    systemProperty 'io.netty.logger.type', 'JDK'
}
```

## Logging Levels Mapping

gRPC uses the following log level conventions:

| gRPC Internal Level | Java Logging Level | Use Case |
|---------------------|-------------------|----------|
| ERROR               | FINE              | Error conditions within gRPC components |
| WARNING             | FINE              | Warning conditions within gRPC components |
| INFO                | FINER             | Informational messages |
| DEBUG               | FINEST            | Debug-level messages |

**Note**: Both ERROR and WARNING levels map to Java's FINE level. This is intentional - gRPC keeps most of its logging at or below FINE to avoid cluttering production logs. These are internal diagnostic messages, not application-level errors. To see gRPC debug information, set the log level to FINE or lower.

## Troubleshooting

### Logs not appearing

1. Verify that JDK logging is properly configured:
   ```java
   System.out.println("Netty logger: " + 
       io.netty.util.internal.logging.InternalLoggerFactory.getDefaultFactory().getClass().getName());
   ```
   Should print: `io.netty.util.internal.logging.JdkLoggerFactory`

2. Check that the log level is set correctly. gRPC logs at FINE level or below.

3. Ensure your console handler level allows the messages through:
   ```properties
   java.util.logging.ConsoleHandler.level=ALL
   ```

### SLF4J or Log4J being used instead

If SLF4J or Log4J is on your classpath and being used instead of JDK logging:

1. Set the system property early: `-Dio.netty.logger.type=JDK`
2. Or call `InternalLoggerFactory.setDefaultFactory(JdkLoggerFactory.INSTANCE)` in a static initializer
3. Verify with the troubleshooting command above

## References

- [Java Logging Overview](https://docs.oracle.com/javase/8/docs/technotes/guides/logging/overview.html)
- [Netty Logging Documentation](https://netty.io/wiki/user-guide-for-4.x.html#logging-in-netty)
