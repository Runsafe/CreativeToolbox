package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotCalculator;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldeditbridge.WorldEditInterface;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;

public class SelectCommand extends PlayerCommand
{
	public SelectCommand(PlotCalculator calculator)
	{
		super("select", "Sets your WorldEdit region to the plot you are in", "runsafe.creative.select");
		worldEdit = RunsafePlugin.getFirstPluginAPI(WorldEditInterface.class);
		worldGuard = RunsafePlugin.getFirstPluginAPI(WorldGuardInterface.class);
		plotCalculator = calculator;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		List<String> candidate = worldGuard.getRegionsAtLocation(executor.getLocation());
		Rectangle2D area;
		if (candidate != null && candidate.size() == 1)
			area = worldGuard.getRectangle(executor.getWorld(), candidate.get(0));
		else
			area = plotCalculator.getPlotArea(executor.getLocation());
		worldEdit.select(
			executor,
			plotCalculator.getMinPosition(executor.getWorld(), area),
			plotCalculator.getMaxPosition(executor.getWorld(), area)
		);
		return null;
	}

	private final WorldEditInterface worldEdit;
	private final WorldGuardInterface worldGuard;
	private final PlotCalculator plotCalculator;
}
