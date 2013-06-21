package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotCalculator;
import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.event.SyncInteractEvents;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteHereCommand extends PlayerAsyncCommand
{
	public DeleteHereCommand(
		PlotFilter filter,
		WorldGuardInterface worldGuard,
		PlotCalculator plotCalculator,
		SyncInteractEvents interactEvents,
		IScheduler scheduler)
	{
		super("deletehere", "delete the region you are in.", "runsafe.creative.delete", scheduler);
		this.filter = filter;
		this.worldGuard = worldGuard;
		this.plotCalculator = plotCalculator;
		this.interactEvents = interactEvents;
	}

	@Override
	public String OnAsyncExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		List<String> delete = filter.apply(worldGuard.getRegionsAtLocation(executor.getLocation()));
		if (delete == null || delete.size() == 0)
			return "No regions to delete!";
		Map<String, Rectangle2D> regions = new HashMap<String, Rectangle2D>();
		for (String region : delete)
		{
			Rectangle2D area = plotCalculator.pad(worldGuard.getRectangle(executor.getWorld(), region));
			regions.put(region, area);
		}
		interactEvents.startDeletion(executor, regions);
		return String.format("Right click ground to confirm deletion of %d region%s.", regions.size(), regions.size() > 1 ? "s" : "");
	}

	private final PlotFilter filter;
	private final WorldGuardInterface worldGuard;
	private final PlotCalculator plotCalculator;
	private final SyncInteractEvents interactEvents;
}
