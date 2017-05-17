package com.google.inject;

public interface Provider<T>{
    T get();
}