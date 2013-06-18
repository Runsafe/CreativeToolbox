package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotCalculator;
import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.PlotEntranceRepository;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldeditbridge.WorldEditInterface;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;

public class DeleteHereCommand extends PlayerCommand
{
	public DeleteHereCommand(PlotFilter filter, WorldGuardInterface worldGuard, WorldEditInterface worldEdit, PlotEntranceRepository entranceRepository, PlotCalculator plotCalculator, PlotManager manager)
	{
		super("deletehere", "delete the region you are in.", "runsafe.creative.delete");
		this.filter = filter;
		this.worldGuard = worldGuard;
		this.worldEdit = worldEdit;
		this.entrances = entranceRepository;
		this.plotCalculator = plotCalculator;
		this.manager = manager;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		List<String> delete = filter.apply(worldGuard.getRegionsAtLocation(executor.getLocation()));
		if (delete == null || delete.size() == 0)
			return "No regions to delete!";
		StringBuilder results = new StringBuilder();
		for (String region : delete)
		{
			manager.delete(region);
			Rectangle2D area = plotCalculator.pad(worldGuard.getRectangle(executor.getWorld(), region));
			RunsafeLocation minPos = plotCalculator.getMinPosition(executor.getWorld(), area);
			RunsafeLocation maxPos = plotCalculator.getMaxPosition(executor.getWorld(), area);
			worldEdit.regenerate(executor, minPos, maxPos);
			results.append(String.format("Deleted region '%s'.", region));
		}
		return results.toString();
	}

	private final PlotFilter filter;
	private final WorldGuardInterface worldGuard;
	private final WorldEditInterface worldEdit;
	private final PlotEntranceRepository entrances;
	private final PlotCalculator plotCalculator;
	private final PlotManager manager;
}
