package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.PlotEntranceRepository;
import no.runsafe.framework.command.RunsafeAsyncPlayerCommand;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.timer.IScheduler;

import java.util.HashMap;

public class TeleportCommand extends RunsafeAsyncPlayerCommand
{

	public TeleportCommand(
		IScheduler scheduler,
		PlotManager manager
	)
	{
		super("teleport", scheduler, "plotname");
		this.manager = manager;
	}

	@Override
	public String getDescription()
	{
		return "teleport to a plot.";
	}

	@Override
	public String requiredPermission()
	{
		return "runsafe.creative.teleport.plot";
	}

	@Override
	public void OnCommandCompletion(RunsafePlayer player, String message)
	{
		if (warpTo.containsKey(player.getName()))
		{
			RunsafeLocation target = warpTo.get(player.getName());
			int air = 0;
			int y = target.getBlockY();
			for(; y < 256; ++y)
			{
				if(target.getWorld().getBlockAt(target.getBlockX(), y, target.getBlockZ()).isAir())
					air++;
				if(air > 1)
					break;
			}
			target.setY(y);
			player.teleport(target);
		}
		super.OnCommandCompletion(player, message);
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args)
	{
		warpTo.remove(executor.getName());
		String plot = getArg("plotname");
		RunsafeLocation target = manager.getPlotEntrance(plot);
		if (target == null)
			return String.format("Plot '%s' not found.", plot);

		warpTo.put(executor.getName(), target);

		return String.format("Teleported to '%s'", plot);
	}

	private final PlotManager manager;
	private final HashMap<String, RunsafeLocation> warpTo = new HashMap<String, RunsafeLocation>();
}
