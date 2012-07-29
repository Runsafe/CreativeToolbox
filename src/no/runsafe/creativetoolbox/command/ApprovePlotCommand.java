package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotApproval;
import no.runsafe.framework.command.RunsafePlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;

import java.sql.Timestamp;
import java.util.Date;

public class ApprovePlotCommand extends RunsafePlayerCommand {
	public ApprovePlotCommand(ApprovedPlotRepository approvalRepository) {
		super("approve", null, "plotname");
		repository = approvalRepository;
	}

	@Override
	public String requiredPermission() {
		return "runsafe.creative.approval.set";
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args) {
		String plot = getArg("plotname");

		PlotApproval approval = new PlotApproval();
		approval.setApproved(new Date());
		approval.setApprovedBy(executor.getName());
		approval.setName(plot);
		repository.persist(approval);
		approval = repository.get(plot);
		if(approval == null)
			return String.format("Failed approving plot %s!", plot);
		return String.format("Plot %s has been approved.", plot);
	}

	ApprovedPlotRepository repository;
}
