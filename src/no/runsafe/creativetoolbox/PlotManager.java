package no.runsafe.creativetoolbox;

import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotEntrance;
import no.runsafe.creativetoolbox.database.PlotEntranceRepository;
import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;
import org.bukkit.ChatColor;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormat;

import java.awt.geom.Rectangle2D;
import java.util.*;

public class PlotManager implements IConfigurationChanged
{
	public PlotManager(
		PlotFilter filter,
		WorldGuardInterface worldGuard,
		PlotEntranceRepository plotEntranceRepository,
		ApprovedPlotRepository approvedPlotRepository
	)
	{
		this.filter = filter;
		this.worldGuard = worldGuard;
		this.plotEntrance = plotEntranceRepository;
		this.plotApproval = approvedPlotRepository;
	}

	@Override
	public void OnConfigurationChanged(IConfiguration config)
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
		world = RunsafeServer.Instance.getWorld(config.getConfigValueAsString("world"));
		ignoredRegions = config.getConfigValueAsList("free.ignore");
		groundLevel = config.getConfigValueAsInt("plot.groundLevel");
		limit = new Period(0, 0, 0, config.getConfigValueAsInt("old_after"), 0, 0, 0, 0).toDurationTo(DateTime.now());
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
		ArrayList<Rectangle2D> takenPlots = new ArrayList<Rectangle2D>();
		for (String region : taken.keySet())
		{
			if (!ignoredRegions.contains(region))
				takenPlots.add(taken.get(region));
		}
		ArrayList<RunsafeLocation> freePlots = new ArrayList<RunsafeLocation>();
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
					freePlots.add(getLocation(x + origin.getWidth(), y + origin.getHeight(), groundLevel));
			}
		}
		return freePlots;
	}

	public RunsafeLocation getDefaultPlotEntrance(double x, double y)
	{
		if (!fence.contains(x, y))
			return null;

		x -= origin.getX();
		x /= origin.getWidth() + spacing;
		x = Math.ceil(x);
		x *= origin.getWidth() + spacing;
		x -= spacing;
		x += origin.getX();

		y -= origin.getY();
		y /= origin.getHeight() + spacing;
		y = Math.ceil(y);
		y *= origin.getHeight() + spacing;
		y -= spacing;
		y += origin.getY();

		return getLocation(x, y, groundLevel);
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

	public Map<String, String> getOldPlots()
	{
		if (!worldGuard.serverHasWorldGuard())
			return null;

		List<String> approvedPlots = plotApproval.getApprovedPlots();
		Map<String, Set<String>> checkList = worldGuard.getAllRegionsWithOwnersInWorld(getWorld());
		Map<String, String> hits = new HashMap<String, String>();
		for (String region : filter.apply(new ArrayList<String>(checkList.keySet())))
		{
			if (approvedPlots.contains(region))
				continue;

			Duration status = getPlotStatus(checkList.get(region));
			if (status != null && (status.equals(Duration.ZERO) || status.isShorterThan(limit)))
				continue;
			hits.put(region, formatReason(status));
		}
		return hits;
	}

	private String formatReason(Duration status)
	{
		String info = null;
		if (status != null && status.equals(ban))
			info = "banned";
		else if (status != null)
			info = PeriodFormat.getDefault().print(new Period(status, DateTime.now(), PeriodType.yearMonthDay()));

		return String.format(
			"%s%s%s",
			info == null || info.equals("banned") ? ChatColor.RED : ChatColor.YELLOW,
			info,
			ChatColor.RESET
		);
	}

	private Duration getPlotStatus(Set<String> owners)
	{
		Duration result = null;
		for (String owner : owners)
		{
			Duration ownerSeen = getSeen(owner);
			if (ownerSeen == null)
				return null;
			if (ownerSeen.isEqual(Duration.ZERO))
				return Duration.ZERO;
			if (result == null || !result.isEqual(ban))
				result = ownerSeen;
		}
		return result;
	}

	private Duration getSeen(String playerName)
	{
		playerName = playerName.toLowerCase();
		if (lastSeen.containsKey(playerName))
			return lastSeen.get(playerName);

		RunsafePlayer player = RunsafeServer.Instance.getPlayer(playerName);
		if (player.isOnline())
			lastSeen.put(playerName, Duration.ZERO);
		else if (player.isBanned())
			lastSeen.put(playerName, ban);
		else
		{
			DateTime logout = player.lastLogout();
			if (logout == null)
				lastSeen.put(playerName, null);
			else
				lastSeen.put(playerName, new Duration(player.lastLogout(), DateTime.now()));
		}
		return lastSeen.get(playerName);
	}

	public String getOldPlotPointer(RunsafePlayer player)
	{
		if (oldPlotPointers.containsKey(player.getName()))
			return oldPlotPointers.get(player.getName());
		return null;
	}

	public void setOldPlotPointer(RunsafePlayer player, String value)
	{
		oldPlotPointers.put(player.getName(), value);
	}


	public Map<String, String> getOldPlotWorkList(RunsafePlayer player)
	{
		if (!oldPlotList.containsKey(player.getName()))
			oldPlotList.put(player.getName(), getOldPlots());
		return oldPlotList.get(player.getName());
	}

	public void clearOldPlotWorkList(RunsafePlayer player)
	{
		if (oldPlotList.containsKey(player.getName()))
			oldPlotList.remove(player.getName());
	}

	public RunsafeWorld getWorld()
	{
		return world;
	}

	final PlotFilter filter;
	final WorldGuardInterface worldGuard;
	Rectangle2D fence;
	Rectangle2D origin;
	int spacing;
	final PlotEntranceRepository plotEntrance;
	final ApprovedPlotRepository plotApproval;
	RunsafeWorld world;
	final Map<String, String> oldPlotPointers = new HashMap<String, String>();
	final Map<String, Map<String, String>> oldPlotList = new HashMap<String, Map<String, String>>();
	List<String> ignoredRegions;
	int groundLevel;
	Duration limit;
	Duration ban = new Duration(Long.MAX_VALUE);
	final HashMap<String, Duration> lastSeen = new HashMap<String, Duration>();
}
