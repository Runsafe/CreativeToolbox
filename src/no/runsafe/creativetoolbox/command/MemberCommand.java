package no.runsafe.creativetoolbox.command;

import no.runsafe.framework.command.RunsafeCommand;

public class MemberCommand extends RunsafeCommand
{
	public MemberCommand()
	{
		super("member");
	}

	@Override
	public String getDescription()
	{
		return "Tools to handle plot membership";
	}
}
