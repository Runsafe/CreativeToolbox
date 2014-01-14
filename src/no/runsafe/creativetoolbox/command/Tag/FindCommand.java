package no.runsafe.creativetoolbox.command.Tag;

import no.runsafe.creativetoolbox.PlotList;
import no.runsafe.creativetoolbox.database.PlotTagRepository;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.AsyncCommand;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.RequiredArgument;
import no.runsafe.framework.api.player.IPlayer;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class FindCommand extends AsyncCommand
{
	public FindCommand(IScheduler scheduler, PlotTagRepository tagRepository, PlotList plotList)
	{
		super("find", "Search for plots with a given tag", "runsafe.creative.tag.find", scheduler, new RequiredArgument("lookup"));
		this.tagRepository = tagRepository;
		this.plotList = plotList;
	}

	@Override
	public String OnAsyncExecute(ICommandExecutor executor, IArgumentList param)
	{
		List<String> hits = tagRepository.findPlots(param.get("lookup"));
		if (hits.isEmpty())
			return String.format("No plots have been tagged with %s..", param.get("lookup"));
		if (executor instanceof IPlayer)
			plotList.set((IPlayer) executor, hits);
		if (hits.size() > 20)
			return String.format("Found %d plots:Too many hits to list.", hits.size());
		return String.format("Found %d plots: %s", hits.size(), StringUtils.join(hits, ", "));
	}

	private final PlotTagRepository tagRepository;
	private final PlotList plotList;
}
