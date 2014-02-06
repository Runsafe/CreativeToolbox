package no.runsafe.creativetoolbox.command;

import net.minecraft.server.v1_7_R1.ItemStack;
import net.minecraft.server.v1_7_R1.NBTTagByte;
import net.minecraft.server.v1_7_R1.NBTTagCompound;
import net.minecraft.server.v1_7_R1.NBTTagList;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.internal.wrapper.ObjectUnwrapper;
import no.runsafe.framework.internal.wrapper.ObjectWrapper;
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
			return "&cError trying to do that!";

		NBTTagCompound tag = raw.getTag();
		if (tag == null) tag = new NBTTagCompound();

		tag.set("ench", new NBTTagList());
		tag.set("cbox.paintbrush", new NBTTagByte((byte) 1));
		raw.setTag(tag);

		executor.give(ObjectWrapper.convert(raw));
		return "&aConjured a paintbrush! Left click to select a paint block, right click to paint with it!";
	}
}
