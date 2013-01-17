package no.runsafe.creativetoolbox.database;

import org.joda.time.DateTime;

public class PlotApproval
{
	public String getName()
	{
		return name;
	}

	public void setName(String value)
	{
		name = value;
	}

	public DateTime getApproved()
	{
		return approved;
	}

	public void setApproved(DateTime value)
	{
		approved = value;
	}

	public String getApprovedBy()
	{
		return approvedBy;
	}

	public void setApprovedBy(String value)
	{
		approvedBy = value;
	}

	private String name;
	private DateTime approved;
	private String approvedBy;
}
