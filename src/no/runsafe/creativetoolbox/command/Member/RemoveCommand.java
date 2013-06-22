package no.runsafe.creativetoolbox.command.Member;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.event.PlotMembershipRevokedEvent;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class RemoveCommand extends PlayerAsyncCommand
{
	public RemoveCommand(IScheduler scheduler, PlotFilter filter, WorldGuardInterface worldGuard)
	{
		super("remove", "Remove a member from the plot you are standing in.", "runsafe.creative.member.remove", scheduler, "player");
		plotFilter = filter;
		worldGuardInterface = worldGuard;
	}

	@Override
	public String OnAsyncExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		List<String> targets = plotFilter.apply(worldGuardInterface.getRegionsAtLocation(executor.getLocation()));
		if (targets == null || targets.isEmpty())
			return "No region defined at your location!";

		List<String> ownedRegions = worldGuardInterface.getOwnedRegions(executor, plotFilter.getWorld());
		StringBuilder results = new StringBuilder();
		for (String region : targets)
		{
			if (ownedRegions.contains(region) || executor.hasPermission("runsafe.creative.member.override"))
			{
				Set<String> members = worldGuardInterface.getMembers(executor.getWorld(), region);
				for (String member : members)
					if (member.toLowerCase().startsWith(parameters.get("player").toLowerCase()))
					{
						RunsafePlayer target = RunsafeServer.Instance.getOfflinePlayerExact(member);
						if (worldGuardInterface.removeMemberFromRegion(plotFilter.getWorld(), region, target))
						{
							results.append(String.format("Player %s was successfully removed from the plot %s.", member, region));
							new PlotMembershipRevokedEvent(target, region).Fire();
						}
						else
							results.append(String.format("Could not remove player %s from the plot %s.", member, region));
					}
			}
			else
				results.append(String.format("You do not appear to be an owner of %s.", region));
		}
		return results.toString();
	}

	private final WorldGuardInterface worldGuardInterface;
	private final PlotFilter plotFilter;
}
