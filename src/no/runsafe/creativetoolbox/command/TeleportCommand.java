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
		PlotEntranceRepository entranceRepository,
		PlotFilter filter,
		IScheduler scheduler,
		PlotManager manager
	)
	{
		super("teleport", scheduler, "plotname");
		this.manager = manager;
	}

	@Override
	public String requiredPermission()
	{
		return "runsafe.creative.teleport";
	}

	@Override
	public void OnCommandCompletion(RunsafePlayer player, String message)
	{
		if (warpTo.containsKey(player.getName()))
			player.teleport(warpTo.get(player.getName()));
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

	PlotManager manager;
	HashMap<String, RunsafeLocation> warpTo = new HashMap<String, RunsafeLocation>();
}
