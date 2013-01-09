package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotCalculator;
import no.runsafe.creativetoolbox.Plugin;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.command.RunsafeAsyncPlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.timer.IScheduler;
import no.runsafe.worldeditbridge.WorldEditInterface;

import java.awt.geom.Rectangle2D;

public class SelectCommand extends RunsafeAsyncPlayerCommand
{
	public SelectCommand(IScheduler scheduler, PlotCalculator calculator)
	{
		super("select", scheduler);
		RunsafePlugin bridge = Plugin.Instances.get("WorldEditBridge");
		if (bridge != null)
			worldEdit = bridge.getComponent(WorldEditInterface.class);
		plotCalculator = calculator;
	}

	@Override
	public String getDescription()
	{
		return "Sets your WorldEdit region to the plot you are in";
	}

	@Override
	public String requiredPermission()
	{
		if (worldEdit == null)
			return null;
		return "runsafe.creative.select";
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args)
	{
		Rectangle2D area = plotCalculator.getPlotArea(executor.getLocation());
		worldEdit.select(
			executor,
			plotCalculator.getMinPosition(executor.getWorld(), area),
			plotCalculator.getMaxPosition(executor.getWorld(), area)
		);
		return null;
	}

	private WorldEditInterface worldEdit;
	private PlotCalculator plotCalculator;
}
