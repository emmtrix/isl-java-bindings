package com.emmtrix.isl.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class JavaApiSmokeTest {
  @Test
  void javaTestsAreWiredIntoBuild() {
    assertTrue(true, "JUnit tests should run during mvn verify.");
  }
}
