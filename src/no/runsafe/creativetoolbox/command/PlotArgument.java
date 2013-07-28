package no.runsafe.creativetoolbox.command;

import com.google.common.collect.Lists;
import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.framework.api.command.argument.ITabComplete;
import no.runsafe.framework.api.command.argument.RequiredArgument;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.List;

public class PlotArgument extends RequiredArgument implements ITabComplete
{
	public PlotArgument(PlotFilter filter, WorldGuardInterface worldGuard)
	{
		super("plotname");
		this.filter = filter;
		this.worldGuard = worldGuard;
	}

	@Override
	public List<String> getAlternatives(RunsafePlayer executor, String arg)
	{
		if (!arg.contains("_"))
			return Lists.newArrayList();
		RunsafePlayer player = RunsafeServer.Instance.getOfflinePlayerExact(arg.substring(0, arg.lastIndexOf('_')));
		return filter.apply(worldGuard.getOwnedRegions(player, filter.getWorld()));
	}

	private final PlotFilter filter;
	private final WorldGuardInterface worldGuard;
}
