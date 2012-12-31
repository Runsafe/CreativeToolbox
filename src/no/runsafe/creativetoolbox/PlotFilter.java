package no.runsafe.creativetoolbox;

import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.worldguardbridge.WorldGuardInterface;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class PlotFilter implements IConfigurationChanged
{
	public PlotFilter(WorldGuardInterface worldGuard)
	{
		this.worldGuard = worldGuard;
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		filter = configuration.getConfigValueAsList("ignored");
		if (filter == null)
			filter = new ArrayList<String>();
		world = RunsafeServer.Instance.getWorld(configuration.getConfigValueAsString("world"));
		filterCache = null;
		worldName = configuration.getConfigValueAsString("world");
	}

	public List<String> apply(List<String> unfiltered)
	{
		if (unfiltered == null)
			return null;
		ArrayList<String> result = new ArrayList<String>();
		for (String value : unfiltered)
			if (!filter.contains(value))
				result.add(value);
		return result;
	}

	public String apply(String unfiltered)
	{
		if (unfiltered == null || filter.contains(unfiltered))
			return null;
		return unfiltered;
	}

	public List<String> getFiltered()
	{
		DateTime now = DateTime.now();
		if (filterCache == null || filterCache.isBefore(now.minusSeconds(30)))
		{
			filtered = apply(worldGuard.getRegionsInWorld(getWorld()));
			filterCache = now;
		}
		return filtered;
	}

	public RunsafeWorld getWorld()
	{
		if (world == null)
			world = RunsafeServer.Instance.getWorld(worldName);
		return world;
	}

	private List<String> filter;
	private final WorldGuardInterface worldGuard;
	private List<String> filtered;
	private DateTime filterCache;
	private RunsafeWorld world;
	private String worldName;
}
