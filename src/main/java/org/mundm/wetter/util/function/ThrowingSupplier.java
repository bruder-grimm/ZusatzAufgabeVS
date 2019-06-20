package org.mundm.wetter.util.function;

@FunctionalInterface
public interface ThrowingSupplier<T> {
    T get() throws Throwable;
}
