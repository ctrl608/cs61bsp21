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
		assert newLength >= size;
		T[] temp = (T[]) new Object[newLength];
		int prevLength= array.length;
		//[0,head) (head,preLen)
		System.arraycopy(array, 0, temp, 0, head);
		System.arraycopy(array,head,temp,head+newLength-prevLength,size-head);
		head=head+newLength-prevLength;
		array=temp;
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
		if (size >= array.length) {
			resize(array.length * 2);
		}
		array[(head+size+array.length)%array.length] = item;
		size += 1;
	}

	public T removeLast() {
		size-=1;
	}

	public T removeFirst() {
		head=(head+1)%array.length;
		size-=1;
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
		return size != 0;
	}


}
