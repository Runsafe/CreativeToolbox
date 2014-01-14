package no.runsafe.creativetoolbox.command.Tag;

import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.PlotTagRepository;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;

public class ClearCommand extends PlayerAsyncCommand
{
	public ClearCommand(IScheduler scheduler, PlotManager manager, PlotTagRepository tagRepository)
	{
		super("clear", "Clears the tags from the current plot", "runsafe.creative.tag.clear", scheduler);
		this.manager = manager;
		this.tagRepository = tagRepository;
	}

	@Override
	public String OnAsyncExecute(IPlayer player, IArgumentList stringStringHashMap)
	{
		if (manager.isInWrongWorld(player))
			return "You cannot use that here.";

		String plot = manager.getCurrentRegionFiltered(player);
		if (plot == null)
			return "There is no plot here.";

		return tagRepository.setTags(plot, null) ?
			String.format("Cleared tags for %s.", plot) :
			String.format("Could not clear tags for %s.", plot);
	}

	private final PlotManager manager;
	private final PlotTagRepository tagRepository;
}
