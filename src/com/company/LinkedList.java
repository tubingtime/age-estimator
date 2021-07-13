package com.company;

public class LinkedList<T> implements List<T> {

    private class Node<T> {
        T data;
        Node<T> next;

        public Node(T value) {
            data = value;
            next = null;
        }
    }

    private int size;
    private Node<T> head;
    private Node<T> tail;

    public LinkedList() {
        size = 0;
        head = null;
        tail = null;
    }


    @Override
    public void add(T item) throws Exception {
        if (head == null) {
            head = new Node<T>(item);
            size++;
        } else {
            Node<T> node = head;
            for (int i = 0; i < size - 1; i++)
                node = node.next;
            tail.next = new Node<T>(item);
            size++;
        }
    }


    public void add(int pos, T item) throws Exception {
        if (pos < 0 || pos > size)
            throw new Exception("List index out of bounds");
        if (pos == 0) {
            Node<T> node = new Node<T>(item);
            node.next = head;
            head = node;
            size++;
        } else {
            Node<T> prev = head;
            for (int i = 0; i < pos - 1; i++)
                prev = prev.next;
            Node<T> newNode = new Node<T>(item);
            newNode.next = prev.next;
            prev.next = newNode;
            size++;
        }
    }

    @Override
    public T get(int pos) throws Exception {
        if (pos < 0 || pos >= size)
            throw new Exception("List index out of bounds");
        Node<T> curr = head;
        for (int i = 0; i < pos; i++)
            curr = curr.next;
        return curr.data;
    }

    @Override
    public T remove(int pos) throws Exception {
        if (pos < 0 || pos > size)
            throw new Exception("List index out of bounds");

        if (pos == 0) {
            Node<T> node = head;
            head = head.next;
            size--;
            return node.data;
        }

        // Not at head removal

        Node<T> prev = head;
        for (int i = 0; i < pos - 1; i++) {
            prev = prev.next;
        }
        Node<T> node = prev.next;
        prev.next = node.next;
        size--;
        return node.data;
    }


    @Override
    public int size() {
        return size;
    }

}
