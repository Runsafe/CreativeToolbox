package no.runsafe.creativetoolbox.command;

import no.runsafe.framework.command.ICommand;
import no.runsafe.framework.command.RunsafeCommand;

import java.util.Collection;

public class CreativeToolboxCommand extends RunsafeCommand
{
	public CreativeToolboxCommand()
	{
		super("creativetoolbox");
	}

	@Override
	public String getDescription()
	{
		return "A collection of tools for use in a minecraft creative world.";
	}
}
