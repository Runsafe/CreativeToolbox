package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotApproval;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.AsyncCommand;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.worldguardbridge.IRegionControl;

import java.util.List;

public class CheckApprovalCommand extends AsyncCommand
{
	public CheckApprovalCommand(
		ApprovedPlotRepository approvalRepository,
		PlotFilter filter,
		IRegionControl worldGuard,
		IScheduler scheduler,
		PlotArgument plotName
	)
	{
		super("checkapproval", "find out who approved a plot.", "runsafe.creative.approval.read", scheduler, plotName);
		repository = approvalRepository;
		plotFilter = filter;
		worldGuardInterface = worldGuard;
	}

	@Override
	public String OnAsyncExecute(ICommandExecutor executor, IArgumentList parameters)
	{
		String plot = parameters.getValue("plotname");
		if (plot.equals(".") && executor instanceof IPlayer)
		{
			List<String> here = plotFilter.apply(worldGuardInterface.getRegionsAtLocation(((IPlayer) executor).getLocation()));
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
	private final IRegionControl worldGuardInterface;
}
