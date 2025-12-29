/*
 * Copyright 2024 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.grpc.examples.logging;

import io.grpc.Channel;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.examples.helloworld.HelloReply;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * A simple client demonstrating how to configure JDK logging for gRPC.
 * This example shows how to:
 * 1. Force Netty to use JDK logging
 * 2. Configure logging levels programmatically
 * 3. Enable gRPC debug logging
 */
public class JdkLoggingClient {
  private static final Logger logger = Logger.getLogger(JdkLoggingClient.class.getName());

  // Static initializer to configure logging before any gRPC code runs
  static {
    // Force Netty to use JDK logging (required when SLF4J or Log4J is on classpath)
    // This can also be set via system property: -Dio.netty.logger.type=JDK
    System.setProperty("io.netty.logger.type", "JDK");
    
    // Configure JDK logging programmatically
    configureLogging();
  }

  /**
   * Configure JDK logging to show gRPC debug messages.
   * In production, you would typically use a logging.properties file instead.
   * 
   * Note: This adds a handler to the root logger. If you already have handlers
   * configured, you may see duplicate log entries. In that case, either remove
   * existing handlers or skip adding a new one.
   */
  private static void configureLogging() {
    // Get the root logger
    Logger rootLogger = Logger.getLogger("");
    
    // Only add handler if none exist to avoid duplicates
    if (rootLogger.getHandlers().length == 0) {
      // Create a console handler with appropriate level
      ConsoleHandler handler = new ConsoleHandler();
      handler.setLevel(Level.ALL);
      handler.setFormatter(new SimpleFormatter());
      
      // Add handler to root logger
      rootLogger.addHandler(handler);
    }
    rootLogger.setLevel(Level.INFO);
    
    // Set gRPC package to FINE to see debug logs
    // Note: Most gRPC logs are at FINE level or below
    Logger grpcLogger = Logger.getLogger("io.grpc");
    grpcLogger.setLevel(Level.FINE);
    
    // You can also configure specific components:
    // Logger.getLogger("io.grpc.netty").setLevel(Level.FINE);
    // Logger.getLogger("io.grpc.internal").setLevel(Level.FINE);
    
    logger.info("JDK logging configured successfully");
    logger.info("Netty logger factory: " + 
        System.getProperty("io.netty.logger.type", "not set"));
  }

  private final GreeterGrpc.GreeterBlockingStub blockingStub;

  /** Construct client for accessing HelloWorld server using the existing channel. */
  public JdkLoggingClient(Channel channel) {
    blockingStub = GreeterGrpc.newBlockingStub(channel);
  }

  /** Say hello to server. */
  public void greet(String name) {
    logger.log(Level.INFO, "Will try to greet {0} ...", name);
    HelloRequest request = HelloRequest.newBuilder().setName(name).build();
    HelloReply response;
    try {
      response = blockingStub.sayHello(request);
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }
    logger.log(Level.INFO, "Greeting: {0}", response.getMessage());
  }

  /**
   * Greet server. If provided, the first element of {@code args} is the name to use in the
   * greeting. The second argument is the target server.
   */
  public static void main(String[] args) throws Exception {
    String user = "world";
    String target = "localhost:50051";
    
    if (args.length > 0) {
      if ("--help".equals(args[0])) {
        System.err.println("Usage: [name [target]]");
        System.err.println("");
        System.err.println("  name    The name you wish to be greeted by. Defaults to " + user);
        System.err.println("  target  The server to connect to. Defaults to " + target);
        System.exit(1);
      }
      user = args[0];
    }
    if (args.length > 1) {
      target = args[1];
    }

    // Create a communication channel to the server
    ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
        .build();
    try {
      JdkLoggingClient client = new JdkLoggingClient(channel);
      client.greet(user);
    } finally {
      // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
      // resources the channel should be shut down when it will no longer be used. If it may be used
      // again leave it running.
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }
}
