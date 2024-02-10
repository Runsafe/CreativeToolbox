package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlayerTeleport;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerAsyncCallbackCommand;
import no.runsafe.framework.api.player.IPlayer;

public class TeleportCommand extends PlayerAsyncCallbackCommand<PlayerTeleport>
{
	public TeleportCommand(IScheduler scheduler, PlotManager manager, OptionalPlotArgument plotName)
	{
		super("teleport", "teleport to a plot.", "runsafe.creative.teleport.plot", scheduler, plotName);
		this.manager = manager;
	}

	@Override
	public PlayerTeleport OnAsyncExecute(IPlayer executor, IArgumentList parameters)
	{
		PlayerTeleport target = new PlayerTeleport();
		target.who = executor;
		String plot = parameters.getValue("plotname");
		if (plot == null)
		{
			plot = manager.getLatestPlot(executor);
			if (plot == null)
			{
				target.message = "&cYou do not appear to own any plots.";
				return target;
			}
		}
		target.location = manager.getPlotEntrance(plot);
		if (target.location == null)
		{
			target.location = manager.getPlotEntrance(String.format("%s_%s", executor.getName(), plot));
			if (target.location != null)
				plot = String.format("%s_%s", executor.getName(), plot);
		}
		if (target.location == null)
			target.message = String.format("&cPlot '%s' not found.", plot);
		else
			target.message = String.format("&aTeleported to '%s'", plot);

		return target;
	}

	@Override
	public void SyncPostExecute(PlayerTeleport result)
	{
		if (result.location == null)
		{
			result.who.sendColouredMessage(result.message);
			return;
		}

		ILocation target = result.location;
		IWorld world = result.location.getWorld();
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
		result.who.sendColouredMessage(result.message);
	}

	private final PlotManager manager;
}