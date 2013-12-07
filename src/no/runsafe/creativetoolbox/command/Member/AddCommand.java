package no.runsafe.creativetoolbox.command.Member;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.database.PlotMemberBlacklistRepository;
import no.runsafe.creativetoolbox.database.PlotMemberRepository;
import no.runsafe.creativetoolbox.event.PlotMembershipGrantedEvent;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.PlayerArgument;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.player.RunsafeAmbiguousPlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddCommand extends PlayerAsyncCommand
{
	public AddCommand(IScheduler scheduler, PlotFilter filter, WorldGuardInterface worldGuard, PlotMemberRepository members, PlotMemberBlacklistRepository blacklistRepository)
	{
		super("add", "Add a member to the plot you are standing in", "runsafe.creative.member.add", scheduler, new PlayerArgument());
		plotFilter = filter;
		worldGuardInterface = worldGuard;
		this.members = members;
		this.blacklistRepository = blacklistRepository;
	}

	@Override
	public String OnAsyncExecute(IPlayer executor, Map<String, String> parameters)
	{
		IPlayer member = RunsafeServer.Instance.getPlayer(parameters.get("player"));

		if (member == null)
			return "&cUnable to find player.";

		if (member instanceof RunsafeAmbiguousPlayer)
			return member.toString();

		if (blacklistRepository.isBlacklisted(member))
			return String.format("The player %s has been blacklisted from being added as a member.", member.getPrettyName());

		List<String> target = plotFilter.apply(worldGuardInterface.getRegionsAtLocation(executor.getLocation()));
		List<String> ownedRegions = worldGuardInterface.getOwnedRegions(executor, plotFilter.getWorld());
		if (target == null || target.size() == 0)
			return "No region defined at your location!";
		List<String> results = new ArrayList<String>();
		for (String region : target)
		{
			if (ownedRegions.contains(region) || executor.hasPermission("runsafe.creative.member.override"))
			{
				if (worldGuardInterface.addMemberToRegion(plotFilter.getWorld(), region, member))
				{
					members.addMember(region, member.getName(), false);
					results.add(String.format("%s was successfully added to the plot %s.", member.getPrettyName(), region));
					new PlotMembershipGrantedEvent(member, region).Fire();
				}
				else
					results.add(String.format("Could not add %s to the plot %s.", member.getPrettyName(), region));
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
	private final PlotMemberRepository members;
	private final PlotMemberBlacklistRepository blacklistRepository;
}
