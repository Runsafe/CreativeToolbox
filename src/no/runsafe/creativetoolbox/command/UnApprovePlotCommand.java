package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotApproval;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.IServer;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.worldguardbridge.IRegionControl;

public class UnApprovePlotCommand extends PlayerAsyncCommand
{
	public UnApprovePlotCommand(IScheduler scheduler, IRegionControl worldGuard, PlotFilter filter, PlotManager manager, ApprovedPlotRepository approval, IServer server)
	{
		super("unapprove", "Removes a previously granted approval.", "runsafe.creative.approval.revoke", scheduler, new PlotArgument(filter, worldGuard, server));
		this.manager = manager;
		plotFilter = filter;
		this.approval = approval;
	}

	@Override
	public String OnAsyncExecute(IPlayer executor, IArgumentList parameters)
	{
		String plot;
		if (parameters.getValue("plotname").equals("."))
		{
			if (manager.isInWrongWorld(executor))
				return "You cannot use that here.";

			plot = manager.getCurrentRegionFiltered(executor);
		}
		else
			plot = plotFilter.apply(parameters.get("plotname"));
		if (plot == null)
			return "You cannot unapprove that plot.";

		PlotApproval approved = approval.get(plot);
		if (approved == null)
			return String.format("The plot %s was not approved.", plot);

		approval.delete(approved);
		return String.format("The plot '%s' previously approved by %s has been unapproved.", plot, approved.getApprovedBy());
	}

	private final PlotManager manager;
	private final PlotFilter plotFilter;
	private final ApprovedPlotRepository approval;
}
