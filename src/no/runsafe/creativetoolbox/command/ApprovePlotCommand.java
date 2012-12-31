package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotApproval;
import no.runsafe.framework.command.RunsafeAsyncPlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.timer.IScheduler;
import org.joda.time.DateTime;

public class ApprovePlotCommand extends RunsafeAsyncPlayerCommand
{
	public ApprovePlotCommand(ApprovedPlotRepository approvalRepository, PlotFilter filter, IScheduler scheduler)
	{
		super("approve", scheduler, "plotname");
		repository = approvalRepository;
		plotFilter = filter;
	}

	@Override
	public String requiredPermission()
	{
		return "runsafe.creative.approval.set";
	}

	@Override
	public String getDescription()
	{
		return "exempts a plot from the old plots command.";
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args)
	{
		String plot = plotFilter.apply(getArg("plotname"));
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
}
