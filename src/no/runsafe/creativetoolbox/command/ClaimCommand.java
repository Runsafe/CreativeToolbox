package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotCalculator;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.RunsafeWorld;
import no.runsafe.framework.minecraft.player.RunsafeAmbiguousPlayer;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;

public class ClaimCommand extends PlayerCommand
{
	public ClaimCommand(PlotManager manager, PlotCalculator calculator, WorldGuardInterface worldGuard)
	{
		super("claim", "Claims a plot", "runsafe.creative.claim", "owner");
		this.manager = manager;
		this.calculator = calculator;
		this.worldGuard = worldGuard;
	}

	@Override
	public String OnExecute(RunsafePlayer player, HashMap<String, String> params)
	{
		String current = manager.getCurrentRegionFiltered(player);
		if (current != null)
			return String.format("This plot is already claimed as %s!", current);

		RunsafeWorld world = player.getWorld();
		RunsafePlayer owner = RunsafeServer.Instance.getOfflinePlayerExact(params.get("owner"));

		if (owner instanceof RunsafeAmbiguousPlayer)
			return owner.toString();

		List<String> existing = worldGuard.getOwnedRegions(owner, world);
		int n = 1;
		String plotName = String.format("%s_%%d", owner.getName().toLowerCase());
		while (existing.contains(String.format(plotName, n)))
			n++;
		plotName = String.format(plotName, n);

		Rectangle2D region = calculator.getPlotArea(player.getLocation());
		if (worldGuard.createRegion(
			owner, world, plotName,
			calculator.getMinPosition(world, region),
			calculator.getMaxPosition(world, region)
		))
			return String.format("Successfully claimed the plot \"%s\" for %s!", plotName, owner.getPrettyName());

		return String.format("Unable to claim a new plot for %s :(", owner.getPrettyName());
	}

	private final PlotManager manager;
	private final PlotCalculator calculator;
	private final WorldGuardInterface worldGuard;
}
