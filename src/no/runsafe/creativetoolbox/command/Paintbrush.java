package no.runsafe.creativetoolbox.command;

import net.minecraft.server.v1_7_R1.NBTTagByte;
import net.minecraft.server.v1_7_R1.NBTTagCompound;
import net.minecraft.server.v1_7_R1.NBTTagList;
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
		brush.setDisplayName("Paintbrush");

		NBTTagCompound tag = new NBTTagCompound();
		tag.set("ench", new NBTTagList());
		tag.set("cbox.paintbrush", new NBTTagByte((byte) 1));
		tag.setString("cbox.paintbrush", "AIR"); // Default air.

		executor.give(brush.cloneWithNewCompound(tag));
		return "&aConjured a paintbrush! Left click to select a paint block, right click to paint with it!";
	}
}
