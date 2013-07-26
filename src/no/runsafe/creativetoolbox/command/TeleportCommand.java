package no.runsafe.creativetoolbox.command;

import com.google.common.collect.Lists;
import no.runsafe.creativetoolbox.PlayerTeleport;
import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.player.PlayerAsyncCallbackCommand;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.RunsafeWorld;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class TeleportCommand extends PlayerAsyncCallbackCommand<PlayerTeleport>
{
	public TeleportCommand(IScheduler scheduler, PlotManager manager, PlotFilter filter, WorldGuardInterface worldGuard)
	{
		super("teleport", "teleport to a plot.", "runsafe.creative.teleport.plot", scheduler, "plotname");
		this.manager = manager;
		this.filter = filter;
		this.worldGuard = worldGuard;
	}

	@Override
	public PlayerTeleport OnAsyncExecute(RunsafePlayer executor, Map<String, String> parameters)
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

	@Nullable
	@Override
	public List<String> getParameterOptionsPartial(String parameter, String arg)
	{
		console.fine("Tab completion: %s=%s", parameter, arg);
		if (!arg.contains("_"))
			return Lists.newArrayList();
		console.fine("Doing tab completion");
		RunsafePlayer player = RunsafeServer.Instance.getOfflinePlayerExact(arg.substring(0, arg.lastIndexOf('_') - 1));
		console.fine("Found player %s using '%s'", player, arg.substring(0, arg.lastIndexOf('_') - 1));
		return filter.apply(worldGuard.getOwnedRegions(player, filter.getWorld()));
	}

	private final PlotManager manager;
	private final PlotFilter filter;
	private final WorldGuardInterface worldGuard;
}