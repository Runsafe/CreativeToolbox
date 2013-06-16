package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotCalculator;
import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldeditbridge.WorldEditInterface;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;

public class SelectCommand extends PlayerCommand
{
	public SelectCommand(WorldEditInterface worldEdit, WorldGuardInterface worldGuard, PlotCalculator calculator, PlotFilter filter)
	{
		super("select", "Sets your WorldEdit region to the plot you are in", "runsafe.creative.select");
		this.worldEdit = worldEdit;
		this.worldGuard = worldGuard;
		this.filter = filter;
		plotCalculator = calculator;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		List<String> candidate = filter.apply(worldGuard.getRegionsAtLocation(executor.getLocation()));
		Rectangle2D area;
		if (candidate != null && candidate.size() == 1)
			area = worldGuard.getRectangle(executor.getWorld(), candidate.get(0));
		else
			area = plotCalculator.getPlotArea(executor.getLocation(), false);
		RunsafeLocation minPos = plotCalculator.getMinPosition(executor.getWorld(), area);
		RunsafeLocation maxPos = plotCalculator.getMaxPosition(executor.getWorld(), area);
		worldEdit.select(executor, minPos, maxPos);
		return null;
	}

	private final WorldEditInterface worldEdit;
	private final WorldGuardInterface worldGuard;
	private final PlotCalculator plotCalculator;
	private final PlotFilter filter;
}
