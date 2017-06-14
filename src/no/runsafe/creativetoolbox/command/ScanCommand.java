package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.entity.IEntity;
import no.runsafe.framework.api.player.IPlayer;

import java.util.HashMap;

public class ScanCommand extends PlayerCommand
{
	public ScanCommand(PlotManager manager)
	{
		super("scan", "list number of items and mobs in world.", "runsafe.creative.scan.items");
		this.manager = manager;
	}

	@Override
	public String OnExecute(IPlayer executor, IArgumentList parameters)
	{
		HashMap<String, Integer> counts = new HashMap<>();
		IWorld world = manager.getWorld();
		if (world == null)
			return "Creative world not found!";

		int count = 0;
		for (IEntity entity : world.getEntities())
		{
			if (entity instanceof IPlayer)
				continue;
			String name = entity.getEntityType().getName();
			if (!counts.containsKey(name))
				counts.put(name, 1);
			else
				counts.put(name, counts.get(name) + 1);
			count++;
		}
		StringBuilder results = new StringBuilder(String.format("%d items found:\n", count));
		for (String name : counts.keySet())
			results.append(String.format("  %s: %d.\n", name, counts.get(name)));
		return results.toString();
	}

	private final PlotManager manager;
}
