package no.runsafe.creativetoolbox.command;

import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;

public class Paintbrush extends PlayerCommand
{
	public Paintbrush()
	{
		super("paintbrush", "Conjure a paint-brush for creative use!", "runsafe.creative.paintbrush");
	}

	@Override
	public String OnExecute(IPlayer executor, IArgumentList parameters)
	{
		RunsafeMeta brush = Item.Materials.BlazeRod.getItem();
		brush.setDisplayName("§bMagical Paintbrush");
		brush.addLore("Tool: Paintbrush");
		executor.give(brush);
		return "&aConjured a paintbrush! Left click to select a paint block, right click to paint with it!";
	}
}
