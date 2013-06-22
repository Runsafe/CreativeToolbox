package no.runsafe.creativetoolbox.event;

import no.runsafe.framework.minecraft.event.player.RunsafeCustomEvent;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

public class PlotMembershipGrantedEvent extends RunsafeCustomEvent
{
	public PlotMembershipGrantedEvent(RunsafePlayer player, String plot)
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

