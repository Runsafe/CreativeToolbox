package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.PlotApproval;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;

public class ApprovePlotCommand extends PlayerAsyncCommand
{
	public ApprovePlotCommand(PlotFilter filter, PlotManager plotManager, IScheduler scheduler, PlotArgument plotName)
	{
		super("approve", "exempts a plot from the old plots command.", "runsafe.creative.approval.set", scheduler, plotName);
		plotFilter = filter;
		manager = plotManager;
	}

	@Override
	public String OnAsyncExecute(IPlayer executor, IArgumentList parameters)
	{
		if (manager.isInWrongWorld(executor))
			return "&cYou cannot use that here.";
		String plot = parameters.getValue("plotname");
		if (plot == null || plot.equals("."))
			plot = manager.getCurrentRegionFiltered(executor);
		else
			plot = plotFilter.apply(plot);
		if (plot == null)
			return "&cYou cannot approve that plot.";

		PlotApproval approval = manager.approve(executor.getName(), plot);
		if (approval == null)
			return String.format("&cFailed approving plot %s!", plot);

		return null;
	}

	private final PlotFilter plotFilter;
	private final PlotManager manager;
}
