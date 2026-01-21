package com.emmtrix.isl.core;

import java.util.Objects;

public final class IslSet extends IslObject {
  IslSet(IslContext context, long handle, boolean owned) {
    super(context, handle, owned);
  }

  public static IslSet readFrom(IslContext context, String text) {
    Objects.requireNonNull(context, "context");
    Objects.requireNonNull(text, "text");
    long handle = nativeReadFrom(context.handle(), text);
    return new IslSet(context, handle, true);
  }

  @Override
  protected void releaseNative(long handle) {
    nativeRelease(handle);
  }

  static native long nativeReadFrom(long contextHandle, String text);

  private static native void nativeRelease(long handle);
}
