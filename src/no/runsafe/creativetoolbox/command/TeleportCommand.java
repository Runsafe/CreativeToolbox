package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.database.PlotEntrance;
import no.runsafe.creativetoolbox.database.PlotEntranceRepository;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.command.RunsafePlayerCommand;
import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;

public class TeleportCommand extends RunsafePlayerCommand {

	public TeleportCommand(PlotEntranceRepository entranceRepository, IConfiguration config) {
		super("teleport", null, "plotname");
		repository = entranceRepository;
		this.config = config;
	}

	@Override
	public String requiredPermission() {
		return "runsafe.creative.teleport";
	}

	@Override
	public String OnExecute(RunsafePlayer executor, String[] args) {
		RunsafeLocation target = null;
		String plot = getArg("plotname");
		RunsafeWorld world = RunsafeServer.Instance.getWorld(config.getConfigValueAsString("world"));
		PlotEntrance entrance = repository.get(plot);
		if(entrance != null)
			target = entrance.getLocation();

		if(target == null) {
			if(RunsafePlugin.Instances.containsKey("RunsafeWorldGuardBridge"))
				target = RunsafePlugin.Instances.get("RunsafeWorldGuardBridge")
						.getComponent(WorldGuardInterface.class).getRegionLocation(world, plot);

			if(target != null) {
				target.setX(target.getX() + 0.5);
				target.setZ(target.getZ() + 0.5);
				target.setPitch(-1.65f);
				target.setYaw(137.55f);
				while(world.getBlockAt(target).canPassThrough() && target.getY() > 60)
					target.setY(target.getY() - 1);
				target.setY(target.getY() + 2);
			}
		}

		if(target == null)
			return "Plot not found";

		executor.teleport(target);
		return null;
	}

	PlotEntranceRepository repository;
	IConfiguration config;
}
