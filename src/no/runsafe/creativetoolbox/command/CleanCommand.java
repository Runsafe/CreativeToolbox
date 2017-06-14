package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.OptionalArgument;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.entity.IEntity;
import no.runsafe.framework.api.player.IPlayer;

import java.util.HashMap;

public class CleanCommand extends PlayerCommand
{
	public CleanCommand(ConfigurationManager config, PlotManager manager)
	{
		super("clean", "Remove items and mobs from the world", "runsafe.creative.clean", new OptionalArgument("filter"));
		this.config = config;
		this.manager = manager;
	}

	@Override
	public String OnExecute(IPlayer executor, IArgumentList parameters)
	{
		HashMap<String, Integer> counts = new HashMap<>();
		String[] arguments = new String[0];
		if (parameters.getValue("filter") != null)
			arguments = ((String) parameters.getValue("filter")).split("\\s+");
		int count = 0;
		for (IEntity entity : manager.getWorld().getEntities())
		{
			String name = entity.getEntityType().getName();
			boolean clean = true;
			if (entity instanceof IPlayer)
				continue;
			if (arguments.length > 0)
			{
				clean = false;
				for (String filter : arguments)
				{
					if (name.contains(filter))
					{
						clean = true;
						break;
					}
				}
			}
			else
			{
				for (String filter : config.getCleanFilter())
				{
					if (name.contains(filter))
					{
						clean = false;
						break;
					}
				}
			}
			if (!clean)
				continue;
			if (!counts.containsKey(name))
				counts.put(name, 1);
			else
				counts.put(name, counts.get(name) + 1);
			count++;
			entity.remove();
		}
		StringBuilder results = new StringBuilder(String.format("%d items cleaned:\n", count));
		for (String name : counts.keySet())
			results.append(String.format("  %s: %d.\n", name, counts.get(name)));
		return results.toString();
	}

	private final ConfigurationManager config;
	private final PlotManager manager;
}
