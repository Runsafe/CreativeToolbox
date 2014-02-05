package no.runsafe.creativetoolbox.command;

import net.minecraft.server.v1_7_R1.ItemStack;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.internal.wrapper.ObjectUnwrapper;
import no.runsafe.framework.minecraft.Enchant;
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

		ItemStack raw = ObjectUnwrapper.getMinecraft(brush);
		if (raw == null)
			executor.sendColouredMessage("It's null..");
		else
			executor.sendColouredMessage(raw.getClass().getName());


		/*NBTTagCompound tag = brush.getTagCompound();

		if (tag == null)
			tag = new NBTTagCompound();

		tag.set("ench", new NBTTagList());
		tag.set("cbox.paintbrush", new NBTTagByte((byte) 1));
		brush.setTagCompound(tag);

		NBTTagCompound newTag = brush.getTagCompound();
		if (newTag == null)
			executor.sendColouredMessage("It's null..");*/

		executor.give(brush);
		return "&aConjured a paintbrush! Left click to select a paint block, right click to paint with it!";
	}
}
