package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.framework.command.player.PlayerAsyncCommand;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.timer.IScheduler;
import no.runsafe.worldguardbridge.WorldGuardInterface;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;

public class ListCommand extends PlayerAsyncCommand
{
	public ListCommand(
		RunsafeServer server,
		WorldGuardInterface worldGuard,
		PlotFilter filter,
		IScheduler scheduler)
	{
		super("list", "lists plots owned by a player.", "runsafe.creative.list", scheduler, "playerName");
		this.server = server;
		this.worldGuard = worldGuard;
		this.filter = filter;
	}

	@Override
	public String OnAsyncExecute(RunsafePlayer executor, HashMap<String, String> parameters, String[] arguments)
	{
		if (!worldGuard.serverHasWorldGuard())
			return "Unable to find WorldGuard!";

		if (filter.getWorld() == null)
			return "No world defined!";

		RunsafePlayer player = server.getPlayer(parameters.get("playerName"));

		List<String> property = filter.apply(worldGuard.getOwnedRegions(player, filter.getWorld()));
		return String.format(
			"%d regions owned by %s:\n  %s",
			property.size(),
			player.getPrettyName(),
			StringUtils.join(property, "\n  ")
		);
	}

	private final RunsafeServer server;
	private final WorldGuardInterface worldGuard;
	private final PlotFilter filter;
}
