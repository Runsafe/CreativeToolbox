package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.database.PlotTagRepository;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.OptionalArgument;
import no.runsafe.framework.api.command.player.PlayerAsyncCallbackCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomPlotCommand extends PlayerAsyncCallbackCommand<RandomPlotCommand.Sudo>
{
	public RandomPlotCommand(PlotFilter filter, IScheduler scheduler, PlotTagRepository tagRepository)
	{
		super("randomplot", "teleport to a random plot.", "runsafe.creative.teleport.random", scheduler, new OptionalArgument("tag"));
		plotFilter = filter;
		this.tagRepository = tagRepository;
		rng = new Random();
	}

	@Override
	public Sudo OnAsyncExecute(RunsafePlayer executor, Map<String, String> parameters)
	{
		if (plotFilter.getWorld() == null)
			return null;
		List<String> plots;
		if (parameters.containsKey("tag"))
		{
			console.debugFine("Optional argument tag detected: %s", parameters.get("tag"));
			plots = tagRepository.findPlots(parameters.get("tag"));
			if (plots.isEmpty())
			{
				executor.sendColouredMessage("&cSorry, found no plots tagged \"%s\".", parameters.get("tag"));
				return null;
			}
		}
		else
			plots = plotFilter.getFiltered();
		int r = rng.nextInt(plots.size());
		Sudo target = new Sudo();
		target.player = executor;
		target.command = String.format("creativetoolbox teleport %s", plots.get(r));
		return target;
	}

	@Override
	public void SyncPostExecute(Sudo result)
	{
		if (result != null)
			result.player.getRawPlayer().performCommand(result.command);
	}

	class Sudo
	{
		public RunsafePlayer player;
		public String command;
	}

	private final PlotTagRepository tagRepository;
	private final PlotFilter plotFilter;
	private final Random rng;
}
