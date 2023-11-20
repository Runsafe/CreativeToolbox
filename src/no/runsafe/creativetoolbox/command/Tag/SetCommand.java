package no.runsafe.creativetoolbox.command.Tag;

import com.google.common.collect.Lists;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.PlotTagRepository;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.TrailingArgument;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.text.ChatColour;

public class SetCommand extends PlayerAsyncCommand
{
	public SetCommand(IScheduler scheduler, PlotManager manager, PlotTagRepository tagRepository)
	{
		super("set", "Sets the tags for the current plot", "runsafe.creative.tag.set", scheduler, new TrailingArgument("tags"));
		this.manager = manager;
		this.tagRepository = tagRepository;
	}

	@Override
	public String OnAsyncExecute(IPlayer player, IArgumentList param)
	{
		if (manager.isInWrongWorld(player))
			return "&cYou cannot use that here.";

		String plot = manager.getCurrentRegionFiltered(player);
		if (plot == null)
			return "&cThere is no plot here.";

		String[] tags = ChatColour.Strip(param.getValue("tags")).split("\\s+");
		return String.format(
			tagRepository.setTags(plot, Lists.newArrayList(tags))
				? "&aChanged tags for plot %s."
				: "&cUnable to save tags for plot %s.",
			plot
		);
	}

	private final PlotManager manager;
	private final PlotTagRepository tagRepository;
}
