package no.runsafe.creativetoolbox.command.Tag;

import com.google.common.collect.Lists;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.PlotTagRepository;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.HashMap;

public class SetCommand extends PlayerAsyncCommand
{
	public SetCommand(IScheduler scheduler, PlotManager manager, PlotTagRepository tagRepository)
	{
		super("set", "Sets the tags for the current plot", "runsafe.creative.tag.set", scheduler, "tags");
		this.manager = manager;
		this.tagRepository = tagRepository;
		captureTail();
	}

	@Override
	public String OnAsyncExecute(RunsafePlayer player, HashMap<String, String> param)
	{
		String plot = manager.getCurrentRegionFiltered(player);
		if (plot == null)
			return "There is no plot here.";

		String[] tags = param.get("tags").split("\\s+");
		return String.format(
			tagRepository.setTags(plot, Lists.newArrayList(tags))
				? "Changed tags for plot %s."
				: "Unable to save tags for plot %s.",
			plot
		);
	}

	private final PlotManager manager;
	private final PlotTagRepository tagRepository;
}
