package no.runsafe.creativetoolbox.command.Member;

import no.runsafe.creativetoolbox.database.PlotMemberBlacklistRepository;
import no.runsafe.framework.api.command.ExecutableCommand;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.Player;
import no.runsafe.framework.api.player.IPlayer;

public class WhitelistCommand extends ExecutableCommand
{
	public WhitelistCommand(PlotMemberBlacklistRepository blacklistRepository)
	{
		super("whitelist", "Removes a player from the membership blacklist.", "runsafe.creative.whitelist", new Player.Any().require());
		this.blacklistRepository = blacklistRepository;
	}

	@Override
	public String OnExecute(ICommandExecutor executor, IArgumentList params)
	{
		IPlayer player = params.getValue("player");
		if (player == null)
			return null;

		if (!blacklistRepository.isBlacklisted(player))
			return "&cThat player is not blacklisted.";

		blacklistRepository.remove(player);
		return String.format("The player %s has been removed from the blacklist.", player.getPrettyName());
	}

	private final PlotMemberBlacklistRepository blacklistRepository;
}
