package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.event.InteractEvents;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IPlayer;

public class ExtendCommand extends PlayerCommand
{
	public ExtendCommand(PlotManager manager, InteractEvents interact)
	{
		super("extend", "Extends a plot", "runsafe.creative.extend");
		this.manager = manager;
		interactEvents = interact;
	}

	@Override
	public String OnExecute(IPlayer player, IArgumentList stringStringHashMap)
	{
		if (manager.isInWrongWorld(player))
			return "&cYou cannot use that here.";

		String target = manager.getCurrentRegionFiltered(player);
		if (target == null)
			return "&cThere is no plot defined here.";
		interactEvents.startPlotExtension(player, target);
		return String.format("Now right click the ground in the plot you wish to extend to include in %s.", target);
	}

	private final PlotManager manager;
	private final InteractEvents interactEvents;
}
