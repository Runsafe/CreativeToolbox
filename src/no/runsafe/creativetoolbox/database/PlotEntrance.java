package no.runsafe.creativetoolbox.database;

import no.runsafe.framework.server.RunsafeLocation;

public class PlotEntrance
{
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public RunsafeLocation getLocation()
	{
		return location;
	}

	public void setLocation(RunsafeLocation location)
	{
		this.location = location;
	}

	private RunsafeLocation location;
	private String name;
}
