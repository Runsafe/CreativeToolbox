package no.runsafe.creativetoolbox.fence;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;

public class FenceConfig implements IConfigurationChanged
{
	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		lowX = configuration.getConfigValueAsInt("fence.x");
		lowZ = configuration.getConfigValueAsInt("fence.y");
		highX = lowX + configuration.getConfigValueAsInt("fence.w");
		highZ = lowZ + configuration.getConfigValueAsInt("fence.h");
		lowY = configuration.getConfigValueAsInt("fence.b");
		highY = configuration.getConfigValueAsInt("fence.t");
		world = configuration.getConfigValueAsWorld("world");
	}

	public int getLowX()
	{
		return lowX;
	}

	public int getLowZ()
	{
		return lowZ;
	}

	public int getHighX()
	{
		return highX;
	}

	public int getHighZ()
	{
		return highZ;
	}

	public int getLowY()
	{
		return lowY;
	}

	public int getHighY()
	{
		return highY;
	}

	public IWorld getWorld()
	{
		return world;
	}

	private int lowX;
	private int lowZ;
	private int highX;
	private int highZ;
	private int lowY;
	private int highY;
	private IWorld world;
}
