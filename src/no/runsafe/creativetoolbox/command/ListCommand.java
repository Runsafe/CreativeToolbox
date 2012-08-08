package no.runsafe.creativetoolbox.command;

import joptsimple.internal.Strings;
import no.runsafe.framework.command.RunsafeCommand;
import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.List;

public class ListCommand extends RunsafeCommand implements IConfigurationChanged
{
	public ListCommand(RunsafeServer server, IConfiguration configuration, WorldGuardInterface worldGuard)
	{
		super("list", null, "playerName");
		this.server = server;
		config = configuration;
		this.worldGuard = worldGuard;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args)
	{
		if (!worldGuard.serverHasWorldGuard())
			return "No WorldGuard installed";

		List<String> property = worldGuard.getOwnedRegions(server.getPlayer(getArg("playerName")), getWorld());
		return String.format("%d regions owned:\n  %s", property.size(), Strings.join(property, "\n  "));
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

	private RunsafeServer server;
	private IConfiguration config;
	private WorldGuardInterface worldGuard;
	private RunsafeWorld world;
}
