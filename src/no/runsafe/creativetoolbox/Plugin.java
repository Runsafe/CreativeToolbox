package no.runsafe.creativetoolbox;

import no.runsafe.creativetoolbox.command.*;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotEntranceRepository;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.command.RunsafeCommand;
import no.runsafe.framework.configuration.IConfigurationFile;

import java.io.InputStream;

public class Plugin extends RunsafePlugin implements IConfigurationFile {
	@Override
	protected void PluginSetup() {
		addComponent(ApprovedPlotRepository.class);
		addComponent(PlotEntranceRepository.class);

		RunsafeCommand toolbox = new RunsafeCommand("creativetoolbox", null);
		addComponent(toolbox);

		toolbox.addSubCommand(getInstance(ApprovePlotCommand.class));
		toolbox.addSubCommand(getInstance(CheckApprovalCommand.class));
		toolbox.addSubCommand(getInstance(OldPlotsCommand.class));
		toolbox.addSubCommand(getInstance(SetEntranceCommand.class));
		toolbox.addSubCommand(getInstance(TeleportCommand.class));
		toolbox.addSubCommand(getInstance(ScanCommand.class));
		toolbox.addSubCommand(getInstance(CleanCommand.class));
		toolbox.addSubCommand(getInstance(ListCommand.class));
	}

	@Override
	public String getConfigurationPath() {
		return "plugins/CreativeToolbox/config.yml";
	}

	@Override
	public InputStream getDefaultConfiguration() {
		return getResource("defaults.yml");
	}
}
