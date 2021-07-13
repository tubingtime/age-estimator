package com.company;

public interface List<T> {
    void add(T item) throws Exception;

    void add(int pos, T item) throws Exception;

    T get(int pos) throws Exception;

    T remove(int pos) throws Exception;

    int size();
}
