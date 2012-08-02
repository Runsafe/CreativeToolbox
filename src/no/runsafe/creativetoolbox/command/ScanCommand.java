package no.runsafe.creativetoolbox.command;

import no.runsafe.framework.command.ICommand;
import no.runsafe.framework.command.RunsafePlayerCommand;
import no.runsafe.framework.server.entity.RunsafeEntity;
import no.runsafe.framework.server.entity.RunsafePainting;
import no.runsafe.framework.server.player.RunsafePlayer;

import java.util.Collection;
import java.util.HashMap;

public class ScanCommand extends RunsafePlayerCommand
{
	public ScanCommand()
	{
		super("scan", null);
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args)
	{
		HashMap<String, Integer> counts = new HashMap<String, Integer>();
		int count = 0;
		for(RunsafeEntity entity : executor.getWorld().getEntities())
		{
			String name =	entity.getRaw().getClass().getSimpleName();
			if(!counts.containsKey(name))
				counts.put(name, 1);
			else
				counts.put(name, counts.get(name) + 1);
			count++;
		}
		StringBuilder results = new StringBuilder(String.format("%d items found:\n", count));
		for(String name : counts.keySet())
			results.append(String.format("  %s: %d.\n", name, counts.get(name)));
		return results.toString();
	}
}
