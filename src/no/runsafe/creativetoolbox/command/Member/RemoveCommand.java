package no.runsafe.creativetoolbox.command.Member;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.framework.command.player.PlayerAsyncCommand;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.timer.IScheduler;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.HashMap;
import java.util.List;

public class RemoveCommand extends PlayerAsyncCommand
{
	public RemoveCommand(IScheduler scheduler, PlotFilter filter, WorldGuardInterface worldGuard)
	{
		super("remove", "Remove a member from the plot you are standing in.", "runsafe.creative.member.remove", scheduler, "player");
		plotFilter = filter;
		worldGuardInterface = worldGuard;
	}

	@Override
	public String OnAsyncExecute(RunsafePlayer executor, HashMap<String, String> parameters, String[] arguments)
	{
		RunsafePlayer member = RunsafeServer.Instance.getPlayer(parameters.get("player"));
		List<String> target = plotFilter.apply(worldGuardInterface.getRegionsAtLocation(executor.getLocation()));
		List<String> ownedRegions = worldGuardInterface.getOwnedRegions(executor, plotFilter.getWorld());
		if (target == null || target.size() == 0)
			return "No region defined at your location!";
		StringBuilder results = new StringBuilder();
		for (String region : target)
		{
			if (ownedRegions.contains(region))
			{
				if (worldGuardInterface.removeMemberFromRegion(plotFilter.getWorld(), region, member))
					results.append(String.format("Player %s was successfully removed from your plot %s.", member.getName(), region));
				else
					results.append(String.format("Could not remove player %s from your plot %s.", member.getName(), region));
			}
			else
				results.append(String.format("You do not appear to be an owner of %s.", region));
		}
		return results.toString();
	}

	private final WorldGuardInterface worldGuardInterface;
	private final PlotFilter plotFilter;
}
