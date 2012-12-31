package no.runsafe.creativetoolbox;

import no.runsafe.creativetoolbox.command.*;
import no.runsafe.creativetoolbox.command.Member.AddCommand;
import no.runsafe.creativetoolbox.command.Member.RemoveCommand;
import no.runsafe.creativetoolbox.command.OldPlots.NextCommand;
import no.runsafe.creativetoolbox.command.OldPlots.PreviousCommand;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotEntranceRepository;
import no.runsafe.creativetoolbox.events.InteractEvents;
import no.runsafe.framework.RunsafeConfigurablePlugin;
import no.runsafe.framework.command.RunsafeCommand;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.io.InputStream;

public class Plugin extends RunsafeConfigurablePlugin
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

		RunsafeCommand oldPlots = new OldPlotsCommand();
		oldPlots.addSubCommand(getInstance(no.runsafe.creativetoolbox.command.OldPlots.ListCommand.class));
		oldPlots.addSubCommand(getInstance(NextCommand.class));
		oldPlots.addSubCommand(getInstance(PreviousCommand.class));
		toolbox.addSubCommand(oldPlots);

		RunsafeCommand member = new RunsafeCommand("member");
		member.addSubCommand(getInstance(AddCommand.class));
		member.addSubCommand(getInstance(RemoveCommand.class));
		toolbox.addSubCommand(member);

		toolbox.addSubCommand(getInstance(ApprovePlotCommand.class));
		toolbox.addSubCommand(getInstance(CheckApprovalCommand.class));
		toolbox.addSubCommand(getInstance(SetEntranceCommand.class));
		toolbox.addSubCommand(getInstance(TeleportCommand.class));
		toolbox.addSubCommand(getInstance(ScanCommand.class));
		toolbox.addSubCommand(getInstance(CleanCommand.class));
		toolbox.addSubCommand(getInstance(ListCommand.class));
		toolbox.addSubCommand(getInstance(RandomPlotCommand.class));
		toolbox.addSubCommand(getInstance(DeleteHereCommand.class));
		toolbox.addSubCommand(getInstance(FindFreePlotCommand.class));
	}
}
