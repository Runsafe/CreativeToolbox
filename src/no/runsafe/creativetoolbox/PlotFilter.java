package no.runsafe.creativetoolbox;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.worldguardbridge.IRegionControl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PlotFilter implements IConfigurationChanged
{
	public PlotFilter(IRegionControl worldGuard)
	{
		this.worldGuard = worldGuard;
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		filter = configuration.getConfigValueAsList("ignored");
		if (filter == null)
			filter = new ArrayList<>();
		world = configuration.getConfigValueAsWorld("world");

		filterCache = null;
	}

	public List<String> apply(List<String> unfiltered)
	{
		if (unfiltered == null)
			return null;
		ArrayList<String> result = new ArrayList<>();
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
		if (filterCache == null || filterCache.isBefore(Instant.now().minusSeconds(30)))
		{
			filtered = apply(worldGuard.getRegionsInWorld(getWorld()));
			filterCache = Instant.now();
		}
		return filtered;
	}

	public IWorld getWorld()
	{
		return world;
	}

	private List<String> filter;
	private final IRegionControl worldGuard;
	private List<String> filtered;
	private Instant filterCache;
	private IWorld world;
}
