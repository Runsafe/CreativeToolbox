package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IPlayer;

import java.util.Map;

public class VoteCommand extends PlayerCommand
{
	public VoteCommand(PlotManager manager)
	{
		super("vote", "Vote for the plot you are standing in.", "runsafe.creative.vote");
		this.manager = manager;
	}

	@Override
	public String OnExecute(IPlayer player, Map<String, String> stringStringHashMap)
	{
		String region = manager.getCurrentRegionFiltered(player);
		if (region == null)
			return "There is no plot here.";

		if (manager.disallowVote(player, region))
			return "You are not allowed to vote for this plot.";

		return manager.vote(player, region)
			? String.format("Thank you for voting for the plot \"%s\".", region)
			: "An error occurred while casting ballot!";
	}

	private final PlotManager manager;
}
