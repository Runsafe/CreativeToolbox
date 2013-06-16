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
import no.runsafe.framework.api.command.Command;
import no.runsafe.worldeditbridge.WorldEditInterface;
import no.runsafe.worldguardbridge.WorldGuardInterface;

public class Plugin extends RunsafeConfigurablePlugin
{
	@Override
	protected void PluginSetup()
	{
		addComponent(getFirstPluginAPI(WorldGuardInterface.class));
		addComponent(getFirstPluginAPI(WorldEditInterface.class));
		addComponent(PlotFilter.class);
		addComponent(PlotManager.class);
		addComponent(ApprovedPlotRepository.class);
		addComponent(PlotEntranceRepository.class);
		addComponent(InteractEvents.class);
		addComponent(PlotCalculator.class);

		Command toolbox = new Command("creativetoolbox", "A collection of tools for use in a minecraft creative world.", null);
		addComponent(toolbox);

		Command oldPlots = new Command("old", "Tool to handle old plots", null);
		oldPlots.addSubCommand(getInstance(no.runsafe.creativetoolbox.command.OldPlots.ListCommand.class));
		oldPlots.addSubCommand(getInstance(NextCommand.class));
		oldPlots.addSubCommand(getInstance(PreviousCommand.class));
		toolbox.addSubCommand(oldPlots);

		Command member = new Command("member", "Tools to handle plot membership", null);
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
		toolbox.addSubCommand(getInstance(SelectCommand.class));
		toolbox.addSubCommand(getInstance(RegenerateCommand.class));
	}
}
