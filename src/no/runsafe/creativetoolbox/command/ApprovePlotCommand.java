package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotApproval;
import no.runsafe.framework.command.player.PlayerAsyncCommand;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.timer.IScheduler;
import org.joda.time.DateTime;

import java.util.HashMap;

public class ApprovePlotCommand extends PlayerAsyncCommand
{
	public ApprovePlotCommand(
		ApprovedPlotRepository approvalRepository,
		PlotFilter filter,
		PlotManager plotManager,
		IScheduler scheduler)
	{
		super("approve", "exempts a plot from the old plots command.", "runsafe.creative.approval.set", scheduler, "plotname");
		repository = approvalRepository;
		plotFilter = filter;
		manager = plotManager;
	}

	@Override
	public String OnAsyncExecute(RunsafePlayer executor, HashMap<String, String> parameters, String[] arguments)
	{
		String plot;
		if (parameters.get("plotname").equals("."))
			plot = manager.getCurrentRegionFiltered(executor);
		else
			plot = plotFilter.apply(parameters.get("plotname"));
		if (plot == null)
			return "You cannot approve that plot.";

		PlotApproval approval = new PlotApproval();
		approval.setApproved(DateTime.now());
		approval.setApprovedBy(executor.getName());
		approval.setName(plot);
		repository.persist(approval);
		approval = repository.get(plot);
		if (approval == null)
			return String.format("Failed approving plot %s!", plot);
		return String.format("Plot %s has been approved.", plot);
	}

	private final ApprovedPlotRepository repository;
	private final PlotFilter plotFilter;
	private final PlotManager manager;
}
