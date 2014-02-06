package no.runsafe.creativetoolbox;

import no.runsafe.framework.api.block.IBlock;
import no.runsafe.framework.api.event.player.IPlayerLeftClickBlockEvent;
import no.runsafe.framework.api.event.player.IPlayerRightClickBlock;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerClickEvent;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;

import java.util.HashMap;

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
		if (blockType != null && usingItem != null && isPaintbrush(usingItem))
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
		String playerName = player.getName();
		return paintbrushes.containsKey(playerName) ? paintbrushes.get(playerName) : Item.Unavailable.Air;
	}

	private void setPaintbrushBlock(IPlayer player, Item setItem)
	{
		paintbrushes.put(player.getName(), setItem);
		player.sendColouredMessage("&ePaintbrush block changed: " + setItem.getName());
	}

	private final HashMap<String, Item> paintbrushes = new HashMap<String, Item>(0);
}
