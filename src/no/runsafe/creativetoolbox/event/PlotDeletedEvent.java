package no.runsafe.creativetoolbox.event;

import no.runsafe.framework.minecraft.event.player.RunsafeCustomEvent;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

public class PlotDeletedEvent extends RunsafeCustomEvent
{
	public PlotDeletedEvent(RunsafePlayer player, String plotName)
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
