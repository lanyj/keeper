package cn.lanyj.keeper.services;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.stereotype.Service;

@Service
public class SchedulerService {
	
	private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
	
	public SchedulerService() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				scheduledExecutorService.shutdownNow();
			}
		}));
	}
	
	public ScheduledExecutorService getScheduledExecutorService() {
		return scheduledExecutorService;
	}
	
}
