package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.framework.command.RunsafeAsyncPlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.timer.IScheduler;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class RandomPlotCommand extends RunsafeAsyncPlayerCommand
{
	public RandomPlotCommand(
		PlotFilter filter,
		CreativeToolboxCommand ctCommand,
		IScheduler scheduler
	)
	{
		super("randomplot", scheduler);
		plotFilter = filter;
		rng = new Random();
		this.command = ctCommand;
	}

	@Override
	public String requiredPermission()
	{
		return "runsafe.creative.teleport.random";
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args)
	{
		warpTo.remove(executor.getName());
		if (plotFilter.getWorld() == null)
			return "No world..";
		List<String> plots = plotFilter.getFiltered();
		int r = rng.nextInt(plots.size());
		warpTo.put(executor.getName(), plots.get(r));
		return null;
	}

	@Override
	public void OnCommandCompletion(RunsafePlayer player, String message)
	{
		if (warpTo.get(player.getName()) != null)
			command.Execute(player, new String[]{"teleport", warpTo.get(player.getName())});
		super.OnCommandCompletion(player, message);
	}

	private final PlotFilter plotFilter;
	private final Random rng;
	private final CreativeToolboxCommand command;
	private final HashMap<String, String> warpTo = new HashMap<String, String>();
}
