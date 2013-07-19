package no.runsafe.creativetoolbox.command.OldPlots;

import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.HashMap;
import java.util.Map;

public class ListCommand extends PlayerAsyncCommand
{
	public ListCommand(PlotManager manager, IScheduler scheduler, IConfiguration config)
	{
		super("list", "list old plots that may be removed.", "runsafe.creative.scan.old-plots", scheduler);
		this.manager = manager;
		this.config = config;
	}

	@Override
	public String OnAsyncExecute(RunsafePlayer executor, Map<String, String> parameters)
	{
		int max_listed = config.getConfigValueAsInt("max_listed");
		if (executor != null)
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
