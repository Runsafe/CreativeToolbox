package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotCalculator;
import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotApproval;
import no.runsafe.creativetoolbox.event.SyncInteractEvents;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.worldguardbridge.IRegionControl;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteHereCommand extends PlayerAsyncCommand
{
	public DeleteHereCommand(
		PlotManager manager, PlotFilter filter,
		IRegionControl worldGuard,
		PlotCalculator plotCalculator,
		SyncInteractEvents interactEvents,
		IScheduler scheduler, ApprovedPlotRepository approvedPlotRepository)
	{
		super("deletehere", "delete the region you are in.", "runsafe.creative.delete", scheduler);
		this.manager = manager;
		this.filter = filter;
		this.worldGuard = worldGuard;
		this.plotCalculator = plotCalculator;
		this.interactEvents = interactEvents;
		this.approvedPlotRepository = approvedPlotRepository;
	}

	@Override
	public String OnAsyncExecute(IPlayer executor, IArgumentList parameters)
	{
		if (manager.isInWrongWorld(executor))
			return "&cYou cannot use that here.";
		List<String> delete = filter.apply(worldGuard.getRegionsAtLocation(executor.getLocation()));
		if (delete == null || delete.isEmpty())
			return "&cNo regions to delete!";
		Map<String, Rectangle2D> regions = new HashMap<>();
		for (String region : delete)
		{
			PlotApproval approval = approvedPlotRepository.get(region);
			if (approval != null && approval.getApproved() != null)
				return "&cYou may not delete an approved plot!";

			Rectangle2D area = plotCalculator.pad(worldGuard.getRectangle(executor.getWorld(), region));
			regions.put(region, area);
		}
		interactEvents.startDeletion(executor, regions);
		return String.format("Right click ground to confirm deletion of %d region%s.", regions.size(), regions.size() > 1 ? "s" : "");
	}

	private final PlotManager manager;
	private final PlotFilter filter;
	private final IRegionControl worldGuard;
	private final PlotCalculator plotCalculator;
	private final SyncInteractEvents interactEvents;
	private final ApprovedPlotRepository approvedPlotRepository;
}
