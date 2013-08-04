package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.database.PlotEntrance;
import no.runsafe.creativetoolbox.database.PlotEntranceRepository;
import no.runsafe.framework.api.IOutput;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.List;
import java.util.Map;

public class SetEntranceCommand extends PlayerAsyncCommand
{
	public SetEntranceCommand(
		PlotEntranceRepository repository,
		IOutput output,
		PlotFilter filter,
		IScheduler scheduler,
		WorldGuardInterface worldGuard
	)
	{
		super("setentrance", "define where users teleport to in a plot.", null, scheduler);
		this.repository = repository;
		this.console = output;
		this.plotFilter = filter;
		this.worldGuard = worldGuard;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, Map<String, String> parameters)
	{
		String currentRegion = getCurrentRegion(executor);
		if (currentRegion == null)
			return "No plot at your current location.";

		console.fine(String.format("Player is in region %s", currentRegion));
		if (!(executor.hasPermission("runsafe.creative.entrance.set")
			|| worldGuard.getOwners(executor.getWorld(), currentRegion).contains(executor.getName().toLowerCase())))
			return String.format("You are not allowed to set the entrance for the region %s", currentRegion);

		return super.OnExecute(executor, parameters);
	}

	@Override
	public String OnAsyncExecute(RunsafePlayer executor, Map<String, String> parameters)
	{
		String currentRegion = getCurrentRegion(executor);
		if (currentRegion == null)
			return null;

		console.fine(String.format("Player is in region %s", currentRegion));
		if (!(executor.hasPermission("runsafe.creative.entrance.set")
			|| worldGuard.getOwners(executor.getWorld(), currentRegion).contains(executor.getName().toLowerCase())))
			return null;

		PlotEntrance entrance = new PlotEntrance();
		entrance.setName(currentRegion);
		entrance.setLocation(executor.getLocation());
		repository.persist(entrance);
		return String.format("Entrance for %s set.", currentRegion);
	}

	private String getCurrentRegion(RunsafePlayer player)
	{
		List<String> regions = plotFilter.apply(worldGuard.getRegionsAtLocation(player.getLocation()));
		if (regions == null || regions.size() == 0)
			return null;
		return regions.get(0);
	}

	private final PlotEntranceRepository repository;
	private final IOutput console;
	private final PlotFilter plotFilter;
	private final WorldGuardInterface worldGuard;
}
