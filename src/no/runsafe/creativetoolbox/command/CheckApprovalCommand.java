package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotApproval;
import no.runsafe.framework.command.RunsafeCommand;
import no.runsafe.framework.server.player.RunsafePlayer;

public class CheckApprovalCommand extends RunsafeCommand {
	public CheckApprovalCommand(ApprovedPlotRepository approvalRepository) {
		super("checkapproval", null, "plotname");
		repository = approvalRepository;
	}

	@Override
	public String requiredPermission() {
		return "runsafe.creative.approval.read";
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args) {
		String plot = getArg("plotname");
		PlotApproval approval = repository.get(plot);
		if(approval == null)
			return String.format("Plot %s has not been approved.", plot);

		return String.format("Plot %s was approved by %s at %s", plot, approval.getApprovedBy(), approval.getApproved());
	}

	private ApprovedPlotRepository repository;
}
