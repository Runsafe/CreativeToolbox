package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotCalculator;
import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldeditbridge.WorldEditInterface;
import no.runsafe.worldgenerator.PlotChunkGenerator;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;

public class RegenerateCommand extends PlayerCommand
{
	public RegenerateCommand(WorldEditInterface worldEdit, WorldGuardInterface worldGuard, PlotCalculator calculator, PlotFilter filter, PlotChunkGenerator generator)
	{
		super("regenerate", "Regenerates the plot you are currently in.", "runsafe.creative.regenerate");
		this.worldEdit = worldEdit;
		this.worldGuard = worldGuard;
		this.filter = filter;
		plotCalculator = calculator;
		this.generator = generator;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters, String[] arguments)
	{
		if (arguments != null && arguments.length > 0)
		{
			try
			{
				if (arguments[0].equalsIgnoreCase("flat"))
					generator.setMode(PlotChunkGenerator.Mode.FLAT);
				if (arguments[0].equalsIgnoreCase("normal"))
					generator.setMode(PlotChunkGenerator.Mode.NORMAL);
				if (arguments[0].equalsIgnoreCase("void"))
					generator.setMode(PlotChunkGenerator.Mode.VOID);
				return regenerate(executor, false);
			}
			catch (Exception e)
			{
				console.logException(e);
				return "Internal error in command!";
			}
			finally
			{
				generator.setMode(PlotChunkGenerator.Mode.NORMAL);
			}
		}
		return regenerate(executor, true);
	}

	@Override
	public String OnExecute(RunsafePlayer player, HashMap<String, String> stringStringHashMap)
	{
		return null;
	}

	private String regenerate(RunsafePlayer executor, boolean regenPadding)
	{
		List<String> candidate = filter.apply(worldGuard.getRegionsAtLocation(executor.getLocation()));
		Rectangle2D area;
		if (candidate != null && candidate.size() == 1)
			area = plotCalculator.pad(worldGuard.getRectangle(executor.getWorld(), candidate.get(0)));
		else
			area = plotCalculator.getPlotArea(executor.getLocation(), regenPadding);
		RunsafeLocation minPos = plotCalculator.getMinPosition(executor.getWorld(), area);
		RunsafeLocation maxPos = plotCalculator.getMaxPosition(executor.getWorld(), area);
		return worldEdit.regenerate(executor, minPos, maxPos) ? "Plot regenerated." : "Could not regenerate plot.";
	}

	private final WorldEditInterface worldEdit;
	private final WorldGuardInterface worldGuard;
	private final PlotCalculator plotCalculator;
	private final PlotFilter filter;
	private final PlotChunkGenerator generator;
}
