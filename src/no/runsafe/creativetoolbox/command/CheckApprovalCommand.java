package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotApproval;
import no.runsafe.framework.command.AsyncCommand;
import no.runsafe.framework.server.ICommandExecutor;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.timer.IScheduler;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.HashMap;
import java.util.List;

public class CheckApprovalCommand extends AsyncCommand
{
	public CheckApprovalCommand(
		ApprovedPlotRepository approvalRepository,
		PlotFilter filter,
		WorldGuardInterface worldGuard,
		IScheduler scheduler
	)
	{
		super("checkapproval", "find out who approved a plot.", "runsafe.creative.approval.read", scheduler, "plotname");
		repository = approvalRepository;
		plotFilter = filter;
		worldGuardInterface = worldGuard;
	}

	@Override
	public String OnAsyncExecute(ICommandExecutor executor, HashMap<String, String> parameters, String[] arguments)
	{
		String plot = parameters.get("plotname");
		if (plot.equals(".") && executor instanceof RunsafePlayer)
		{
			List<String> here = plotFilter.apply(worldGuardInterface.getRegionsAtLocation(((RunsafePlayer) executor).getLocation()));
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
