package no.runsafe.creativetoolbox.command.OldPlots;

import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.command.RunsafeAsyncPlayerCommand;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.timer.IScheduler;

import java.util.HashMap;
import java.util.Map;

public class PreviousCommand extends RunsafeAsyncPlayerCommand
{
	public PreviousCommand(IScheduler scheduler, PlotManager manager)
	{
		super("previous", scheduler);
		this.manager = manager;
	}

	@Override
	public String getDescription()
	{
		return "Teleport to the previous plot.";
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
		String current = manager.getOldPlotPointer(player);
		if (current == null)
			return "There was no previous plot.";
		String prev = null;
		boolean found = false;
		int n = 0;
		for (String plot : plots.keySet())
		{
			if (plot.equals(current))
			{
				found = true;
				break;
			}
			prev = plot;
			n++;
		}
		if(found && prev == null)
			return "Already at the first plot!";
		if (prev != null && found)
		{
			manager.setOldPlotPointer(player, prev);
			warpTo.put(player.getName(), manager.getPlotEntrance(prev));
			return String.format("[%d/%d] Previous plot is %s, reason: %s", n, plots.size(), prev, plots.get(prev));
		}
		return "Unable to find current plot";
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
