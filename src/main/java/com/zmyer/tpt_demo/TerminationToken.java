package com.zmyer.tpt_demo;

import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TerminationToken
{
	protected volatile boolean toShutdown = false;
	
	public final AtomicInteger reservations = new AtomicInteger();
	
	 private final Queue<WeakReference<Terminatable>> coordinateThreads;
	 
	 public TerminationToken()
	 {
		 coordinateThreads = new ConcurrentLinkedQueue<WeakReference<Terminatable>>();
	 }
	 
	 public boolean isToShutdown()
	 {
		 return toShutdown;
	 }
	 
	 protected void setToShutdown(boolean toShutdown)
	 {
		 this.toShutdown = toShutdown;
	 }
	 
	 protected void register(Terminatable thread)
	 {
		 coordinateThreads.add(new WeakReference<Terminatable>(thread));
	 }
	 
	 protected void notifyThreadTermination(Terminatable thread)
	 {
		 WeakReference<Terminatable> wrThread;
		 
		 Terminatable otherThread;
		 
		 while(null != (wrThread = coordinateThreads.poll()))
		 {
			 otherThread = wrThread.get();
			 if(null != otherThread && otherThread != thread)
			 {
				 otherThread.terminate();
			 }
		 }
	 }
}
