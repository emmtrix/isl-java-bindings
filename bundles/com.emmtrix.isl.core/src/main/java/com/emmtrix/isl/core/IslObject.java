package com.emmtrix.isl.core;

import java.util.Objects;

public abstract class IslObject implements AutoCloseable {
  private final IslContext context;
  private final boolean owned;
  private long handle;
  private boolean closed;

  protected IslObject(IslContext context, long handle, boolean owned) {
    this.context = Objects.requireNonNull(context, "context");
    if (handle == 0) {
      throw new IslException("Native handle must not be 0");
    }
    this.handle = handle;
    this.owned = owned;
  }

  protected final IslContext context() {
    return context;
  }

  protected final long handle() {
    synchronized (this) {
      ensureOpen();
      return handle;
    }
  }

  protected final void ensureOpen() {
    if (closed) {
      throw new IllegalStateException("ISL object is closed");
    }
  }

  @Override
  public final void close() {
    synchronized (this) {
      if (closed) {
        return;
      }
      closed = true;
      if (owned && handle != 0) {
        releaseNative(handle);
      }
      handle = 0;
    }
  }

  protected abstract void releaseNative(long handle);
}
