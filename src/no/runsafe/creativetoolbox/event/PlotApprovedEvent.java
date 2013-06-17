package no.runsafe.creativetoolbox.event;

import no.runsafe.creativetoolbox.database.PlotApproval;
import no.runsafe.framework.minecraft.event.player.RunsafeCustomEvent;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.HashMap;
import java.util.Map;

public class PlotApprovedEvent extends RunsafeCustomEvent
{
	public PlotApprovedEvent(RunsafePlayer player, String owner, PlotApproval approval)
	{
		super(player, "creative.plot.approved");
		this.owner = owner;
		this.approval = approval;
	}

	public PlotApproval getApproval()
	{
		return approval;
	}

	public String getOwner()
	{
		return owner;
	}

	@Override
	public Map<String, String> getData()
	{
		Map<String, String> data = new HashMap<String, String>();
		data.put("plot", approval.getName());
		data.put("approved_by", approval.getApprovedBy());
		data.put("owner", owner);
		return data;
	}

	private final PlotApproval approval;
	private final String owner;
}
