package net.serenitybdd.instrumentation;

public interface InstrumentedObjectFactory {
    <T> T create(Class<T> type, Interceptor interceptor, Object... parameters);
}

