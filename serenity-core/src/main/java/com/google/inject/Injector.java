package com.google.inject;
public interface Injector{
    <T> Provider<T> getProvider(Class<T> type);

    <T> T getInstance(Class<T> type);
}
