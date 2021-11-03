package org.andreidodu.tbot.workers;

public interface BotWorker {

	public String getType();

	public Runnable getRunnable(String configPrefix);
}
