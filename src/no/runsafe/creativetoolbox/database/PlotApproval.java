package no.runsafe.creativetoolbox.database;

import java.time.Instant;

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

	public Instant getApproved()
	{
		return approved;
	}

	public void setApproved(Instant value)
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
	private Instant approved;
	private String approvedBy;
}
