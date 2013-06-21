package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotCalculator;
import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.event.SyncInteractEvents;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
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
		IScheduler scheduler)
	{
		super("regenerate", "Regenerates the plot you are currently in.", "runsafe.creative.regenerate", scheduler);
		this.worldGuard = worldGuard;
		this.filter = filter;
		plotCalculator = calculator;
		this.interactEvents = interactEvents;
	}

	@Override
	public String OnAsyncExecute(RunsafePlayer executor, HashMap<String, String> parameters, String[] arguments)
	{
		List<String> candidate = filter.apply(worldGuard.getRegionsAtLocation(executor.getLocation()));
		Rectangle2D area;
		if (candidate != null && candidate.size() == 1)
			area = worldGuard.getRectangle(executor.getWorld(), candidate.get(0));
		else
			area = plotCalculator.getPlotArea(executor.getLocation(), false);

		if (arguments != null && arguments.length > 0)
		{
			PlotChunkGenerator.Mode mode = getMode(arguments[0]);
			if (mode == null)
				return String.format("Unknown generator, %s!", arguments[0]);
			interactEvents.startRegeneration(executor, area, mode);
		}
		else
			interactEvents.startRegeneration(executor, plotCalculator.pad(area), null);

		return "Right click the ground to confirm regeneration.";
	}

	@Override
	public String OnAsyncExecute(RunsafePlayer player, HashMap<String, String> parameters)
	{
		return null;
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
}
