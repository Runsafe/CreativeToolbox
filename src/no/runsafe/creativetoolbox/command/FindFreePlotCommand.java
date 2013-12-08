package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlayerTeleport;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.player.PlayerAsyncCallbackCommand;
import no.runsafe.framework.api.player.IPlayer;

import java.util.List;
import java.util.Map;
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
	public PlayerTeleport OnAsyncExecute(IPlayer executor, Map<String, String> parameters)
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

	private ILocation getCandidate()
	{
		List<ILocation> options = manager.getFreePlotEntrances();
		if (options == null || options.size() < 1)
			return null;
		if (options.size() == 1)
			return options.get(0);
		else
			return options.get(rng.nextInt(options.size() - 1));
	}

	private final PlotManager manager;
	private final Random rng = new Random();
}
