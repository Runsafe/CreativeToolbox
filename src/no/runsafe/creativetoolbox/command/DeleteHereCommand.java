package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.database.PlotEntranceRepository;
import no.runsafe.framework.command.player.PlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.HashMap;
import java.util.List;

public class DeleteHereCommand extends PlayerCommand
{
	public DeleteHereCommand(PlotFilter filter, WorldGuardInterface worldGuard, PlotEntranceRepository entranceRepository)
	{
		super("deletehere", "delete the region you are in.", "runsafe.creative.delete");
		this.filter = filter;
		this.worldGuard = worldGuard;
		this.entrances = entranceRepository;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters, String[] arguments)
	{
		List<String> delete = filter.apply(worldGuard.getRegionsAtLocation(executor.getLocation()));
		if (delete == null || delete.size() == 0)
			return "No regions to delete!";
		StringBuilder results = new StringBuilder();
		for (String region : delete)
		{
			worldGuard.deleteRegion(filter.getWorld(), region);
			entrances.delete(region);
			results.append(String.format("Deleted region '%s'.", region));
		}
		return results.toString();
	}

	private final PlotFilter filter;
	private final WorldGuardInterface worldGuard;
	private final PlotEntranceRepository entrances;
}
