package no.runsafe.creativetoolbox.fence;

import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.event.player.IPlayerMove;
import no.runsafe.framework.api.player.IPlayer;

public class FenceHandler implements IPlayerMove
{
	public FenceHandler(FenceConfig config)
	{
		this.config = config;
	}

	@Override
	public boolean OnPlayerMove(IPlayer player, ILocation from, ILocation to)
	{
		return isWithinFence(to);
	}

	private boolean isWithinFence(ILocation location)
	{
		return location.getX() <= config.getHighX() &&
				location.getX() >= config.getLowX() &&
				location.getZ() <= config.getHighZ() &&
				location.getZ() >= config.getLowZ() &&
				location.getY() <= config.getHighY() &&
				location.getY() >= config.getLowY();
	}

	private final FenceConfig config;
}
