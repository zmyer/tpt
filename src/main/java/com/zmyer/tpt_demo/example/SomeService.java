package com.zmyer.tpt_demo.example;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.zmyer.tpt_demo.AbstractTerminatableThread;

public class SomeService
{
	private final BlockingQueue<String> queue = new ArrayBlockingQueue<String>(100);
	private final Producer producer = new Producer();
	private final Consumer consumer = new Consumer();
	
	private class Producer extends AbstractTerminatableThread
	{
		private int i = 0;
		@Override
		protected void doRun() throws Exception
		{
			// TODO Auto-generated method stub
			queue.put(String.valueOf(i++));
			consumer.terminationToken.reservations.incrementAndGet();
		}
	};
	
	private class Consumer extends AbstractTerminatableThread
	{

		@Override
		protected void doRun() throws Exception
		{
			// TODO Auto-generated method stub
			String product = queue.take();
			System.out.println("Processintg product: " + product);
			
			try
			{
				Thread.sleep(new Random().nextInt(10));
			}
			catch(InterruptedException e)
			{
				;
			}
			finally
			{
				terminationToken.reservations.decrementAndGet();
			}
		}
		
	};
	
	public void shutdown()
	{
		producer.terminate(true);
		consumer.terminate();
	}
	
	public void init()
	{
		producer.start();
		consumer.start();
	}
	
	public static void main(String[] args) throws Exception
	{
		SomeService ss = new SomeService();
		ss.init();
		Thread.sleep(500);
		ss.shutdown();
	}
}
