package com.zmyer.tpt_demo;

public abstract class AbstractTerminatableThread extends Thread implements Terminatable
{
	public final TerminationToken terminationToken;
	
	public AbstractTerminatableThread()
	{
		this(new TerminationToken());
	}
	
	public AbstractTerminatableThread(TerminationToken terminationToken)
	{
		super();
		this.terminationToken = terminationToken;
		terminationToken.register(this);
	}
	
	protected abstract void doRun() throws Exception;
	
	protected void doCleanup(Exception cause)
	{
		
	}
	
	protected void doTerminate()
	{
		
	}
	
	public void run()
	{
		Exception ex = null;
		
		try
		{
			for(;;)
			{
				if(terminationToken.isToShutdown() && terminationToken.reservations.get() <=0)
				{
					break;
				}
				
				doRun();
			}
		}
		catch(Exception e)
		{
			ex = e;
		}
		finally
		{
			try
			{
				doCleanup(ex);
			}
			finally
			{
				terminationToken.notifyThreadTermination(this);
			}
		}
	}
	
	public void interrupt()
	{
		terminate();
	}
	
	public void terminate()
	{
		terminationToken.setToShutdown(true);
		try
		{
			doTerminate();
		}
		finally
		{
			if(terminationToken.reservations.get() <=0)
			{
				super.interrupt();
			}
		}
	}
	
	public void terminate(boolean waitUtilThreadTerminated)
	{
		terminate();
		if(waitUtilThreadTerminated)
		{
			try
			{
				this.join();
			}
			catch(InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		}
	}
	
}
