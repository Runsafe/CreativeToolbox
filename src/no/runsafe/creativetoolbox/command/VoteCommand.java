package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IPlayer;

public class VoteCommand extends PlayerCommand
{
	public VoteCommand(PlotManager manager)
	{
		super("vote", "Vote for the plot you are standing in.", "runsafe.creative.vote");
		this.manager = manager;
	}

	@Override
	public String OnExecute(IPlayer player, IArgumentList stringStringHashMap)
	{
		if (manager.isInWrongWorld(player))
			return "&cYou cannot use that here.";

		String region = manager.getCurrentRegionFiltered(player);
		if (region == null)
			return "&cThere is no plot here.";

		if (manager.disallowVote(player, region))
			return "&cYou are not allowed to vote for this plot.";

		return manager.vote(player, region)
			? String.format("&aThank you for voting for the plot \"%s\".", region)
			: "&cAn error occurred while casting ballot!";
	}

	private final PlotManager manager;
}
