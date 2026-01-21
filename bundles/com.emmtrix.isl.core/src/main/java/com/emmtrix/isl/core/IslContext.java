package com.emmtrix.isl.core;

import java.util.Objects;

/**
 * Represents an ISL context. Instances are not thread-safe and must be confined to a single
 * thread or externally synchronized.
 */
public final class IslContext implements AutoCloseable {
  private final Object lock = new Object();
  private long handle;
  private boolean closed;

  private IslContext(long handle) {
    this.handle = handle;
  }

  public static IslContext create() {
    NativeLibrary.load();
    long handle = nativeCreate();
    if (handle == 0) {
      throw new IslException("Failed to create ISL context");
    }
    return new IslContext(handle);
  }

  public IslSet readSet(String text) {
    Objects.requireNonNull(text, "text");
    long setHandle;
    synchronized (lock) {
      ensureOpen();
      setHandle = IslSet.nativeReadFrom(handle, text);
    }
    return new IslSet(this, setHandle, true);
  }

  void ensureOpen() {
    if (closed) {
      throw new IllegalStateException("ISL context is closed");
    }
  }

  long handle() {
    synchronized (lock) {
      ensureOpen();
      return handle;
    }
  }

  @Override
  public void close() {
    synchronized (lock) {
      if (closed) {
        return;
      }
      closed = true;
      if (handle != 0) {
        nativeRelease(handle);
        handle = 0;
      }
    }
  }

  private static native long nativeCreate();

  private static native void nativeRelease(long handle);
}
