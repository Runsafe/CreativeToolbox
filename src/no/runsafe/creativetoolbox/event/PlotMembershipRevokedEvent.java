package no.runsafe.creativetoolbox.event;

import no.runsafe.framework.minecraft.event.player.RunsafeCustomEvent;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

public class PlotMembershipRevokedEvent extends RunsafeCustomEvent
{
	public PlotMembershipRevokedEvent(RunsafePlayer player, String plot)
	{
		super(player, "region.member.removed");
		this.plot = plot;
	}

	@Override
	public String getData()
	{
		return plot;
	}

	private final String plot;
}
