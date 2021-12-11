import java.util.concurrent.Semaphore;
import java.util.Random;

public class RaceCondition
{

	static final int BUFFER_SIZE = 10;
	static final int LOOP = 20;
	static int [] buffer = new int[BUFFER_SIZE];
	static int limit = LOOP;
	static int nextIn = 0;
	static int nextOut = 0;
	static Semaphore emptyBuffer = new Semaphore(buffer.length);
	static Semaphore usedBuffer = new Semaphore(0);
	static boolean finished = false;
	static Random rNum = new Random();
	
	
	public static void main(String[] args) throws InterruptedException
	{
		Thread t1 = new Thread(() -> {
				try
				{
					producerThread();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			});
			
		Thread t2 = new Thread(() -> {
				try
				{
					consumerThread();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			});
		
		t1.start();
		t2.start();
		t1.join();
		t2.join();
	}
	
	public static void producerThread() throws InterruptedException
	{
		while(!finished)
		{
			int k1 = rNum.nextInt(BUFFER_SIZE/2) + 1;
			for(int i = 0; i < k1 - 1; i++)
			{
				if(emptyBuffer.availablePermits() > 0)
				{
					emptyBuffer.acquire();
					buffer[i] = 1;
					usedBuffer.release();
				} else
				{
					break;
				}
			}
			
			nextIn = (nextIn + k1) % buffer.length;
			System.out.println("Producer is ok: " + nextIn);
			limit--;
			
			if(limit <= 0)
			{
				System.out.println("Producer exits with no issues");
				finished = true;
			}
			
			Thread.sleep((int) (Math.random() * 900 + 100));
			
		}
	}
	
	public static void consumerThread() throws InterruptedException
	{
		while(!finished)
		{
			Thread.sleep((int) (Math.random() * 900 + 100));
			int k2 = rNum.nextInt(BUFFER_SIZE/2) + 1;
			int data;
			
			for(int i = 0; i < k2 - 1; i++)
			{
				usedBuffer.acquire();
				data = buffer[i];
				
				if(data != 1)
				{
					System.out.println("No value in buffer");
					usedBuffer.release();
				}
			}
			
			nextOut = (nextOut + k2) % buffer.length;
			System.out.println("Consumer is ok: " + nextOut);
			limit--;
			
			if(limit <= 0)
			{
				System.out.println("Consumer exits with no issues");
				finished = true;
			}
			Thread.sleep((int) (Math.random() * 900 + 100));
		}
	}

}
