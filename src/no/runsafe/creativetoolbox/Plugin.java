package no.runsafe.creativetoolbox;

import no.runsafe.creativetoolbox.command.*;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotEntranceRepository;
import no.runsafe.creativetoolbox.events.InteractEvents;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.command.RunsafeCommand;
import no.runsafe.framework.configuration.IConfigurationFile;
import no.runsafe.framework.event.IPluginEnabled;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.io.InputStream;

public class Plugin extends RunsafePlugin implements IConfigurationFile
{
	@Override
	protected void PluginSetup()
	{
		addComponent(Instances.get("RunsafeWorldGuardBridge").getComponent(WorldGuardInterface.class));
		addComponent(PlotFilter.class);
		addComponent(PlotManager.class);
		addComponent(ApprovedPlotRepository.class);
		addComponent(PlotEntranceRepository.class);
		addComponent(InteractEvents.class);

		RunsafeCommand toolbox = new CreativeToolboxCommand();
		addComponent(toolbox);

		toolbox.addSubCommand(getInstance(ApprovePlotCommand.class));
		toolbox.addSubCommand(getInstance(CheckApprovalCommand.class));
		toolbox.addSubCommand(getInstance(OldPlotsCommand.class));
		toolbox.addSubCommand(getInstance(SetEntranceCommand.class));
		toolbox.addSubCommand(getInstance(TeleportCommand.class));
		toolbox.addSubCommand(getInstance(ScanCommand.class));
		toolbox.addSubCommand(getInstance(CleanCommand.class));
		toolbox.addSubCommand(getInstance(ListCommand.class));
		toolbox.addSubCommand(getInstance(RandomPlotCommand.class));
		toolbox.addSubCommand(getInstance(DeleteHereCommand.class));
		toolbox.addSubCommand(getInstance(FindFreePlotCommand.class));
	}

	@Override
	public String getConfigurationPath()
	{
		return "plugins/CreativeToolbox/config.yml";
	}

	@Override
	public InputStream getDefaultConfiguration()
	{
		return getResource("defaults.yml");
	}
}
