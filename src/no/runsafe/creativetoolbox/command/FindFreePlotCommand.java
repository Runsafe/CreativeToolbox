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
	public String getDescription()
	{
		return "teleport to a random empty plot.";
	}

	@Override
	public String requiredPermission()
	{
		return "runsafe.creative.teleport.free";
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args)
	{
		warpTo.remove(executor.getName());
		RunsafeLocation target;
		do
		{
			target = getCandidate();
		}
		while (!manager.verifyFreePlot(target));

		if (target == null)
			return "Sorry, no free plots could be located.";
		warpTo.put(executor.getName(), target);
		return null;
	}

	@Override
	public void OnCommandCompletion(RunsafePlayer player, String message)
	{
		if (warpTo.get(player.getName()) != null)
			player.teleport(warpTo.get(player.getName()));
		super.OnCommandCompletion(player, message);
	}

	private RunsafeLocation getCandidate()
	{
		List<RunsafeLocation> options = manager.getFreePlotEntrances();
		if (options == null || options.size() < 1)
			return null;
		if (options.size() == 1)
			return options.get(0);
		else
			return options.get(rng.nextInt(options.size() - 1));
	}

	private final PlotManager manager;
	private final HashMap<String, RunsafeLocation> warpTo = new HashMap<String, RunsafeLocation>();
	private final Random rng = new Random();
}
