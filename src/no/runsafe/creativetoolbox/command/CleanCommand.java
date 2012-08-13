package no.runsafe.creativetoolbox.command;

import no.runsafe.framework.command.RunsafeAsyncPlayerCommand;
import no.runsafe.framework.command.RunsafePlayerCommand;
import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.server.entity.RunsafeEntity;
import no.runsafe.framework.server.event.player.RunsafePlayerInteractEvent;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.timer.IScheduler;

import java.util.HashMap;
import java.util.List;

public class CleanCommand extends RunsafePlayerCommand
{
	public CleanCommand(IConfiguration configuration)
	{
		super("clean", null);
		config = configuration;
	}

	@Override
	public String requiredPermission()
	{
		return "runsafe.creative.clean";
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args)
	{
		HashMap<String, Integer> counts = new HashMap<String, Integer>();
		List<String> noClean = config.getConfigValueAsList("clean.ignore");
		int count = 0;
		for (RunsafeEntity entity : executor.getWorld().getEntities())
		{
			String name =	entity.getRaw().getClass().getSimpleName();
			boolean clean = true;
			if(entity instanceof RunsafePlayer)
				continue;
			if(args.length > 0)
			{
				clean = false;
				for(String filter : args)
				{
					if(name.contains(filter))
					{
						clean = true;
						break;
					}
				}
			}
			else
			{
				for (String filter : noClean)
				{
					if (name.contains(filter))
					{
						clean = false;
						break;/**/
					}
				}
			}
			if(!clean)
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

	private final IConfiguration config;
}
