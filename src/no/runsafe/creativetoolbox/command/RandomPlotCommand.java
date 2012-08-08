package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.framework.command.RunsafePlayerCommand;
import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomPlotCommand extends RunsafePlayerCommand implements IConfigurationChanged
{
	public RandomPlotCommand(
		IConfiguration configuration,
		WorldGuardInterface worldGuardInterface,
		PlotFilter filter,
		CreativeToolboxCommand ctCommand)
	{
		super("randomplot", null);
		config = configuration;
		worldGuard = worldGuardInterface;
		plotFilter = filter;
		rng = new Random();
		this.command = ctCommand;
	}

	@Override
	public String requiredPermission()
	{
		return "runsafe.creative.teleport";
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args)
	{
		if (!worldGuard.serverHasWorldGuard())
			return "Could not find WorldGuard!";
		if (getWorld() == null)
			return "No world..";
		List<String> plots = plotFilter.apply(new ArrayList<String>(worldGuard.getAllRegionsWithOwnersInWorld(getWorld()).keySet()));
		int r = rng.nextInt(plots.size());
		command.Execute(executor, new String[]{"teleport", plots.get(r)});
		return null;
	}

	@Override
	public void OnConfigurationChanged()
	{
		world = RunsafeServer.Instance.getWorld(config.getConfigValueAsString("world"));
	}

	public RunsafeWorld getWorld()
	{
		if (world == null)
			world = RunsafeServer.Instance.getWorld(config.getConfigValueAsString("world"));
		return world;
	}

	private RunsafeWorld world;
	private IConfiguration config;
	private WorldGuardInterface worldGuard;
	private PlotFilter plotFilter;
	private Random rng;
	private CreativeToolboxCommand command;
}
