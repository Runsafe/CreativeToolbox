package no.runsafe.creativetoolbox.database;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import no.runsafe.framework.api.database.IDatabase;
import no.runsafe.framework.api.database.IValue;
import no.runsafe.framework.api.database.Repository;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlotMemberRepository extends Repository
{
	public PlotMemberRepository(IDatabase database)
	{
		this.database = database;
	}

	@Override
	public String getTableName()
	{
		return "creative_plot_member";
	}

	public void addMember(String plot, String player, boolean isOwner)
	{
		database.Execute(
			"INSERT INTO creative_plot_member (`plot`,`player`,`owner`) VALUES (?,?,?)" +
				"ON DUPLICATE KEY UPDATE owner=VALUES(owner)",
			plot, player, isOwner ? 1 : 0
		);
	}

	public void removeMember(String region, String member)
	{
		database.Execute("DELETE FROM creative_plot_member WHERE `plot`=? AND `player`=?", region, member);
	}

	public List<String> getMembers(String plot, boolean owners, boolean members)
	{
		if (!owners && !members)
			return null;

		List<IValue> data;
		if (owners && members)
			data = database.QueryColumn("SELECT `player` FROM creative_plot_member WHERE plot=?", plot);
		else
			data = database.QueryColumn("SELECT `player` FROM creative_plot_member WHERE plot=? AND owner=?", plot, owners ? 1 : 0);

		if (data == null || data.isEmpty())
			return null;
		return Lists.transform(data, new Function<IValue, String>()
		{
			@Override
			public String apply(@Nullable IValue player)
			{
				if (player == null)
					return null;
				return player.String();
			}
		});
	}

	public List<String> getPlots(String player, boolean owner, boolean member)
	{
		if (!owner && !member)
			return null;

		List<IValue> data;
		if (owner && member)
			data = database.QueryColumn("SELECT DISTINCT `plot` FROM creative_plot_member WHERE player=?", player);
		else
			data = database.QueryColumn("SELECT DISTINCT `plot` FROM creative_plot_member WHERE player=? AND owner=?", player, owner ? 1 : 0);

		if (data == null || data.isEmpty())
			return null;
		return Lists.transform(data, new Function<IValue, String>()
		{
			@Override
			public String apply(@Nullable IValue plot)
			{
				if (plot == null)
					return null;
				return plot.String();
			}
		});
	}

	public int cleanStaleData()
	{
		return database.Update("DELETE FROM creative_plot_member WHERE `plot` NOT IN (SELECT `plot` FROM creative_plot_log)");
	}

	@Override
	public HashMap<Integer, List<String>> getSchemaUpdateQueries()
	{
		HashMap<Integer, List<String>> revisions = new HashMap<Integer, List<String>>();
		List<String> sql = new ArrayList<String>();
		sql.add(
			"CREATE TABLE creative_plot_member (" +
				"`plot` VARCHAR(250)," +
				"`player` VARCHAR(250)," +
				"`owner` INT," +
				"PRIMARY KEY(`plot`,`player`)" +
				");"
		);
		revisions.put(1, sql);
		return revisions;
	}

	private final IDatabase database;
}
