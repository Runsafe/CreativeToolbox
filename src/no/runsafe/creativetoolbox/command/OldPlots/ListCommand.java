package no.runsafe.creativetoolbox.command.OldPlots;

import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.command.RunsafeAsyncCommand;
import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.timer.IScheduler;

import java.util.Map;

public class ListCommand extends RunsafeAsyncCommand
{
	public ListCommand(PlotManager manager, IScheduler scheduler, IConfiguration config)
	{
		super("list", scheduler);
		this.manager = manager;
		this.config = config;
	}

	@Override
	public String getDescription()
	{
		return "list old plots that may be removed.";
	}

	@Override
	public String requiredPermission()
	{
		return "runsafe.creative.scan.old-plots";
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] strings)
	{
		int max_listed = config.getConfigValueAsInt("max_listed");
		if(executor != null)
		{
			manager.clearOldPlotWorkList(executor);
			manager.setOldPlotPointer(executor, null);
		}
		Map<String, String> hits = manager.getOldPlots();
		int n = 0;
		StringBuilder result = new StringBuilder();
		for (Map.Entry<String, String> item : hits.entrySet())
		{
			if (executor == null || n < max_listed)
			{
				result.append(String.format("%s - %s\n", item.getKey(), item.getValue()));
				n++;
			}
		}
		if (result.length() == 0)
			return "No old plots found.";
		if (executor == null || n == hits.size())
			result.append(String.format("%d plots found", hits.size()));
		else
			result.append(String.format("Showing %d of %d plots found", n, hits.size()));

		return result.toString();
	}

	private final PlotManager manager;
	private final IConfiguration config;
}
