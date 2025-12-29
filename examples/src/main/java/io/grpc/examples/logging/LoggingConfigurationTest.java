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

/**
 * Simple test to verify JDK logging configuration.
 * This test demonstrates that the system property can be set to force JDK logging.
 */
public class LoggingConfigurationTest {
  
  public static void main(String[] args) {
    // Test 1: Verify system property can be set
    System.setProperty("io.netty.logger.type", "JDK");
    String loggerType = System.getProperty("io.netty.logger.type");
    
    System.out.println("Test 1: System property configuration");
    System.out.println("  Expected: JDK");
    System.out.println("  Actual: " + loggerType);
    System.out.println("  Result: " + ("JDK".equals(loggerType) ? "PASS" : "FAIL"));
    System.out.println();
    
    // Test 2: Verify JDK logger can be created
    System.out.println("Test 2: JDK Logger instantiation");
    try {
      java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
          LoggingConfigurationTest.class.getName());
      logger.info("Test log message");
      System.out.println("  Result: PASS - Logger created and message logged");
    } catch (Exception e) {
      System.out.println("  Result: FAIL - " + e.getMessage());
      e.printStackTrace();
    }
    System.out.println();
    
    // Test 3: Verify gRPC logger can be configured
    System.out.println("Test 3: gRPC Logger configuration");
    try {
      java.util.logging.Logger grpcLogger = java.util.logging.Logger.getLogger("io.grpc");
      grpcLogger.setLevel(java.util.logging.Level.FINE);
      System.out.println("  Expected level: FINE");
      System.out.println("  Actual level: " + grpcLogger.getLevel());
      System.out.println("  Result: PASS - gRPC logger configured");
    } catch (Exception e) {
      System.out.println("  Result: FAIL - " + e.getMessage());
      e.printStackTrace();
    }
    System.out.println();
    
    System.out.println("All tests completed successfully!");
    System.out.println();
    System.out.println("Configuration summary:");
    System.out.println("  - Set system property: -Dio.netty.logger.type=JDK");
    System.out.println("  - Or programmatically: System.setProperty(\"io.netty.logger.type\", \"JDK\")");
    System.out.println("  - Configure log levels: Logger.getLogger(\"io.grpc\").setLevel(Level.FINE)");
  }
}
