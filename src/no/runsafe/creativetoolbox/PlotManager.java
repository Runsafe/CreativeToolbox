package no.runsafe.creativetoolbox;

import no.runsafe.creativetoolbox.database.PlotEntrance;
import no.runsafe.creativetoolbox.database.PlotEntranceRepository;
import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlotManager implements IConfigurationChanged
{
	public PlotManager(
		PlotFilter filter,
		WorldGuardInterface worldGuard,
		IConfiguration config,
		PlotEntranceRepository plotEntranceRepository,
		IOutput console
	)
	{
		this.filter = filter;
		this.worldGuard = worldGuard;
		this.config = config;
		this.plotEntrance = plotEntranceRepository;
		this.console = console;
	}

	@Override
	public void OnConfigurationChanged()
	{
		fence = new Rectangle2D.Double();
		fence.setRect(
			config.getConfigValueAsDouble("fence.x"),
			config.getConfigValueAsDouble("fence.y"),
			config.getConfigValueAsDouble("fence.w"),
			config.getConfigValueAsDouble("fence.h")
		);
		origin = new Rectangle2D.Double();
		origin.setRect(
			config.getConfigValueAsDouble("plot.origin.x"),
			config.getConfigValueAsDouble("plot.origin.y"),
			config.getConfigValueAsDouble("plot.w"),
			config.getConfigValueAsDouble("plot.h")
		);
		spacing = config.getConfigValueAsInt("plot.spacing");
	}

	public java.util.List<RunsafeLocation> getPlotEntrances()
	{
		ArrayList<RunsafeLocation> entrances = new ArrayList<RunsafeLocation>();
		for (String plot : filter.getFiltered())
			entrances.add(plotEntrance.get(plot).getLocation());
		return entrances;
	}

	public java.util.List<RunsafeLocation> getFreePlotEntrances()
	{
		Map<String, Rectangle2D> taken = worldGuard.getRegionRectanglesInWorld(filter.getWorld());
		List<String> ignored = config.getConfigValueAsList("free.ignore");
		ArrayList<Rectangle2D> takenPlots = new ArrayList<Rectangle2D>();
		for (String region : taken.keySet())
		{
			if (!ignored.contains(region))
				takenPlots.add(taken.get(region));
		}
		ArrayList<RunsafeLocation> freePlots = new ArrayList<RunsafeLocation>();
		int ground = config.getConfigValueAsInt("plot.groundLevel");
		for (double x = origin.getX(); x < fence.getMaxX(); x += origin.getWidth() + spacing)
		{
			for (double y = origin.getY(); y < fence.getMaxY(); y += origin.getHeight() + spacing)
			{
				boolean free = true;
				for (Rectangle2D region : takenPlots)
				{
					if (region.contains(x + 1, y + 1))
					{
						free = false;
						break;
					}
				}
				if (free)
					freePlots.add(getLocation(x + origin.getWidth(), y + origin.getHeight(), ground));
			}
		}
		return freePlots;
	}

	public RunsafeLocation getDefaultPlotEntrance(double x, double y)
	{
		if (!fence.contains(x, y))
			return null;

		console.fine(String.format("x:%.2f", x));
		x -= origin.getX();
		console.fine(String.format("x:%.2f", x));
		x /= origin.getWidth() + spacing;
		console.fine(String.format("x:%.2f", x));
		x = Math.ceil(x);
		console.fine(String.format("x:%.2f", x));
		x *= origin.getWidth() + spacing;
		console.fine(String.format("x:%.2f", x));
		x -= spacing;
		console.fine(String.format("x:%.2f", x));
		x += origin.getX();
		console.fine(String.format("x:%.2f", x));

		y -= origin.getY();
		y /= origin.getHeight() + spacing;
		y = Math.ceil(y);
		y *= origin.getHeight() + spacing;
		y -= spacing;
		y += origin.getY();

		return getLocation(x, y, config.getConfigValueAsDouble("plot.groundLevel"));
	}

	public RunsafeLocation getLocation(double x, double y, double altitude)
	{
		// This pitch and yaw faces into the region if x and y are the maximums.
		return new RunsafeLocation(filter.getWorld(), x, altitude, y, 137.55f, -1.65f);
	}

	/* TODO Requires WorldEditBridge in order to be completed
	public void Regenerate(String plotName)
	{
		RunsafeLocation location = worldGuard.getRegionLocation(filter.getWorld(), plotName);
		if(location == null)
			return;
		Rectangle2D target = new Rectangle2D.Double(
			location.getX() - origin.getWidth(),
			location.getY() - origin.getHeight(),
			origin.getWidth(),
			origin.getHeight()
		);
	}
	*/

	public RunsafeLocation getPlotEntrance(String plot)
	{
		PlotEntrance entrance = plotEntrance.get(plot);
		RunsafeLocation target = null;
		if (entrance != null)
			target = entrance.getLocation();

		if (target == null)
		{
			target = worldGuard.getRegionLocation(filter.getWorld(), plot);
			if (target == null)
				return null;

			target = getDefaultPlotEntrance(target.getX() - 1, target.getZ() - 1);
			while (filter.getWorld().getBlockAt(target).canPassThrough() && target.getY() > 60)
				target.setY(target.getY() - 1);
			target.setY(target.getY() + 2);
		}
		return target;
	}

	private PlotFilter filter;
	private WorldGuardInterface worldGuard;
	private Rectangle2D fence;
	private IConfiguration config;
	private Rectangle2D origin;
	private int spacing;
	private PlotEntranceRepository plotEntrance;
	private IOutput console;
}
