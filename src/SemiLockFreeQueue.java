/*
 * LockFreeQueue.java
 *
 * Created on December 29, 2005, 2:05 PM
 *
 * The Art of Multiprocessor Programming, by Maurice Herlihy and Nir Shavit.
 * by Maurice Herlihy and Nir Shavit.
 * Copyright 20065 Elsevier Inc. All rights reserved.
 */



import java.util.Random;
import java.util.concurrent.atomic.*;

/**
 * Lock-free queue.
 * Based on Michael and Scott http://doi.acm.org/10.1145/248052.248106
 * @param T item type
 * @author Maurice Herlihy
 */
public class SemiLockFreeQueue<T> implements myQueue<T> {
  
  private AtomicReference<Node> head;
  private AtomicReference<Node> tail;
  private int n;
  
  private ThreadLocal<Random> rand;
  
  public SemiLockFreeQueue(int n) {
    Node sentinel = new Node(null);
    this.head = new AtomicReference<Node>(sentinel);
    this.tail = new AtomicReference<Node>(sentinel);
    this.n = n;
    this.rand = new ThreadLocal<Random>(){
    	@Override protected Random initialValue(){
    		return new Random();
    	}
    };
  }
  /**
   * Append item to end of queue.
   * @param item
   */
  public void enq(T item) {
	    if (item == null) throw new NullPointerException();
	    Node node = new Node(item); // allocate & initialize new node
	    while (true) {		 // keep trying
	      Node last = tail.get();    // read tail
	      Node next = last.next.get(); // read next
	      if (last == tail.get()) { // are they consistent?
	        if (next == null){
	        	if(last.next.compareAndSet(next, node)){
	        		tail.compareAndSet(last, node);
	        		return;
	        	}
	        } else {
	        	tail.compareAndSet(last, next);
	        }
	      }
	    }
	  }
  /**
   * Remove and return head of queue.
   * @return remove first item in queue
   * @throws queue.EmptyException
   */
  public T deq() throws EmptyException {
    while (true) {
      Node first = head.get();
      Node next = first.next.get();
      Node last = tail.get();
      if (first == head.get()) { // if it is head it should rehead
    	  if(first == last){
    		  if(next == null){
    			  throw new EmptyException();
    			  }
          	// tail is behind 
          	tail.compareAndSet(last, next);	
          	}
    	  else {
    		  int idx = rand.get().nextInt(n+1);
    		  if(idx == 0){
    			  // next is not null we need put head to next no marked node
    			  if(next.marked.get())
    				  head.compareAndSet(first, next);
    			  }else{
    				  Node tmp = first;
    				  for(int i=0;i<idx&&tmp!=null;i++ ){
    					  tmp = tmp.next.get();
    				  }
    				  T value = tmp.value; // read value before dequeuing
    				  // return only when logical dequeue happend
    				  if (tmp.marked.compareAndSet(false, true))
    					  return value;
    				  }
    		  }
    	  }
      }
    }

  public class Node {
    public volatile T value;
    public AtomicReference<Node> next;
    public AtomicBoolean marked;
    
    public Node(T value) {
      this.value = value;
      this.next  = new AtomicReference<Node>(null);
      this.marked = new AtomicBoolean(false);
    }
  }

@Override
public int size() {
	int num = 0;
	Node tmp = head.get();
	while(tmp != tail.get() && tmp.next.get() != null){
		if(!tmp.marked.get())num++;
		tmp = tmp.next.get();
	}
	return num;
}
}
