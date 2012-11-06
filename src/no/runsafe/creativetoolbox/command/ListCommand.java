package no.runsafe.creativetoolbox.command;

import no.runsafe.framework.command.RunsafeAsyncCommand;
import no.runsafe.framework.command.RunsafeCommand;
import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.timer.IScheduler;
import no.runsafe.worldguardbridge.WorldGuardInterface;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class ListCommand extends RunsafeAsyncCommand
{
	public ListCommand(
		RunsafeServer server,
		WorldGuardInterface worldGuard,
		IConfiguration configuration,
		IScheduler scheduler)
	{
		super("list", scheduler, "playerName");
		this.server = server;
		this.worldGuard = worldGuard;
		this.configuration = configuration;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args)
	{
		if (!worldGuard.serverHasWorldGuard())
			return "Unable to find WorldGuard!";

		List<String> property = worldGuard.getOwnedRegions(server.getPlayer(getArg("playerName")), getWorld());
		return String.format("%d regions owned:\n  %s", property.size(), StringUtils.join(property, "\n  "));
	}

	@Override
	public String getDescription()
	{
		return "lists plots owned by a player.";
	}

	@Override
	public String requiredPermission()
	{
		return "runsafe.creative.list";
	}

	public RunsafeWorld getWorld()
	{
		return RunsafeServer.Instance.getWorld(configuration.getConfigValueAsString("world"));
	}

	private final RunsafeServer server;
	private final WorldGuardInterface worldGuard;
	private final IConfiguration configuration;
}
