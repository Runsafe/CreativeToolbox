package no.runsafe.creativetoolbox.command;

import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.entity.RunsafeEntity;

import java.util.HashMap;
import java.util.Map;

public class ScanCommand extends PlayerCommand
{
	public ScanCommand()
	{
		super("scan", "list number of items and mobs in world.", "runsafe.creative.scan.items");
	}

	@Override
	public String OnExecute(IPlayer executor, Map<String, String> parameters)
	{
		HashMap<String, Integer> counts = new HashMap<String, Integer>();
		int count = 0;
		for (RunsafeEntity entity : executor.getWorld().getEntities())
		{
			if (entity instanceof IPlayer)
				continue;
			String name = entity.getRaw().getClass().getSimpleName();
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
}
