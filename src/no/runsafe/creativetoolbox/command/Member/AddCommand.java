package no.runsafe.creativetoolbox.command.Member;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.event.PlotMembershipGrantedEvent;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.player.RunsafeAmbiguousPlayer;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.HashMap;
import java.util.List;

public class AddCommand extends PlayerAsyncCommand
{
	public AddCommand(IScheduler scheduler, PlotFilter filter, WorldGuardInterface worldGuard)
	{
		super("add", "Add a member to the plot you are standing in", "runsafe.creative.member.add", scheduler, "player");
		plotFilter = filter;
		worldGuardInterface = worldGuard;
	}

	@Override
	public String OnAsyncExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		RunsafePlayer member = RunsafeServer.Instance.getPlayer(parameters.get("player"));
		if (member instanceof RunsafeAmbiguousPlayer)
			return member.toString();
		List<String> target = plotFilter.apply(worldGuardInterface.getRegionsAtLocation(executor.getLocation()));
		List<String> ownedRegions = worldGuardInterface.getOwnedRegions(executor, plotFilter.getWorld());
		if (target == null || target.size() == 0)
			return "No region defined at your location!";
		StringBuilder results = new StringBuilder();
		for (String region : target)
		{
			if (ownedRegions.contains(region) || executor.hasPermission("runsafe.creative.member.override"))
			{
				if (worldGuardInterface.addMemberToRegion(plotFilter.getWorld(), region, member))
				{
					results.append(String.format("Player %s was successfully added to your plot %s.", member.getName(), region));
					new PlotMembershipGrantedEvent(member, region).Fire();
				}
				else
					results.append(String.format("Could not add player %s to your plot %s.", member.getName(), region));
			}
			else
				results.append(String.format("You do not appear to be an owner of %s.", region));
		}
		return results.toString();
	}

	private final WorldGuardInterface worldGuardInterface;
	private final PlotFilter plotFilter;
}
