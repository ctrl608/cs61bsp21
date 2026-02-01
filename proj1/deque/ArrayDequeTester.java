package deque;

import org.junit.Test;

public class ArrayDequeTester {
	@Test
	public void test1(){
		ArrayDeque<Integer> test=new ArrayDeque<>(1,2,3,5);
		test.printDeque();
		test.addFirst(7);
		test.addFirst(7);
		test.addFirst(7);
		test.addFirst(7);
		test.addFirst(7);
		test.addFirst(7);
		test.addFirst(7);
		test.addFirst(7);
		test.addFirst(7);
		test.addFirst(7);
		test.addFirst(7);
		test.addFirst(7);
		test.addFirst(7);
		test.addFirst(7);
		test.addFirst(7);
		test.addFirst(7);
		test.addFirst(7);
		test.addFirst(7);
	}
	@Test
	public void test2(){
		ArrayDeque<Integer> test=new ArrayDeque<>(7,1,3,4);
		test.addLast(8);
		test.addLast(8);
		test.addLast(8);
		test.addLast(8);
		test.addLast(8);
		test.addLast(8);
		test.addLast(8);
		test.addLast(8);
		test.addLast(8);
		test.addLast(8);
		test.addLast(8);
		test.addLast(8);
		test.addLast(8);
		test.addLast(8);
		test.addLast(8);
		test.addLast(8);
		test.addLast(8);
		test.addLast(8);
	}
	public void test3(){
		ArrayDeque<Integer> test=new ArrayDeque<>(1,2,3,5);

	}
}
