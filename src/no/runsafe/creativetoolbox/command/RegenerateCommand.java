package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotCalculator;
import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotApproval;
import no.runsafe.creativetoolbox.event.SyncInteractEvents;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.EnumArgument;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.worldgenerator.PlotChunkGenerator;
import no.runsafe.worldguardbridge.IRegionControl;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;

public class RegenerateCommand extends PlayerAsyncCommand
{
	public RegenerateCommand(
		IRegionControl worldGuard,
		PlotCalculator calculator,
		PlotFilter filter,
		SyncInteractEvents interactEvents,
		IScheduler scheduler,
		ApprovedPlotRepository approvedPlotRepository, PlotManager manager)
	{
		super(
			"regenerate", "Regenerates the plot you are currently in.", "runsafe.creative.regenerate", scheduler,
			new EnumArgument("mode", PlotChunkGenerator.Mode.values(), false)
		);
		this.worldGuard = worldGuard;
		this.filter = filter;
		plotCalculator = calculator;
		this.interactEvents = interactEvents;
		this.approvedPlotRepository = approvedPlotRepository;
		this.manager = manager;
	}

	@Override
	public String OnAsyncExecute(IPlayer executor, Map<String, String> parameters)
	{
		if (manager.isInWrongWorld(executor))
			return "You cannot use that here.";

		Rectangle2D area = getArea(executor.getLocation());
		if (area == null)
			return "No plot at this point.";

		List<String> candidate = filter.apply(worldGuard.getRegionsAtLocation(executor.getLocation()));
		if (candidate != null && !candidate.isEmpty())
			for (String plot : candidate)
			{
				PlotApproval approval = approvedPlotRepository.get(plot);
				if (approval != null && approval.getApproved() != null)
					return "You may not regenerate an approved plot!";
			}

		PlotChunkGenerator.Mode mode = parameters.containsKey("mode") ? getMode(parameters.get("mode")) : null;
		interactEvents.startRegeneration(executor, area, mode);

		return "Right click the ground to confirm regeneration.";
	}

	private Rectangle2D getArea(ILocation location)
	{
		List<String> candidate = filter.apply(worldGuard.getRegionsAtLocation(location));
		if (candidate != null && candidate.size() == 1)
			return worldGuard.getRectangle(location.getWorld(), candidate.get(0));
		else
			return plotCalculator.getPlotArea(location, false);
	}

	private PlotChunkGenerator.Mode getMode(String value)
	{
		if (value.equalsIgnoreCase("flat"))
			return PlotChunkGenerator.Mode.FLAT;
		if (value.equalsIgnoreCase("normal"))
			return PlotChunkGenerator.Mode.NORMAL;
		if (value.equalsIgnoreCase("void"))
			return PlotChunkGenerator.Mode.VOID;
		return null;
	}

	private final IRegionControl worldGuard;
	private final PlotCalculator plotCalculator;
	private final PlotFilter filter;
	private final SyncInteractEvents interactEvents;
	private final ApprovedPlotRepository approvedPlotRepository;
	private final PlotManager manager;
}
