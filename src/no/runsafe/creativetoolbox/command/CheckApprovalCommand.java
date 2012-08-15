package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotApproval;
import no.runsafe.framework.command.RunsafeAsyncCommand;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.timer.IScheduler;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.List;

public class CheckApprovalCommand extends RunsafeAsyncCommand
{
	public CheckApprovalCommand(
		ApprovedPlotRepository approvalRepository,
		PlotFilter filter,
		WorldGuardInterface worldGuard,
		IScheduler scheduler
	)
	{
		super("checkapproval", scheduler, "plotname");
		repository = approvalRepository;
		plotFilter = filter;
		worldGuardInterface = worldGuard;
	}

	@Override
	public String requiredPermission()
	{
		return "runsafe.creative.approval.read";
	}

	@Override
	public String getDescription()
	{
		return "find out who approved a plot.";
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args)
	{
		String plot = getArg("plotname");
		if (plot.equals("."))
		{
			List<String> here = plotFilter.apply(worldGuardInterface.getRegionsAtLocation(executor.getLocation()));
			if (here == null || here.size() == 0)
				return "No plot here";
			plot = here.get(0);
		}
		PlotApproval approval = repository.get(plot);
		if (approval == null)
			return String.format("Plot %s has not been approved.", plot);

		return String.format("Plot %s was approved by %s at %s", plot, approval.getApprovedBy(), approval.getApproved());
	}

	private final ApprovedPlotRepository repository;
	private final PlotFilter plotFilter;
	private final WorldGuardInterface worldGuardInterface;
}
