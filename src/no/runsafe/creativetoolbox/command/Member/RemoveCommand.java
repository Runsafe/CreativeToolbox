package no.runsafe.creativetoolbox.command.Member;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.database.PlotMemberRepository;
import no.runsafe.creativetoolbox.event.PlotMembershipRevokedEvent;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.Player;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.worldguardbridge.IRegionControl;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class RemoveCommand extends PlayerAsyncCommand
{
	public RemoveCommand(IScheduler scheduler, PlotFilter filter, IRegionControl worldGuard, PlotMemberRepository memberRepository)
	{
		super("remove", "Remove a member from the plot you are standing in.", "runsafe.creative.member.remove", scheduler, new Player().require());
		plotFilter = filter;
		worldGuardInterface = worldGuard;
		this.memberRepository = memberRepository;
	}

	@Override
	public String OnAsyncExecute(IPlayer executor, IArgumentList parameters)
	{
		IPlayer member = parameters.getValue("player");

		if (member == null)
			return null;

		List<String> targetPlots = plotFilter.apply(worldGuardInterface.getRegionsAtLocation(executor.getLocation()));
		List<String> ownedRegions = worldGuardInterface.getOwnedRegions(executor, plotFilter.getWorld());
		if (targetPlots == null || targetPlots.isEmpty())
			return "&cNo region defined at your location!";
		List<String> results = new ArrayList<>();
		for (String region : targetPlots)
		{
			if (ownedRegions.contains(region) || executor.hasPermission("runsafe.creative.member.override"))
			{
				if (worldGuardInterface.removeMemberFromRegion(plotFilter.getWorld(), region, member))
				{
					memberRepository.removeMember(region, member);
					results.add(String.format("%s &awas successfully removed from the plot %s.", member.getPrettyName(), region));
					new PlotMembershipRevokedEvent(member, region).Fire();
				}
				else
					results.add(String.format("&cCould not remove &r%s &cfrom the plot %s.", member.getPrettyName(), region));
			}
			else
				results.add(String.format("&cYou do not appear to be an owner of %s.", region));
		}
		return StringUtils.join(results, "\n");
	}

	private final IRegionControl worldGuardInterface;
	private final PlotFilter plotFilter;
	private final PlotMemberRepository memberRepository;
}
