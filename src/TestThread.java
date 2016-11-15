

public class TestThread extends Thread{
	private static int ID_GEN = 0;
	private myQueue<Integer> queue;
	private int id;
	private long timeout = 0;
	public long enq_num = 0;
	public long deq_num = 0;
	
	public TestThread(myQueue<Integer> queue,int timeout){
		this.queue = queue;
		this.id = ID_GEN++;
		this.timeout = timeout*1000000000;
	}
	
	@Override
	public void run(){
		for(int i=0;i<2000;i++){}
		int status = 0;
		long start = System.nanoTime();
		long duration = 0;
		while(duration < timeout){
			if(status == 0){
				queue.enq(id);
				status = 1;
				enq_num++;
			}else{
				try {
					int value = queue.deq().intValue();
					deq_num++;
				} catch (EmptyException e) {

				} catch(Exception e){
					
				}
				status = 0;
			}
			duration = System.nanoTime()-start;
		}
		for(int i=0;i<2000;i++){}
	}
}
