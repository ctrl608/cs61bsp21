package deque;

import java.util.Iterator;
import java.util.Objects;

public class LinkedListDeque<T> implements Iterable<T> {
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
		if (index < 0 || index >= size) {
			System.out.println("数组越界!");
			return true;
		}
		return false;
	}

	public void addFirst(T item) {
		Node<T> adding = new Node<>(item, sentinel, sentinel.next);
		sentinel.next = adding;
		adding.next.prev = adding;
		size += 1;
	}

	public void addLast(T item) {
		Node<T> adding = new Node<>(item, sentinel.prev, sentinel);
		sentinel.prev = adding;
		adding.prev.next = adding;
		size += 1;
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public void printDeque() {
		Node<T> curr = sentinel.next;
		for (int i = 0; i < size; ++i) {
			System.out.print(curr.value + " ");
			curr = curr.next;
		}
	}

	private Node<T> getNode(int index) {
		if (isNotValidIndex(index)) return null;
		Node<T> curr = sentinel;
		for (int i = 0; i <= index; i += 1) {
			curr = curr.next;
		}
		return curr;
	}

	public T get(int index) {
		Node<T> temp = getNode(index);
		return temp == null ? null : temp.value;
	}
	public T getRecursive(int index){
		class getRecursiveHelper{
			public T helper(int index,Node<T> curr){
				if (index==0) return curr.value;
				return helper(index-1,curr.next);
			}
		}
		if (isNotValidIndex(index))return null;
		return (new getRecursiveHelper()).helper(index,sentinel.next);
	}

	//	private Node<T> remove(int index) {
//		if (isNotValidIndex(index)) return null;
//		Node<T> removing = getNode(index);
//		assert removing != null;
//		removing.prev.next = removing.next;
//		removing.next.prev = removing.prev;
//		size -= 1;
//		return removing;
//	}
	public T removeFirst() {
		if(isEmpty())return null;
		Node<T> removing = sentinel.next;
		sentinel.next = removing.next;
		removing.next.prev = sentinel;
		size -= 1;
		return removing.value;
	}

	public T removeLast() {
		if(isEmpty())return null;
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
			T item =curr.value;
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
		if (!(o instanceof LinkedListDeque)) return false;
		LinkedListDeque<?> other = (LinkedListDeque<?>) o;
		if (other.size() != this.size()) return false;
		Iterator<T> a = this.iterator();
		Iterator<?> b = other.iterator();
		while (a.hasNext()) {
			if (!Objects.equals(a.next(), b.next())) return false;
		}

		return true;
	}
}
