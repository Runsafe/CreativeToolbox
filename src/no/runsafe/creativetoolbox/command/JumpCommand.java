package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.PlotList;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.EnumArgument;
import no.runsafe.framework.api.command.argument.IArgument;
import no.runsafe.framework.api.command.player.PlayerAsyncCallbackCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class JumpCommand extends PlayerAsyncCallbackCommand<JumpCommand.Sudo>
{
	public enum JumpKinds
	{
		Approved,
		Unapproved
	}

	protected JumpCommand(IScheduler scheduler, PlotFilter plotFilter, ApprovedPlotRepository approval)
	{
		super("jump", "Find a random plot of a given kind", "runsafe.creative.teleport.random", scheduler, new EnumArgument("kind", JumpKinds.values(), true));
		this.plotFilter = plotFilter;
		this.approval = approval;
	}

	@Override
	public Sudo OnAsyncExecute(RunsafePlayer executor, Map<String, String> params)
	{
		List<String> approved = approval.getApprovedPlots();
		Sudo target = new Sudo();
		target.player = executor;
		if(params.get("kind").equals(JumpKinds.Approved.name()))
		{
			int r = rng.nextInt(approved.size());
			plotList.set(executor, approved);
			plotList.wind(executor, approved.get(r));
			target.command = String.format("creativetoolbox teleport %s", approved.get(r));
			return target;
		}
		List<String> plots = plotFilter.getFiltered();
		ArrayList<String> result = new ArrayList<String>();
		for (String value : plots)
			if (!approved.contains(value))
				result.add(value);
		int r = rng.nextInt(result.size());
		plotList.set(executor, result);
		plotList.wind(executor, result.get(r));
		target.command = String.format("creativetoolbox teleport %s", result.get(r));
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

	private PlotFilter plotFilter;
	private ApprovedPlotRepository approval;
	private Random rng = new Random();
	private PlotList plotList;
}
