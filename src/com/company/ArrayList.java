package com.company;

public class ArrayList<T> implements List<T> {

    T [] arr;
    int size;

    public ArrayList() {
        size = 0;
        arr = (T[]) new Object[10];
    }

    public ArrayList(int length) {
        size = 0;
        arr = (T[]) new Object[length];
    }

    @Override
    public void add(T item) throws Exception {
        if(size == arr.length){
            growArray(); //grows the array * 2
        }
        arr[size] = item;
        size++;
    }

    @Override
    public void add(int pos, T item) throws Exception {
        if(pos >= arr.length || pos < 0)
            throw new Exception("Array Index is out of bounds!");
        if(size == arr.length)
            growArray();
        for(int i = size; i != pos; i--){
            arr[i] = arr[i-1];
        }
        arr[pos] = item;
        size++;
    }

    @Override
    public T get(int pos) throws Exception {
        if(pos >= arr.length || pos < 0)
            throw new Exception("Array Index is out of bounds!");
        return arr[pos];
    }

    @Override
    public T remove(int pos) throws Exception {
        if(pos >= arr.length || pos < 0)
            throw new Exception("Array Index is out of bounds!");
        T item = arr[pos];
        for(int i = pos; i < size-1; i++){
            arr[i] = arr[i+1];
        }
        size--;
        return item;
    }

    @Override
    public int size() {
        return size;
    }
    private void growArray() {
        T [] newArr = (T[]) new Object[arr.length*2];
        for (int i = 0; i < size; i++){
            newArr[i] = arr[i];
        }
        arr = newArr;
    }
}
