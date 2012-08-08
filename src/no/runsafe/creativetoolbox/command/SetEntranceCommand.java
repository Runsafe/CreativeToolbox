package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.database.PlotEntrance;
import no.runsafe.creativetoolbox.database.PlotEntranceRepository;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.command.RunsafePlayerCommand;
import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.List;

public class SetEntranceCommand extends RunsafePlayerCommand
{
	public SetEntranceCommand(PlotEntranceRepository repository, IConfiguration config, IOutput output, PlotFilter filter)
	{
		super("setentrance", null);
		this.repository = repository;
		this.config = config;
		this.console = output;
		this.plotFilter = filter;
	}

	@Override
	public boolean CanExecute(RunsafePlayer player, String[] args)
	{
		if (!worldGuard.serverHasWorldGuard())
			return true;

		String region = getCurrentRegion(player);
		console.fine(String.format("Player is in region %s", region));
		if (worldGuard.getOwners(player.getWorld(), region).contains(player.getName().toLowerCase()))
			return true;

		return player.hasPermission("runsafe.creative.entrance.set");
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args)
	{
		String currentRegion = getCurrentRegion(executor);
		if (currentRegion == null)
			return "Unable to set entrance here.";

		PlotEntrance entrance = new PlotEntrance();
		entrance.setName(currentRegion);
		entrance.setLocation(executor.getLocation());
		repository.persist(entrance);
		return String.format("Entrance for %s set.", currentRegion);
	}

	private String getCurrentRegion(RunsafePlayer player)
	{
		if (!worldGuard.serverHasWorldGuard())
			return null;

		List<String> regions = plotFilter.apply(worldGuard.getApplicableRegions(player));
		if(regions == null || regions.size() == 0)
			return null;
		return regions.get(0);
	}

	PlotEntranceRepository repository;
	IConfiguration config;
	IOutput console;
	PlotFilter plotFilter;
	WorldGuardInterface worldGuard;
}
