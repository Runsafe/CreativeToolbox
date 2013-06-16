package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.PlotVoteRepository;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.HashMap;

public class VoteCommand extends PlayerCommand
{
	public VoteCommand(PlotManager manager, PlotVoteRepository votes)
	{
		super("vote", "Vote for the plot you are standing in.", "runsafe.creative.vote");
		this.manager = manager;
		this.votes = votes;
	}

	@Override
	public String OnExecute(RunsafePlayer player, HashMap<String, String> stringStringHashMap)
	{
		String region = manager.getCurrentRegionFiltered(player);
		if (!manager.voteValid(player, region))
			return "You are not allowed to vote for this plot.";

		votes.recordVote(player, region);
		return String.format("Thank you for voting for the plot %s", region);
	}

	private final PlotManager manager;
	private final PlotVoteRepository votes;
}
