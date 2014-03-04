package no.runsafe.creativetoolbox.command.Member;

import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.PlotMemberBlacklistRepository;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.AsyncCommand;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.Player;
import no.runsafe.framework.api.player.IAmbiguousPlayer;
import no.runsafe.framework.api.player.IPlayer;

public class BlacklistCommand extends AsyncCommand
{
	public BlacklistCommand(PlotMemberBlacklistRepository blacklistRepository, PlotManager manager, IScheduler scheduler)
	{
		super("blacklist", "Blocks a certain player from being added to any additional creative plots", "runsafe.creative.blacklist", scheduler, new Player().require());
		this.blacklistRepository = blacklistRepository;
		this.manager = manager;
	}

	@Override
	public String OnAsyncExecute(ICommandExecutor executor, IArgumentList params)
	{
		IPlayer player = params.getValue("player");
		if (player == null)
			return "&cUnable to locate player.";

		if (player instanceof IAmbiguousPlayer)
			return player.toString();

		if (blacklistRepository.isBlacklisted(player))
			return "&cThat player is already blacklisted.";

		blacklistRepository.add(executor, player);
		manager.removeMember(player);
		return String.format("The player %s has been blacklisted.", player.getPrettyName());
	}

	private final PlotMemberBlacklistRepository blacklistRepository;
	private final PlotManager manager;
}
