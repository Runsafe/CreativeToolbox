package no.runsafe.creativetoolbox.command;

import no.runsafe.framework.command.RunsafePlayerCommand;
import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.worldguardbridge.WorldGuardInterface;

public class FindFreePlotCommand extends RunsafePlayerCommand implements IConfigurationChanged
{
	public FindFreePlotCommand(WorldGuardInterface worldGuardInterface, IConfiguration configuration)
	{
		super("findfreeplot", null);
		config = configuration;
	}

	@Override
	public void OnConfigurationChanged()
	{
		world = RunsafeServer.Instance.getWorld(config.getConfigValueAsString("world"));
	}

	public RunsafeWorld getWorld()
	{
		if (world == null)
			world = RunsafeServer.Instance.getWorld(config.getConfigValueAsString("world"));
		return world;
	}

	private IConfiguration config;
	private RunsafeWorld world;
}
