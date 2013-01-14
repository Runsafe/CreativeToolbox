package no.runsafe.creativetoolbox.command.OldPlots;

import no.runsafe.creativetoolbox.PlayerTeleport;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.command.player.PlayerAsyncCallbackCommand;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.timer.IScheduler;

import java.util.HashMap;
import java.util.Map;

public class NextCommand extends PlayerAsyncCallbackCommand<PlayerTeleport>
{
	public NextCommand(IScheduler scheduler, PlotManager manager)
	{
		super("next", "Teleport to the next plot.", "runsafe.creative.scan.old-plots", scheduler);
		this.manager = manager;
	}

	@Override
	public PlayerTeleport OnAsyncExecute(RunsafePlayer executor, HashMap<String, String> parameters, String[] arguments)
	{
		PlayerTeleport target = new PlayerTeleport();
		target.who = executor;
		Map<String, String> plots = manager.getOldPlotWorkList(executor);
		if (plots == null || plots.size() == 0)
		{
			target.message = "No old plots found!";
			return target;
		}

		String current = manager.getOldPlotPointer(executor);
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
		{
			target.message = "Already at the last plot!";
			return target;
		}
		manager.setOldPlotPointer(executor, next);
		target.location = manager.getPlotEntrance(next);
		target.message = String.format("[%d/%d] Next plot is %s, reason: %s", n, plots.size(), next, plots.get(next));
		return target;
	}

	@Override
	public void SyncPostExecute(PlayerTeleport result)
	{
		if (result.location != null)
			result.who.teleport(result.location);
		result.who.sendColouredMessage(result.message);
	}

	private final PlotManager manager;
}
