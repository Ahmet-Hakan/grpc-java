# JDK Logging Configuration Example

This example demonstrates how to configure gRPC-Java to use JDK logging (java.util.logging).

## Why This Matters

When using gRPC-Java as a dependency in your project, the Netty transport will auto-detect and use SLF4J or Log4J if they are on your classpath. If you want to use JDK logging instead, you need to explicitly configure it.

## Running the Example

First, make sure you have the HelloWorld server running:

```bash
./build/install/examples/bin/hello-world-server
```

Then run the logging client:

```bash
./build/install/examples/bin/jdk-logging-client
```

You should see log output similar to:

```
Dec 29, 2024 12:00:00 PM io.grpc.examples.logging.JdkLoggingClient configureLogging
INFO: JDK logging configured successfully
Dec 29, 2024 12:00:00 PM io.grpc.examples.logging.JdkLoggingClient configureLogging
INFO: Netty logger factory: JDK
Dec 29, 2024 12:00:00 PM io.grpc.examples.logging.JdkLoggingClient greet
INFO: Will try to greet world ...
Dec 29, 2024 12:00:00 PM io.grpc.examples.logging.JdkLoggingClient greet
INFO: Greeting: Hello world
```

## Configuration Options

The example demonstrates two ways to configure JDK logging:

### 1. System Property (Easiest)

Set the system property before starting your application:

```bash
java -Dio.netty.logger.type=JDK -jar your-app.jar
```

Or programmatically in a static initializer:

```java
static {
    System.setProperty("io.netty.logger.type", "JDK");
}
```

### 2. Programmatic Configuration (More Control)

The example shows how to configure log levels programmatically:

```java
Logger grpcLogger = Logger.getLogger("io.grpc");
grpcLogger.setLevel(Level.FINE);
```

### 3. Using a logging.properties File (Production)

For production use, create a `logging.properties` file:

```properties
handlers=java.util.logging.ConsoleHandler
io.grpc.level=FINE
java.util.logging.ConsoleHandler.level=ALL
java.util.logging.ConsoleHandler.formatter=java.util.logging.SimpleFormatter
```

Then start your application with:

```bash
java -Djava.util.logging.config.file=logging.properties -jar your-app.jar
```

## Log Levels

gRPC uses these log levels:

- **FINE**: Error and warning messages
- **FINER**: Informational messages
- **FINEST**: Debug messages

Set the level to `FINE` or lower to see gRPC internal debug information.

## Verifying Configuration

To verify that JDK logging is being used, check the system property:

```java
System.out.println("Netty logger: " + System.getProperty("io.netty.logger.type"));
```

## Additional Resources

See [LOGGING.md](../../../LOGGING.md) for comprehensive logging configuration documentation.
