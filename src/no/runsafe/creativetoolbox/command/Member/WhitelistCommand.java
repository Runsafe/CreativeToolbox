package no.runsafe.creativetoolbox.command.Member;

import no.runsafe.creativetoolbox.database.PlotMemberBlacklistRepository;
import no.runsafe.framework.api.IServer;
import no.runsafe.framework.api.command.ExecutableCommand;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.api.command.argument.PlayerArgument;
import no.runsafe.framework.api.player.IAmbiguousPlayer;
import no.runsafe.framework.api.player.IPlayer;

import java.util.Map;

public class WhitelistCommand extends ExecutableCommand
{
	public WhitelistCommand(PlotMemberBlacklistRepository blacklistRepository, IServer server)
	{
		super("whitelist", "Removes a player from the membership blacklist.", "runsafe.creative.whitelist", new PlayerArgument());
		this.blacklistRepository = blacklistRepository;
		this.server = server;
	}

	@Override
	public String OnExecute(ICommandExecutor executor, Map<String, String> params)
	{
		IPlayer player = server.getPlayer(params.get("player"));
		if (player == null)
			return "&cUnable to locate player.";

		if (player instanceof IAmbiguousPlayer)
			return player.toString();

		if (!blacklistRepository.isBlacklisted(player))
			return "&cThat player is not blacklisted.";

		blacklistRepository.remove(player);
		return String.format("The player %s has been removed from the blacklist.", player.getPrettyName());
	}

	private final PlotMemberBlacklistRepository blacklistRepository;
	private final IServer server;
}
