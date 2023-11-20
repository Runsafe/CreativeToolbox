package no.runsafe.creativetoolbox.command.Tag;

import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.PlotTagRepository;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.TrailingArgument;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.text.ChatColour;

public class AddCommand extends PlayerAsyncCommand
{
	public AddCommand(IScheduler scheduler, PlotTagRepository tagRepository, PlotManager manager)
	{
		super("add", "Add one or more tags to the current plot", "runsafe.creative.tag.add", scheduler, new TrailingArgument("tag"));
		this.tagRepository = tagRepository;
		this.manager = manager;
	}

	@Override
	public String OnAsyncExecute(IPlayer player, IArgumentList params)
	{
		if (manager.isInWrongWorld(player))
			return "&cYou cannot use that here.";
		String plot = manager.getCurrentRegionFiltered(player);
		if (plot == null)
			return "&cThere is no plot here.";

		String[] tags = ChatColour.Strip(params.getValue("tag")).split("\\s+");
		boolean success = true;
		for (String tag : tags)
			success = success && tagRepository.addTag(plot, tag);

		return String.format(
			success ? "&aSuccessfully updated tags for plot %s." : "&cCould not update %s with tag.",
			plot
		);
	}

	private final PlotTagRepository tagRepository;
	private final PlotManager manager;
}
