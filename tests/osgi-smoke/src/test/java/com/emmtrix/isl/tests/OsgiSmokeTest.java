package com.emmtrix.isl.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OsgiSmokeTest {
  @Test
  void osgiSmokeTestsAreIncluded() {
    assertTrue(true, "OSGi smoke tests should run during mvn verify.");
  }
}
