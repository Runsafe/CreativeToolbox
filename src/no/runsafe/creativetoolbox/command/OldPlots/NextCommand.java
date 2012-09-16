package no.runsafe.creativetoolbox.command.OldPlots;

import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.command.RunsafeAsyncPlayerCommand;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.timer.IScheduler;

import java.util.HashMap;
import java.util.Map;

public class NextCommand extends RunsafeAsyncPlayerCommand
{
	public NextCommand(IScheduler scheduler, PlotManager manager)
	{
		super("next", scheduler);
		this.manager = manager;
	}

	@Override
	public String getDescription()
	{
		return "Teleport to the next plot.";
	}

	@Override
	public String requiredPermission()
	{
		return "runsafe.creative.scan.old-plots";
	}

	@Override
	public String OnExecute(RunsafePlayer player, String[] strings)
	{
		warpTo.remove(player.getName());
		Map<String, String> plots = manager.getOldPlotWorklist(player);
		if(plots == null || plots.size() == 0)
			return "No old plots found!";

		String current = manager.getOldPlotPointer(player);
		String next = null;
		int n = 1;
		boolean found = false;
		for (String plot : plots.keySet())
		{
			if (found || current == null)
			{
				next = plot;
				break;
			}
			if (plot.equals(current))
				found = true;
			n++;
		}
		if (found && next == null)
			return "Already at the last plot!";
		manager.setOldPlotPointer(player, next);
		warpTo.put(player.getName(), manager.getPlotEntrance(next));
		return String.format("[%d/%d] Next plot is %s, reason: %s", n, plots.size(), next, plots.get(next));
	}

	@Override
	public void OnCommandCompletion(RunsafePlayer player, String message)
	{
		if (warpTo.get(player.getName()) != null)
			player.teleport(warpTo.get(player.getName()));
		super.OnCommandCompletion(player, message);
	}

	private final HashMap<String, RunsafeLocation> warpTo = new HashMap<String, RunsafeLocation>();
	private final PlotManager manager;
}
