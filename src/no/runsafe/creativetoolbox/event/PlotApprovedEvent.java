package no.runsafe.creativetoolbox.event;

import no.runsafe.creativetoolbox.database.PlotApproval;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.player.RunsafeCustomEvent;

import java.util.HashMap;
import java.util.Map;

public class PlotApprovedEvent extends RunsafeCustomEvent
{
	public PlotApprovedEvent(IPlayer owner, PlotApproval approval, int approvedPlots)
	{
		super(null, "creative.plot.approved");
		this.owner = owner;
		this.approval = approval;
		this.approvedPlots = approvedPlots;
	}

	public PlotApproval getApproval()
	{
		return approval;
	}

	public IPlayer getOwner()
	{
		return owner;
	}

	public int getApprovedPlots()
	{
		return approvedPlots;
	}

	@Override
	public Map<String, String> getData()
	{
		Map<String, String> data = new HashMap<>();
		data.put("plot", approval.getName());
		data.put("approved_by", approval.getApprovedBy());
		data.put("owner", owner.getName());
		data.put("approved_plots", String.valueOf(approvedPlots));
		return data;
	}

	private final PlotApproval approval;
	private final IPlayer owner;
	private final int approvedPlots;
}
