package no.runsafe.creativetoolbox;

import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.api.vector.IPoint3D;
import no.runsafe.framework.api.vector.IRegion3D;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CreativePlot
{
	public CreativePlot(DateTime created, String claimedBy, String name)
	{
		this.created = created;
		this.claimedBy = claimedBy;
		this.name = name;
		members = new ArrayList<>();
		owners = new ArrayList<>();
	}

	public String getName()
	{
		return name;
	}

	public DateTime getCreated()
	{
		return created;
	}

	public String getClaimedBy()
	{
		return claimedBy;
	}

	public ILocation getEntrance()
	{
		return entrance;
	}

	public IRegion3D getBoundary()
	{
		return boundary;
	}

	public void setBoundary(IRegion3D boundary)
	{
		this.boundary = boundary;
	}

	public boolean isInside(IPoint3D point)
	{
		return boundary.contains(point);
	}

	public void setEntrance(ILocation entrance)
	{
		this.entrance = entrance;
	}

	public void setApproved(DateTime approved, String by)
	{
		this.approved = approved;
		this.approvedBy = by;
	}

	public DateTime getApproved()
	{
		return approved;
	}

	public String getApprovedBy()
	{
		return approvedBy;
	}

	public void addVote(int score)
	{
		votes++;
		voteScore += score;
	}

	public int getVotes()
	{
		return votes;
	}

	public void setVotes(int votes)
	{
		this.votes = votes;
	}

	public int getVoteScore()
	{
		return voteScore;
	}

	public void setVoteScore(int voteScore)
	{
		this.voteScore = voteScore;
	}

	public boolean isOwner(IPlayer player)
	{
		return owners.contains(player.getName());
	}

	public Collection<String> getOwners()
	{
		return Collections.unmodifiableList(owners);
	}

	public void addOwners(Collection<String> ownerList)
	{
		owners.addAll(ownerList);
	}

	public void addOwner(String owner)
	{
		owners.add(owner);
	}

	public void removeOwner(String owner)
	{
		if (owners.contains(owner))
			owners.remove(owner);
	}

	public boolean isMember(IPlayer player)
	{
		return members.contains(player.getName()) || owners.contains(player.getName());
	}

	public Collection<String> getMembers()
	{
		return Collections.unmodifiableList(members);
	}

	public void addMembers(Collection<String> memberList)
	{
		members.addAll(memberList);
	}

	public void addMember(String member)
	{
		members.add(member);
	}

	public void removeMember(String member)
	{
		if (members.contains(member))
			members.remove(member);
	}

	private DateTime approved;
	private String approvedBy;
	private ILocation entrance;
	private IRegion3D boundary;
	private int votes;
	private int voteScore;
	private final DateTime created;
	private final String claimedBy;
	private final String name;
	private final List<String> owners;
	private final List<String> members;
}
