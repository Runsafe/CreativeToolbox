package no.runsafe.creativetoolbox.command;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;

import java.util.List;

public class ConfigurationManager implements IConfigurationChanged
{
	@Override
	public void OnConfigurationChanged(IConfiguration config)
	{
		max_listed = config.getConfigValueAsInt("max_listed");
		noClean = config.getConfigValueAsList("clean.ignore");
	}

	public int getOldPlotsListLimit()
	{
		return max_listed;
	}

	public List<String> getCleanFilter()
	{
		return noClean;
	}

	private int max_listed;
	private List<String> noClean;
}
