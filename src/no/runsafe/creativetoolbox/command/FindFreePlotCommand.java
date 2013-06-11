package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlayerTeleport;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.player.PlayerAsyncCallbackCommand;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class FindFreePlotCommand extends PlayerAsyncCallbackCommand<PlayerTeleport>
{
	public FindFreePlotCommand(
		PlotManager manager,
		IScheduler scheduler
	)
	{
		super("findfreeplot", "teleport to a random empty plot.", "runsafe.creative.teleport.free", scheduler);
		this.manager = manager;
	}

	@Override
	public PlayerTeleport OnAsyncExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		PlayerTeleport target = new PlayerTeleport();
		target.who = executor;
		do
		{
			target.location = getCandidate();
		}
		while (target.location != null && manager.plotIsTaken(target.location));

		if (target.location == null)
			target.message = "Sorry, no free plots could be located.";
		return target;
	}

	public void SyncPostExecute(PlayerTeleport result)
	{
		if (result.location != null)
			result.who.teleport(result.location);
		result.who.sendColouredMessage(result.message);
	}

	private RunsafeLocation getCandidate()
	{
		List<RunsafeLocation> options = manager.getFreePlotEntrances();
		if (options == null || options.size() < 1)
			return null;
		if (options.size() == 1)
			return options.get(0);
		else
			return options.get(rng.nextInt(options.size() - 1));
	}

	private final PlotManager manager;
	private final HashMap<String, RunsafeLocation> warpTo = new HashMap<String, RunsafeLocation>();
	private final Random rng = new Random();
}
