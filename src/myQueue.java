

public interface myQueue<T> {
	public void enq(T item) ;
	public T deq() throws EmptyException;
	public int size();
}
