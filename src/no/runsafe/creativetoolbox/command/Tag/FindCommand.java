package no.runsafe.creativetoolbox.command.Tag;

import no.runsafe.creativetoolbox.database.PlotTagRepository;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.AsyncCommand;
import no.runsafe.framework.api.command.ICommandExecutor;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;

import java.util.HashMap;
import java.util.List;

public class FindCommand extends AsyncCommand
{
	public FindCommand(IScheduler scheduler, PlotTagRepository tagRepository)
	{
		super("find", "Search for plots with a given tag", "runsafe.creative.tag.find", scheduler, "lookup");
		this.tagRepository = tagRepository;
	}

	@Override
	public String OnAsyncExecute(ICommandExecutor executor, HashMap<String, String> param)
	{
		List<String> hits = tagRepository.findPlots(param.get("lookup"));
		if (hits.size() > 20)
			return String.format("Too many hits (%d).", hits.size());
		return String.format("Found these plots: %s", Strings.join(hits, ", "));
	}

	private PlotTagRepository tagRepository;
}
