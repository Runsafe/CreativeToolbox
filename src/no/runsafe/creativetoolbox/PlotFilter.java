package no.runsafe.creativetoolbox;

import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlotFilter implements IConfigurationChanged
{
	public PlotFilter(IConfiguration configuration, WorldGuardInterface worldGuard)
	{
		this.config = configuration;
		this.worldGuard = worldGuard;
	}

	@Override
	public void OnConfigurationChanged()
	{
		filter = config.getConfigValueAsList("ignored");
		if (filter == null)
			filter = new ArrayList<String>();
		world = RunsafeServer.Instance.getWorld(config.getConfigValueAsString("world"));
		filterCache = null;
	}

	public List<String> apply(List<String> unfiltered)
	{
		if(unfiltered == null)
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
		Date now = new Date();
		if (filterCache == null || now.getTime() - filterCache.getTime() > 30000)
		{
			filtered = apply(worldGuard.getRegionsInWorld(getWorld()));
			filterCache = now;
		}
		return filtered;
	}

	public RunsafeWorld getWorld()
	{
		if (world == null)
			world = RunsafeServer.Instance.getWorld(config.getConfigValueAsString("world"));
		return world;
	}

	private final IConfiguration config;
	private List<String> filter;
	private final WorldGuardInterface worldGuard;
	private List<String> filtered;
	private Date filterCache;
	private RunsafeWorld world;
}
