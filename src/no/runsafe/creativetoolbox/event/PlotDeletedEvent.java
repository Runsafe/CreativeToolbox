package no.runsafe.creativetoolbox.event;

import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.player.RunsafeCustomEvent;

public class PlotDeletedEvent extends RunsafeCustomEvent
{
	public PlotDeletedEvent(IPlayer player, String plotName)
	{
		super(player, "creative.plot.deleted");
		this.plotName = plotName;
	}

	@Override
	public String getData()
	{
		return plotName;
	}

	private final String plotName;
}
