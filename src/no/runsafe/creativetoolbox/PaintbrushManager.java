package no.runsafe.creativetoolbox;

import net.minecraft.server.v1_7_R1.NBTTagCompound;
import no.runsafe.framework.api.block.IBlock;
import no.runsafe.framework.api.event.player.IPlayerLeftClickBlockEvent;
import no.runsafe.framework.api.event.player.IPlayerRightClickBlock;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerClickEvent;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;

public class PaintbrushManager implements IPlayerLeftClickBlockEvent, IPlayerRightClickBlock
{
	@Override
	public void OnPlayerLeftClick(RunsafePlayerClickEvent event)
	{
		if (isPaintbrush(event.getPlayer().getItemInHand()))
		{
			IBlock block = event.getBlock();
			setPaintbrushBlock(event.getPlayer(),  block == null ? Item.Unavailable.Air : block.getMaterial());
			event.cancel();
		}
	}

	@Override
	public boolean OnPlayerRightClick(IPlayer player, RunsafeMeta usingItem, IBlock targetBlock)
	{
		Item blockType = getPaintbrushBlock(player);
		if (blockType != null)
		{
			targetBlock.set(blockType);
			return false;
		}
		return true;
	}

	private boolean isPaintbrush(RunsafeMeta item)
	{
		return item != null && item.getTagCompound().hasKey("cbox.paintbrush");
	}

	private Item getPaintbrushBlock(IPlayer player)
	{
		RunsafeMeta item = player.getItemInHand();
		if (item != null && isPaintbrush(item))
		{
			NBTTagCompound tag = item.getTagCompound();
			return Item.get(tag.getString("cbox.paintbrush.block"));
		}
		return null;
	}

	private void setPaintbrushBlock(IPlayer player, Item setItem)
	{
		RunsafeMeta item = player.getItemInHand();
		if (item != null)
		{
			NBTTagCompound tag = item.getTagCompound();
			tag.setString("cbox.paintbrush.block", setItem.getName());
			item.setTagCompound(tag);
		}

		player.sendColouredMessage("&ePaintbrush block changed: " + setItem.getName());
	}
}
