package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.event.InteractEvents;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.HashMap;

public class ExtendCommand extends PlayerCommand
{
	public ExtendCommand(PlotManager manager, InteractEvents interact)
	{
		super("extend", "Extends a plot", "runsafe.creative.extend");
		this.manager = manager;
		interactEvents = interact;
	}

	@Override
	public String OnExecute(RunsafePlayer player, HashMap<String, String> stringStringHashMap)
	{
		String target = manager.getCurrentRegionFiltered(player);
		if (target == null)
			return "There is no plot defined here.";
		interactEvents.startPlotExtension(player, target);
		return String.format("Now right click the ground in the plot you wish to extend to include in %s.", target);
	}

	private final PlotManager manager;
	private final InteractEvents interactEvents;
}
