package net.serenitybdd.instrumentation;

import java.lang.reflect.Method;

public interface Interceptor {
    Object intercept(Object delegate, Method method, Object[] args, MethodProxy methodProxy) throws Throwable;
}
