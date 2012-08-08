package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.database.PlotEntrance;
import no.runsafe.creativetoolbox.database.PlotEntranceRepository;
import no.runsafe.framework.command.RunsafePlayerCommand;
import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;

public class TeleportCommand extends RunsafePlayerCommand implements IConfigurationChanged
{

	public TeleportCommand(
		PlotEntranceRepository entranceRepository,
		IConfiguration config,
		WorldGuardInterface worldGuard,
		PlotFilter filter
	)
	{
		super("teleport", null, "plotname");
		repository = entranceRepository;
		this.config = config;
		worldGuardInterface = worldGuard;
		plotFilter = filter;
	}

	@Override
	public String requiredPermission()
	{
		return "runsafe.creative.teleport";
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args)
	{
		if (!worldGuardInterface.serverHasWorldGuard())
			return "Unable to find WorldGuard!";

		RunsafeLocation target = null;
		String plot = plotFilter.apply(getArg("plotname"));
		PlotEntrance entrance = repository.get(plot);
		if (entrance != null)
			target = entrance.getLocation();

		if (target == null)
		{
			target = worldGuardInterface.getRegionLocation(getWorld(), plot);

			if (target != null)
			{
				target.setX(target.getX() + 0.5);
				target.setZ(target.getZ() + 0.5);
				target.setPitch(-1.65f);
				target.setYaw(137.55f);
				while (getWorld().getBlockAt(target).canPassThrough() && target.getY() > 60)
					target.setY(target.getY() - 1);
				target.setY(target.getY() + 2);
			}
		}

		if (target == null)
			return "Plot not found";

		executor.teleport(target);
		return String.format("Teleported to '%s'", plot);
	}

	@Override
	public void OnConfigurationChanged()
	{
		world = RunsafeServer.Instance.getWorld(config.getConfigValueAsString("world"));
	}

	public RunsafeWorld getWorld()
	{
		if(world == null)
			world = RunsafeServer.Instance.getWorld(config.getConfigValueAsString("world"));
		return world;
	}

	PlotEntranceRepository repository;
	IConfiguration config;
	WorldGuardInterface worldGuardInterface;
	RunsafeWorld world;
	PlotFilter plotFilter;
}
