package no.runsafe.creativetoolbox.command.OldPlots;

import no.runsafe.creativetoolbox.PlayerTeleport;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.player.PlayerAsyncCallbackCommand;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.HashMap;
import java.util.Map;

public class PreviousCommand extends PlayerAsyncCallbackCommand<PlayerTeleport>
{
	public PreviousCommand(IScheduler scheduler, PlotManager manager)
	{
		super("previous", "Teleport to the previous plot.", "runsafe.creative.scan.old-plots", scheduler);
		this.manager = manager;
	}

	@Override
	public PlayerTeleport OnAsyncExecute(RunsafePlayer executor, Map<String, String> parameters)
	{
		PlayerTeleport target = new PlayerTeleport();
		target.who = executor;
		Map<String, String> plots = manager.getOldPlotWorkList(executor);
		String current = manager.getOldPlotPointer(executor);
		if (current == null)
		{
			target.message = "There was no previous plot.";
			return target;
		}
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
		if (found && prev == null)
		{
			target.message = "Already at the first plot!";
			return target;
		}
		if (prev != null && found)
		{
			manager.setOldPlotPointer(executor, prev);
			target.location = manager.getPlotEntrance(prev);
			target.message = String.format("[%d/%d] Previous plot is %s, reason: %s", n, plots.size(), prev, plots.get(prev));
			return target;
		}
		target.message = "Unable to find current plot";
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
