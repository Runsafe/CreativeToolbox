package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlayerTeleport;
import no.runsafe.creativetoolbox.PlotList;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerAsyncCallbackCommand;
import no.runsafe.framework.api.player.IPlayer;

public class NextCommand extends PlayerAsyncCallbackCommand<PlayerTeleport>
{
	public NextCommand(IScheduler scheduler, PlotList plotList, PlotManager manager)
	{
		super("next", "Teleport to the next plot in your current list", "runsafe.creative.teleport.list", scheduler);
		this.plotList = plotList;
		this.manager = manager;
	}

	@Override
	public PlayerTeleport OnAsyncExecute(IPlayer executor, IArgumentList parameters)
	{
		PlayerTeleport target = new PlayerTeleport();
		target.who = executor;
		String plot = plotList.next(executor);
		if (plot == null)
			target.message = "&cYou do not have a list of plots.";
		else
		{
			target.location = manager.getPlotEntrance(plot);
			if (target.location == null)
				target.message = String.format("&cPlot '%s' not found.", plot);
			else
				target.message = String.format("&aTeleported to plot %d/%d: '%s'", plotList.current(executor), plotList.count(executor), plot);
		}
		return target;
	}

	@Override
	public void SyncPostExecute(PlayerTeleport result)
	{
		if (result.location != null)
		{
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
		}
		result.who.sendColouredMessage(result.message);
	}

	private final PlotList plotList;
	private final PlotManager manager;
}
