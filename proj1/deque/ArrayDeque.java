package deque;

import java.util.Iterator;
import java.util.Objects;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] array;
    private int size;
    private int head;
    private final double factor = 2.0;

    public ArrayDeque() {
        size = 0;//user uses
        array = (T[]) new Object[8];
        head = 0;
    }

    private void resize(int newLength) {
        T[] newArray = (T[]) new Object[newLength];
        for (int i = 0; i < size; i++) {
            newArray[i] = array[(head + i) % array.length];
        }
        array = newArray;
        head = 0;
    }

    private void checkResizeDown() {
        if (array.length >= 16 && size < array.length / 4) {
            resize(array.length / 2);
        }
    }


    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        int realIndex = (index + head) % array.length;
        return array[realIndex];
    }

    @Override
    public void addFirst(T item) {
        if (size == array.length) {
            resize(array.length * 2);
        }
        head = (head - 1 + array.length) % array.length;
        array[head] = item;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        if (size == array.length) {
            resize(array.length * 2);
        }
        array[(head + size) % array.length] = item;
        size += 1;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        int lastIndex = (head + size - 1 + array.length) % array.length;
        T item = array[lastIndex];
        array[lastIndex] = null;
        size -= 1;
        checkResizeDown();
        return item;
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T item = array[head];
        array[head] = null;
        head = (head + 1) % array.length;
        size -= 1;
        checkResizeDown();
        return item;
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size; ++i) {
            System.out.print(get(i) + " ");
        }
    }

    @Override
    public int size() {
        return size;
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
        if (other.size() != size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!Objects.equals(get(i), other.get(i))) {
                return false;
            }
        }
        return true;
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int index;

        public ArrayDequeIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public T next() {
            T item = get(index);
            index += 1;
            return item;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }
}
