package no.runsafe.creativetoolbox.command.Member;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.database.PlotMemberRepository;
import no.runsafe.creativetoolbox.event.PlotMembershipRevokedEvent;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RemoveCommand extends PlayerAsyncCommand
{
	public RemoveCommand(IScheduler scheduler, PlotFilter filter, WorldGuardInterface worldGuard, PlotMemberRepository memberRepository)
	{
		super("remove", "Remove a member from the plot you are standing in.", "runsafe.creative.member.remove", scheduler, "player");
		plotFilter = filter;
		worldGuardInterface = worldGuard;
		this.memberRepository = memberRepository;
	}

	@Override
	public String OnAsyncExecute(RunsafePlayer executor, Map<String, String> parameters)
	{
		List<String> targets = plotFilter.apply(worldGuardInterface.getRegionsAtLocation(executor.getLocation()));
		if (targets == null || targets.isEmpty())
			return "No region defined at your location!";

		List<String> ownedRegions = worldGuardInterface.getOwnedRegions(executor, plotFilter.getWorld());
		List<String> results = new ArrayList<String>();
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
							memberRepository.removeMember(region, member);
							results.add(String.format("%s was successfully removed from the plot %s.", target.getPrettyName(), region));
							new PlotMembershipRevokedEvent(target, region).Fire();
						}
						else
							results.add(String.format("Could not remove %s from the plot %s.", target.getPrettyName(), region));
					}
			}
			else
				results.add(String.format("You do not appear to be an owner of %s.", region));
		}
		if (results.isEmpty())
			return null;
		return Strings.join(results, "\n");
	}

	private final WorldGuardInterface worldGuardInterface;
	private final PlotFilter plotFilter;
	private final PlotMemberRepository memberRepository;
}
