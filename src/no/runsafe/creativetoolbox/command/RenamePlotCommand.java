package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.api.command.ExecutableCommand;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.RequiredArgument;

public class RenamePlotCommand extends ExecutableCommand
{
	public RenamePlotCommand(PlotManager manager)
	{
		super(
			"renamePlot", "Renames a plot in creative.", "runsafe.creative.renameplot",
			new RequiredArgument("CurrentPlotName"), new RequiredArgument("NewPlotName")
		);
		this.manager = manager;
	}

	@Override
	public String OnExecute(ICommandExecutor executor, IArgumentList parameters)
	{
		String currentPlotName = parameters.getRequired("CurrentPlotName");
		String newPlotName = parameters.getRequired("NewPlotName");

		if (manager.renamePlot(currentPlotName, newPlotName))
			return "&aPlot %s Renamed to %s.";
		else
			return "&cPlot could not be renamed.";
	}

	PlotManager manager;
}