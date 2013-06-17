package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotApproval;
import no.runsafe.creativetoolbox.event.PlotApprovedEvent;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;
import org.joda.time.DateTime;

import java.util.HashMap;

public class ApprovePlotCommand extends PlayerAsyncCommand
{
	public ApprovePlotCommand(
		ApprovedPlotRepository approvalRepository,
		PlotFilter filter,
		PlotManager plotManager,
		IScheduler scheduler, WorldGuardInterface worldGuard)
	{
		super("approve", "exempts a plot from the old plots command.", "runsafe.creative.approval.set", scheduler, "plotname");
		repository = approvalRepository;
		plotFilter = filter;
		manager = plotManager;
		this.worldGuard = worldGuard;
	}

	@Override
	public String OnAsyncExecute(RunsafePlayer executor, HashMap<String, String> parameters)
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

		for(String owner : worldGuard.getOwners(executor.getWorld(), plot))
			new PlotApprovedEvent(executor, owner, approval).Fire();

		console.broadcastColoured("&6The creative plot &l%s&r&6 has been approved.", plot);
		return null;
	}

	private final ApprovedPlotRepository repository;
	private final PlotFilter plotFilter;
	private final PlotManager manager;
	private final WorldGuardInterface worldGuard;
}
