package no.runsafe.creativetoolbox.event;

import no.runsafe.creativetoolbox.PlotCalculator;
import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.block.IBlock;
import no.runsafe.framework.api.event.player.IPlayerRightClickBlock;
import no.runsafe.framework.api.log.IConsole;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.worldeditbridge.WorldEditInterface;
import no.runsafe.worldgenerator.IPlotGenerator;
import no.runsafe.worldgenerator.PlotChunkGenerator;
import no.runsafe.worldguardbridge.IRegionControl;
import org.apache.commons.lang.StringUtils;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SyncInteractEvents implements IPlayerRightClickBlock
{
	public SyncInteractEvents(IPlotGenerator plotGenerator, PlotCalculator calculator, WorldEditInterface worldEdit, IRegionControl worldGuard, PlotFilter filter, PlotManager manager, IConsole output)
	{
		this.plotGenerator = plotGenerator;
		this.calculator = calculator;
		this.worldEdit = worldEdit;
		this.worldGuard = worldGuard;
		this.filter = filter;
		this.manager = manager;
		this.console = output;
	}

	public void startRegeneration(IPlayer executor, Rectangle2D area, PlotChunkGenerator.Mode mode)
	{
		regenerations.put(executor, area);

		if (mode != null)
			generator.put(executor, mode);
		else generator.remove(executor);
	}

	public void startDeletion(IPlayer executor, Map<String, Rectangle2D> regions)
	{
		deletions.put(executor, regions);
	}

	@Override
	public boolean OnPlayerRightClick(IPlayer player, RunsafeMeta itemInHand, IBlock block)
	{
		return
			deletions.isEmpty() && regenerations.isEmpty()
				|| executeRegenerations(player, block.getLocation()) && executeDeletion(player, block.getLocation());
	}

	private boolean executeRegenerations(IPlayer player, ILocation location)
	{
		if (!regenerations.containsKey(player))
			return true;

		boolean changeMode = generator.containsKey(player)
			&& generator.get(player) != PlotChunkGenerator.Mode.NORMAL;
		try
		{
			Rectangle2D area = regenerations.get(player);
			if (!area.contains(location.getX(), location.getZ()))
			{
				regenerations.remove(player);
				if (changeMode)
					generator.remove(player);
				return true;
			}
			if (changeMode)
			{
				PlotChunkGenerator.Mode mode = generator.get(player);
				plotGenerator.setMode(mode);
			}

			IWorld playerWorld = player.getWorld();
			ILocation minPos = calculator.getMinPosition(playerWorld, area);
			ILocation maxPos = calculator.getMaxPosition(playerWorld, area);
			player.sendColouredMessage(
				worldEdit.regenerate(player, minPos, maxPos, false)
					? "Plot regenerated."
					: "Could not regenerate plot."
			);
			console.logInformation("%s just regenerated plots at [%s].", player.getName(), getRegionNameString(player));

			return false;
		}
		finally
		{
			if (changeMode)
			{
				plotGenerator.setMode(PlotChunkGenerator.Mode.NORMAL);
				generator.remove(player);
			}
			regenerations.remove(player);
		}
	}

	private String getRegionNameString(IPlayer player)
	{
		ILocation location = player.getLocation();
		if (location == null)
			return "Unknown";

		List<String> candidate = filter.apply(worldGuard.getRegionsAtLocation(location));
		if (candidate != null && !candidate.isEmpty())
			return StringUtils.join(candidate, ",");

		return String.format("X: %.2f, Z: %.2f", location.getX(), location.getZ());
	}

	private boolean executeDeletion(IPlayer player, ILocation location)
	{
		if (!deletions.containsKey(player))
			return true;

		StringBuilder results = new StringBuilder();
		Map<String, Rectangle2D> process = deletions.get(player);
		deletions.remove(player);
		boolean nothing = true;
		for (String region : process.keySet())
		{
			Rectangle2D area = process.get(region);
			if (!area.contains(location.getX(), location.getZ()))
				continue;

			nothing = false;
			ILocation minPos = calculator.getMinPosition(player.getWorld(), area);
			ILocation maxPos = calculator.getMaxPosition(player.getWorld(), area);
			manager.delete(player, region);
			plotGenerator.setMode(PlotChunkGenerator.Mode.NORMAL);
			worldEdit.regenerate(player, minPos, maxPos, false);
			results.append(String.format("Deleted plot '%s'.", region));
			console.logInformation(String.format("%s deleted plot %s", player.getName(), region));
		}
		if (!nothing)
			player.sendColouredMessage(results.toString());
		return nothing;
	}

	private final ConcurrentHashMap<IPlayer, Map<String, Rectangle2D>> deletions = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<IPlayer, Rectangle2D> regenerations = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<IPlayer, PlotChunkGenerator.Mode> generator = new ConcurrentHashMap<>();
	private final IPlotGenerator plotGenerator;
	private final PlotCalculator calculator;
	private final WorldEditInterface worldEdit;
	private final IRegionControl worldGuard;
	private final PlotFilter filter;
	private final PlotManager manager;
	private final IConsole console;
}
