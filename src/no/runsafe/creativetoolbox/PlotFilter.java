package no.runsafe.creativetoolbox;

import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.IConfigurationChanged;

import java.util.ArrayList;
import java.util.List;

public class PlotFilter implements IConfigurationChanged
{
	public PlotFilter(IConfiguration configuration)
	{
		this.config = configuration;
	}

	public List<String> apply(List<String> unfiltered)
	{
		ArrayList<String> result = new ArrayList<String>();
		for(String value : unfiltered)
			if(!filter.contains(value))
				result.add(value);
		return result;
	}

	public String apply(String unfiltered)
	{
		if(filter.contains(unfiltered))
			return null;
		return unfiltered;
	}

	@Override
	public void OnConfigurationChanged()
	{
		filter = config.getConfigValueAsList("ignored");
		if(filter == null)
			filter = new ArrayList<String>();
	}

	private IConfiguration config;
	private List<String> filter;
}
