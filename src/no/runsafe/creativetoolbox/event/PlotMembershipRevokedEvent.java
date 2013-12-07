package no.runsafe.creativetoolbox.event;

import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.player.RunsafeCustomEvent;

public class PlotMembershipRevokedEvent extends RunsafeCustomEvent
{
	public PlotMembershipRevokedEvent(IPlayer player, String plot)
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
