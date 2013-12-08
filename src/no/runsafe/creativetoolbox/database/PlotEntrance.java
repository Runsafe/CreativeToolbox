package no.runsafe.creativetoolbox.database;

import no.runsafe.framework.api.ILocation;

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

	public ILocation getLocation()
	{
		return location;
	}

	public void setLocation(ILocation location)
	{
		this.location = location;
	}

	private ILocation location;
	private String name;
}
