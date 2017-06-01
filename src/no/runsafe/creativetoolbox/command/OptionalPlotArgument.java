package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.framework.api.command.argument.ITabComplete;
import no.runsafe.framework.api.command.argument.OptionalArgument;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.api.server.IPlayerProvider;
import no.runsafe.worldguardbridge.IRegionControl;

import java.util.List;

public class OptionalPlotArgument extends OptionalArgument implements ITabComplete
{
	public OptionalPlotArgument(PlotFilter filter, IRegionControl worldGuard, IPlayerProvider playerProvider)
	{
		super("plotname");
		this.filter = filter;
		this.worldGuard = worldGuard;
		this.playerProvider = playerProvider;
	}

	@Override
	public List<String> getAlternatives(IPlayer executor, String arg)
	{
		if (!arg.contains("_"))
			return null;
		IPlayer player = playerProvider.getOfflinePlayerExact(arg.substring(0, arg.lastIndexOf('_')));
		return filter.apply(worldGuard.getOwnedRegions(player, filter.getWorld()));
	}

	private final PlotFilter filter;
	private final IRegionControl worldGuard;
	private final IPlayerProvider playerProvider;
}

