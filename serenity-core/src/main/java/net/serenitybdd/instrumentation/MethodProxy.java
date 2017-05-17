package net.serenitybdd.instrumentation;

public interface MethodProxy {
    Object invokeSuper(Object obj, Object[] args) throws Throwable;
}
