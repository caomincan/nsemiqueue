

public class QueueTest {
	public static final String LQueue = "LQueue";
	public static final String SLQueue = "SLQueue";

	public static void main(String[] args) throws InterruptedException {

		String qname = "",num = "",duration = "",n = "";
		int threads_num= 0, nsemi = 0, timeout =0;
		if(args.length == 3 || args.length == 4){
			qname = args[0];
			num = args[1];
			duration = args[2];
			n = null;
			try{
				n = args[3];
			}catch(Exception e){}
			if( (qname.equals(LQueue) || qname.equals(SLQueue)) && num.matches("^[0-9]+$")
					&& duration.matches("^[0-9]+$")){
				threads_num = Integer.valueOf(num).intValue();
				timeout = Integer.valueOf(duration).intValue();
				if(n != null && n.matches("^[0-9]+$")){
					nsemi = Integer.valueOf(n).intValue();
				}
			}else{
				System.out.println("Usage: java QueueTest <qname> <threads> <duration> [<n>]");
			}
		}else{
			System.out.println("Usage: java QueueTest <qname> <threads> <duration> [<n>]");
		}
		// Initial objects
		myQueue<Integer> queue = null;
		if(qname.equals(LQueue)){
			queue = new LockFreeQueue<Integer>();
		}else if(qname.equals(SLQueue) && nsemi>0){
			queue = new SemiLockFreeQueue<Integer>(nsemi);
		}
		
		if(queue != null && threads_num > 0 && timeout > 0){
			TestThread[] threads = new TestThread[threads_num];
			// create threads
			for(int i=0;i<threads_num;i++){
	        	threads[i] = new TestThread(queue,timeout);
	        }
			// start threads
			for(int i=0;i<threads_num;i++){
				threads[i].start();
			}
			// join threads
			long enq_num = 0;
			long deq_num = 0;
			long left_node = 0;
			for(int i=0;i<threads_num;i++){
				threads[i].join();
				enq_num += threads[i].enq_num;
				deq_num += threads[i].deq_num;
			}
			left_node = queue.size();
			System.out.println("Enqueue: "+enq_num+" Dequeue: " + deq_num+" Size of queue: "+ left_node);
			double throughput = (double)timeout*1000000.0/(double)(enq_num+deq_num);
			System.out.println("Throughtput: "+throughput+" microsecond");
			
		}
        
	}

}
