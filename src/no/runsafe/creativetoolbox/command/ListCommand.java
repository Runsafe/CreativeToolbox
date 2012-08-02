package no.runsafe.creativetoolbox.command;

import joptsimple.internal.Strings;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.command.ICommand;
import no.runsafe.framework.command.RunsafeCommand;
import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.Collection;
import java.util.List;

public class ListCommand extends RunsafeCommand
{
	public ListCommand(RunsafeServer server, IConfiguration configuration)
	{
		super("list", null, "playerName");
		this.server = server;
		config = configuration;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args)
	{
		RunsafeWorld world = RunsafeServer.Instance.getWorld(config.getConfigValueAsString("world"));
		WorldGuardInterface worldGuard = RunsafePlugin.Instances.get("RunsafeWorldGuardBridge").getComponent(WorldGuardInterface.class);
		if(!worldGuard.serverHasWorldGuard())
			return "No WorldGuard installed";

		List<String> property = worldGuard.getOwnedRegions(server.getPlayer(getArg("playerName")), world);
		return String.format("%d regions owned:\n  %s", property.size(), Strings.join(property, "\n  "));
	}

	private RunsafeServer server;
	private IConfiguration config;
}
