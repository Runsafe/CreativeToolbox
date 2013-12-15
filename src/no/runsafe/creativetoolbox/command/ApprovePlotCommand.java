package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.PlotApproval;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;

import java.util.Map;

public class ApprovePlotCommand extends PlayerAsyncCommand
{
	public ApprovePlotCommand(PlotFilter filter, PlotManager plotManager, IScheduler scheduler, PlotArgument plotName)
	{
		super("approve", "exempts a plot from the old plots command.", "runsafe.creative.approval.set", scheduler, plotName);
		plotFilter = filter;
		manager = plotManager;
	}

	@Override
	public String OnAsyncExecute(IPlayer executor, Map<String, String> parameters)
	{
		if (manager.isInWrongWorld(executor))
			return "You cannot use that here.";

		String plot;
		if (parameters.get("plotname").equals("."))
			plot = manager.getCurrentRegionFiltered(executor);
		else
			plot = plotFilter.apply(parameters.get("plotname"));
		if (plot == null)
			return "You cannot approve that plot.";

		PlotApproval approval = manager.approve(executor.getName(), plot);
		if (approval == null)
			return String.format("Failed approving plot %s!", plot);

		return null;
	}

	private final PlotFilter plotFilter;
	private final PlotManager manager;
}
