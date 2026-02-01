package deque;

import org.junit.Test;

public class ArrayDeque<T> {
	private T[] array;
	private int size;
	private int head;
	private final double factor = 2.0;

	public ArrayDeque() {
		size = 0;//user uses
		array = (T[]) new Object[8];
		head = 0;
	}

	public ArrayDeque(int size) {
		this.size = size;
		array = (T[]) new Object[(int) (size * factor)];
		head = 0;
	}

	public ArrayDeque(T... items) {
		this.size = items.length;
		array = (T[]) new Object[(int) (size * factor + 1)];
		head = 0;
		System.arraycopy(items, 0, array, 0, size);
	}

	private void resize(int newLength) {
		T[] newArray = (T[]) new Object[newLength];
		for (int i = 0; i < size; i++) {
			newArray[i] = array[(head + i) % array.length];
		}
		array = newArray;
		head = 0;
	}

	public T get(int index) {
		if (index < 0 || index >= size) return null;
		int realIndex = (index + head) % array.length;
		return array[realIndex];
	}


	public void addFirst(T item) {
		if (size == array.length) {
			resize(array.length * 2);
		}
		head = (head - 1 + array.length) % array.length;
		array[head] = item;
		size += 1;
	}

	public void addLast(T item) {
		if (size == array.length) {
			resize(array.length * 2);
		}
		array[(head + size) % array.length] = item;
		size += 1;
	}

	public T removeLast() {
		if (isEmpty()) {
			return null;
		}
		int lastIndex = (head + size - 1 + array.length) % array.length;
		T item = array[lastIndex];
		array[lastIndex] = null;
		size -= 1;
		return item;
	}

	public T removeFirst() {
		if (isEmpty()) {
			return null;
		}
		T item = array[head];
		array[head] = null;
		head = (head + 1) % array.length;
		size -= 1;
		return item;
	}

	public void printDeque() {
		for (int i = 0; i < size; ++i) {
			System.out.print(get(i) + " ");
		}
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}


}
