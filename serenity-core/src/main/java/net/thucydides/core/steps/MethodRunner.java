package net.thucydides.core.steps;

import net.serenitybdd.instrumentation.MethodProxy;

import java.lang.reflect.Method;

interface MethodRunner {
        Object invokeMethodAndNotifyFailures(Object obj, Method method, Object[] args, MethodProxy proxy, Object result) throws Throwable;
    }
