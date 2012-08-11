package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.framework.command.RunsafePlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.List;

public class DeleteHereCommand extends RunsafePlayerCommand
{
	public DeleteHereCommand(PlotFilter filter, WorldGuardInterface worldGuard)
	{
		super("deletehere", null);
		this.filter = filter;
		this.worldGuard = worldGuard;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args)
	{
		List<String> delete = filter.apply(worldGuard.getRegionsAtLocation(executor.getLocation()));
		if (delete == null || delete.size() == 0)
			return "No regions to delete!";
		StringBuilder results = new StringBuilder();
		for (String region : delete)
		{
			worldGuard.deleteRegion(filter.getWorld(), region);
			results.append(String.format("Deleted region '%s'.", region));
		}
		return results.toString();
	}

	private PlotFilter filter;
	private WorldGuardInterface worldGuard;
}
