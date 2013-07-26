package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlayerTeleport;
import no.runsafe.creativetoolbox.PlotList;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.player.PlayerAsyncCallbackCommand;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.RunsafeWorld;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.Map;

public class PreviousCommand extends PlayerAsyncCallbackCommand<PlayerTeleport>
{
	public PreviousCommand(IScheduler scheduler, PlotList plotList, PlotManager manager)
	{
		super("previous", "Teleport to the previous plot in your current list", "runsafe.creative.teleport.list", scheduler);
		this.plotList = plotList;
		this.manager = manager;
	}

	@Override
	public PlayerTeleport OnAsyncExecute(RunsafePlayer executor, Map<String, String> parameters)
	{
		PlayerTeleport target = new PlayerTeleport();
		target.who = executor;
		String plot = plotList.previous(executor);
		if (plot == null)
			target.message = "You do not have a list of plots.";
		else
		{
			target.location = manager.getPlotEntrance(plot);
			if (target.location == null)
				target.message = String.format("Plot '%s' not found.", plot);
			else
				target.message = String.format("Teleported to '%s'", plot);
		}
		return target;
	}

	@Override
	public void SyncPostExecute(PlayerTeleport result)
	{
		if (result.location != null)
		{
			RunsafeLocation target = result.location;
			RunsafeWorld world = result.location.getWorld();
			int air = 0;
			int y = target.getBlockY();
			for (; y < 256; ++y)
			{
				if (world.getBlockAt(target.getBlockX(), y, target.getBlockZ()).isAir())
					air++;
				if (air > 1)
					break;
			}
			target.setY(y - 1);
			result.who.teleport(result.location);
		}
		result.who.sendColouredMessage("Teleported to %s, plot %d/%d", result.message, plotList.current(result.who), plotList.count(result.who));
	}

	private final PlotList plotList;
	private final PlotManager manager;
}
