package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotCalculator;
import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotApproval;
import no.runsafe.creativetoolbox.event.SyncInteractEvents;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldgenerator.PlotChunkGenerator;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;

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
		super("regenerate", "Regenerates the plot you are currently in.", "runsafe.creative.regenerate", scheduler);
		this.worldGuard = worldGuard;
		this.filter = filter;
		plotCalculator = calculator;
		this.interactEvents = interactEvents;
		this.approvedPlotRepository = approvedPlotRepository;
	}

	@Override
	public String OnAsyncExecute(RunsafePlayer executor, HashMap<String, String> parameters, String[] arguments)
	{
		Rectangle2D area = getArea(executor.getLocation());

		List<String> candidate = filter.apply(worldGuard.getRegionsAtLocation(executor.getLocation()));
		if (candidate != null && !candidate.isEmpty())
			for (String plot : candidate)
			{
				PlotApproval approval = approvedPlotRepository.get(plot);
				if (approval != null && approval.getApproved() != null)
					return "You may not regenerate an approved plot!";
			}

		if (arguments != null && arguments.length > 0)
		{
			PlotChunkGenerator.Mode mode = getMode(arguments[0]);
			PlotChunkGenerator.Biome biome = arguments.length > 1 ? getBiome(arguments[1]) : null;
			if (mode == null)
				return String.format("Unknown generator, %s!", arguments[0]);
			interactEvents.startRegeneration(executor, area, mode, biome);
		}
		else
			interactEvents.startRegeneration(executor, plotCalculator.pad(area), null, null);

		return "Right click the ground to confirm regeneration.";
	}

	@Override
	public String OnAsyncExecute(RunsafePlayer player, HashMap<String, String> parameters)
	{
		return null;
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
		if (value.equalsIgnoreCase("default"))
			return PlotChunkGenerator.Mode.DEFAULT;
		return null;
	}

	private PlotChunkGenerator.Biome getBiome(String value)
	{
		for(PlotChunkGenerator.Biome biome : PlotChunkGenerator.Biome.values())
		{
			if(biome.name().toLowerCase().startsWith(value.toLowerCase()))
				return biome;
		}
		return null;
	}

	private final WorldGuardInterface worldGuard;
	private final PlotCalculator plotCalculator;
	private final PlotFilter filter;
	private final SyncInteractEvents interactEvents;
	private final ApprovedPlotRepository approvedPlotRepository;
}
