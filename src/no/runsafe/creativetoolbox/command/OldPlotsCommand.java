package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.framework.command.RunsafeCommand;
import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.timer.IScheduler;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.HashMap;
import java.util.Map;

public class OldPlotsCommand extends RunsafeCommand
{
	public OldPlotsCommand()
	{
		super("old");
	}

	@Override
	public String getDescription()
	{
		return "Tool to handle old plots";
	}
}
