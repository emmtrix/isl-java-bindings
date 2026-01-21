package com.emmtrix.isl.core;

final class NativeLibrary {
  private static boolean loaded;

  private NativeLibrary() {}

  static synchronized void load() {
    if (loaded) {
      return;
    }
    System.loadLibrary("isl_jni");
    loaded = true;
  }
}
