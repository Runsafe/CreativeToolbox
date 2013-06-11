package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlayerTeleport;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.player.PlayerAsyncCallbackCommand;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.RunsafeWorld;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.HashMap;

public class TeleportCommand extends PlayerAsyncCallbackCommand<PlayerTeleport>
{
	public TeleportCommand(IScheduler scheduler, PlotManager manager)
	{
		super("teleport", "teleport to a plot.", "runsafe.creative.teleport.plot", scheduler, "plotname");
		this.manager = manager;
	}

	@Override
	public PlayerTeleport OnAsyncExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		PlayerTeleport target = new PlayerTeleport();
		target.who = executor;
		String plot = parameters.get("plotname");
		target.location = manager.getPlotEntrance(plot);
		if (target.location == null)
		{
			target.location = manager.getPlotEntrance(String.format("%s_%s", executor.getName(), plot));
			if (target.location != null)
				plot = String.format("%s_%s", executor.getName(), plot);
		}
		if (target.location == null)
			target.message = String.format("Plot '%s' not found.", plot);
		else
			target.message = String.format("Teleported to '%s'", plot);

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
		result.who.sendColouredMessage(result.message);
	}

	private final PlotManager manager;
}