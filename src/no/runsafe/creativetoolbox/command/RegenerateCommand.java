package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotCalculator;
import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotApproval;
import no.runsafe.creativetoolbox.event.SyncInteractEvents;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.ITabComplete;
import no.runsafe.framework.api.command.argument.OptionalArgument;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldgenerator.PlotChunkGenerator;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegenerateCommand extends PlayerAsyncCommand
{
	public RegenerateCommand(
		WorldGuardInterface worldGuard,
		PlotCalculator calculator,
		PlotFilter filter,
		SyncInteractEvents interactEvents,
		IScheduler scheduler,
		ApprovedPlotRepository approvedPlotRepository)
	{
		super("regenerate", "Regenerates the plot you are currently in.", "runsafe.creative.regenerate", scheduler, new GeneratorMode());
		this.worldGuard = worldGuard;
		this.filter = filter;
		plotCalculator = calculator;
		this.interactEvents = interactEvents;
		this.approvedPlotRepository = approvedPlotRepository;
	}

	public static class GeneratorMode extends OptionalArgument implements ITabComplete
	{
		public GeneratorMode()
		{
			super("mode");
			for (PlotChunkGenerator.Mode mode : PlotChunkGenerator.Mode.values())
				modes.add(mode.name().toLowerCase());
		}

		@Override
		public List<String> getAlternatives(RunsafePlayer player, String arg)
		{
			return modes;
		}

		private final List<String> modes = new ArrayList<String>();
	}

	@Override
	public String OnAsyncExecute(RunsafePlayer executor, Map<String, String> parameters)
	{
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

	private Rectangle2D getArea(RunsafeLocation location)
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

	private final WorldGuardInterface worldGuard;
	private final PlotCalculator plotCalculator;
	private final PlotFilter filter;
	private final SyncInteractEvents interactEvents;
	private final ApprovedPlotRepository approvedPlotRepository;
}
