package deque;


import java.util.Iterator;
import java.util.Objects;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {
    private static class Node<T> {
        T value;
        Node<T> prev, next;

        Node(T value, Node<T> prev, Node<T> next) {
            this.value = value;
            this.next = next;
            this.prev = prev;
        }
    }

    private int size;
    private Node<T> sentinel;

    public LinkedListDeque() {
        sentinel = new Node<T>(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }
    private boolean isNotValidIndex(int index) {
        return index < 0 || index >= size;
    }

    @Override
    public void addFirst(T item) {
        Node<T> adding = new Node<>(item, sentinel, sentinel.next);
        sentinel.next = adding;
        adding.next.prev = adding;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        Node<T> adding = new Node<>(item, sentinel.prev, sentinel);
        sentinel.prev = adding;
        adding.prev.next = adding;
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node<T> curr = sentinel.next;
        for (int i = 0; i < size; ++i) {
            System.out.print(curr.value + " ");
            curr = curr.next;
        }
    }

    private Node<T> getNode(int index) {
        if (isNotValidIndex(index)) {
            return null;
        }
        Node<T> curr = sentinel;
        for (int i = 0; i <= index; i += 1) {
            curr = curr.next;
        }
        return curr;
    }

    @Override
    public T get(int index) {
        Node<T> temp = getNode(index);
        return temp == null ? null : temp.value;
    }

    public T getRecursive(int index) {
        if (isNotValidIndex(index)) {
            return null;
        }
        return getRecursiveHelper(index, sentinel.next);
    }

    private T getRecursiveHelper(int index, Node<T> curr) {
        if (index == 0) {
            return curr.value;
        }
        return getRecursiveHelper(index - 1, curr.next);
    }
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        Node<T> removing = sentinel.next;
        sentinel.next = removing.next;
        removing.next.prev = sentinel;
        size -= 1;
        return removing.value;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        Node<T> removing = sentinel.prev;
        sentinel.prev = removing.prev;
        removing.prev.next = sentinel;
        size -= 1;
        return removing.value;
    }

    private class DequeIterator implements Iterator<T> {
        private Node<T> curr = sentinel.next;

        @Override
        public boolean hasNext() {
            return curr != sentinel;
        }

        @Override
        public T next() {
            T item = curr.value;
            curr = curr.next;
            return item;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new DequeIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Deque<?>)) {
            return false;
        }
        Deque<?> other = (Deque<?>) o;
        if (other.size() != this.size()) {
            return false;
        }
        for (int i = 0; i < this.size(); i++) {
            if (!Objects.equals(this.get(i), other.get(i))) {
                return false;
            }
        }
        return true;
    }
}
