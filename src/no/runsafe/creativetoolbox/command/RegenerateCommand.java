package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotCalculator;
import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldeditbridge.WorldEditInterface;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;

public class RegenerateCommand extends PlayerCommand
{
	public RegenerateCommand(PlotCalculator calculator, PlotFilter filter)
	{
		super("regenerate", "Regenerates the plot you are currently in.", "runsafe.creative.regenerate");
		this.filter = filter;
		worldEdit = RunsafePlugin.getFirstPluginAPI(WorldEditInterface.class);
		worldGuard = RunsafePlugin.getFirstPluginAPI(WorldGuardInterface.class);
		plotCalculator = calculator;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> params)
	{
		List<String> candidate = filter.apply(worldGuard.getRegionsAtLocation(executor.getLocation()));
		Rectangle2D area;
		if (candidate != null && candidate.size() == 1)
			area = plotCalculator.pad(worldGuard.getRectangle(executor.getWorld(), candidate.get(0)));
		else
			area = plotCalculator.getPlotArea(executor.getLocation(), true);
		RunsafeLocation minPos = plotCalculator.getMinPosition(executor.getWorld(), area);
		RunsafeLocation maxPos = plotCalculator.getMaxPosition(executor.getWorld(), area);
		return worldEdit.regenerate(executor, minPos, maxPos) ? "Plot regenerated." : "Could not regenerate plot.";
	}

	private final WorldEditInterface worldEdit;
	private final WorldGuardInterface worldGuard;
	private final PlotCalculator plotCalculator;
	private final PlotFilter filter;
}
