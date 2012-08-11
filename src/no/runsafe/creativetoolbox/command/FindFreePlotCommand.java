package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.command.RunsafeAsyncPlayerCommand;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.timer.IScheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class FindFreePlotCommand extends RunsafeAsyncPlayerCommand
{
	public FindFreePlotCommand(
		PlotManager manager,
		IScheduler scheduler
	)
	{
		super("findfreeplot", scheduler);
		this.manager = manager;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args)
	{
		warpTo.remove(executor.getName());
		List<RunsafeLocation> options = manager.getFreePlotEntrances();
		Console.fine(String.format("Found %d plots to choose from", options == null ? 0 : options.size()));
		if (options == null || options.size() < 1)
			return "Sorry, no free plots could be located.";
		for (RunsafeLocation loc : options)
			Console.finer(String.format("[%.1f,%.1f,%.1f]", loc.getX(), loc.getY(), loc.getZ()));
		if (options.size() == 1)
			warpTo.put(executor.getName(), options.get(0));
		else
			warpTo.put(executor.getName(), options.get(rng.nextInt(options.size() - 1)));
		return null;
	}

	@Override
	public void OnCommandCompletion(RunsafePlayer player, String message)
	{
		if (warpTo.get(player.getName()) != null)
			player.teleport(warpTo.get(player.getName()));
		super.OnCommandCompletion(player, message);
	}

	private PlotManager manager;
	private HashMap<String, RunsafeLocation> warpTo = new HashMap<String, RunsafeLocation>();
	private Random rng = new Random();
}
