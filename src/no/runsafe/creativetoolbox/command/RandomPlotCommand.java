package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.player.PlayerAsyncCallbackCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class RandomPlotCommand extends PlayerAsyncCallbackCommand<RandomPlotCommand.Sudo>
{
	public RandomPlotCommand(PlotFilter filter, IScheduler scheduler)
	{
		super("randomplot", "teleport to a random plot.", "runsafe.creative.teleport.random", scheduler);
		plotFilter = filter;
		rng = new Random();
	}

	@Override
	public Sudo OnAsyncExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		if (plotFilter.getWorld() == null)
			return null;
		List<String> plots = plotFilter.getFiltered();
		int r = rng.nextInt(plots.size());
		Sudo target = new Sudo();
		target.player = executor;
		target.command = String.format("creativetoolbox teleport %s", plots.get(r));
		return target;
	}

	@Override
	public void SyncPostExecute(Sudo result)
	{
		if (result != null)
			result.player.getRawPlayer().performCommand(result.command);
	}

	class Sudo
	{
		public RunsafePlayer player;
		public String command;
	}

	private final PlotFilter plotFilter;
	private final Random rng;
}
