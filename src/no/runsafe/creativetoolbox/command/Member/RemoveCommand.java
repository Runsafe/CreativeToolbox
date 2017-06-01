package no.runsafe.creativetoolbox.command.Member;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.database.PlotMemberRepository;
import no.runsafe.creativetoolbox.event.PlotMembershipRevokedEvent;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.IServer;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.Player;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.worldguardbridge.IRegionControl;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RemoveCommand extends PlayerAsyncCommand
{
	public RemoveCommand(IScheduler scheduler, PlotFilter filter, IRegionControl worldGuard, PlotMemberRepository memberRepository, IServer server)
	{
		super("remove", "Remove a member from the plot you are standing in.", "runsafe.creative.member.remove", scheduler, new Player().require());
		plotFilter = filter;
		worldGuardInterface = worldGuard;
		this.memberRepository = memberRepository;
		this.server = server;
	}

	@Override
	public String OnAsyncExecute(IPlayer executor, IArgumentList parameters)
	{
		List<String> targets = plotFilter.apply(worldGuardInterface.getRegionsAtLocation(executor.getLocation()));
		if (targets == null || targets.isEmpty())
			return "No region defined at your location!";

		List<String> ownedRegions = worldGuardInterface.getOwnedRegions(executor, plotFilter.getWorld());
		List<String> results = new ArrayList<String>();
		String filter = ((IPlayer) parameters.getValue("player")).getName().toLowerCase();
		for (String region : targets)
		{
			if (ownedRegions.contains(region) || executor.hasPermission("runsafe.creative.member.override"))
			{
				Set<String> members = worldGuardInterface.getMembers(executor.getWorld(), region);
				for (String member : members)
					if (member.toLowerCase().startsWith(filter))
					{
						IPlayer target = server.getOfflinePlayerExact(member);
						assert (target != null);
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
		return StringUtils.join(results, "\n");
	}

	private final IRegionControl worldGuardInterface;
	private final PlotFilter plotFilter;
	private final PlotMemberRepository memberRepository;
	private final IServer server;
}
