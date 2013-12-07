package no.runsafe.creativetoolbox.event;

import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.player.RunsafeCustomEvent;

public class PlotMembershipGrantedEvent extends RunsafeCustomEvent
{
	public PlotMembershipGrantedEvent(IPlayer player, String plot)
	{
		super(player, "region.member.added");
		this.plot = plot;
	}

	@Override
	public String getData()
	{
		return plot;
	}

	private final String plot;
}

