package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotCalculator;
import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.worldeditbridge.WorldEditInterface;
import no.runsafe.worldguardbridge.IRegionControl;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;

public class SelectCommand extends PlayerCommand
{
	public SelectCommand(WorldEditInterface worldEdit, IRegionControl worldGuard, PlotCalculator calculator, PlotFilter filter)
	{
		super("select", "Sets your WorldEdit region to the plot you are in", "runsafe.creative.select");
		this.worldEdit = worldEdit;
		this.worldGuard = worldGuard;
		this.filter = filter;
		plotCalculator = calculator;
	}

	@Override
	public String OnExecute(IPlayer executor, Map<String, String> parameters)
	{
		List<String> candidate = filter.apply(worldGuard.getRegionsAtLocation(executor.getLocation()));
		Rectangle2D area;
		if (candidate != null && candidate.size() == 1)
			area = worldGuard.getRectangle(executor.getWorld(), candidate.get(0));
		else
			area = plotCalculator.getPlotArea(executor.getLocation(), false);
		ILocation minPos = plotCalculator.getMinPosition(executor.getWorld(), area);
		ILocation maxPos = plotCalculator.getMaxPosition(executor.getWorld(), area);
		worldEdit.select(executor, minPos, maxPos);
		return null;
	}

	private final WorldEditInterface worldEdit;
	private final IRegionControl worldGuard;
	private final PlotCalculator plotCalculator;
	private final PlotFilter filter;
}
