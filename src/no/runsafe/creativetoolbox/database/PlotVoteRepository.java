package no.runsafe.creativetoolbox.database;

import no.runsafe.framework.api.database.IDatabase;
import no.runsafe.framework.api.database.IRow;
import no.runsafe.framework.api.database.Repository;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlotVoteRepository extends Repository
{
	public PlotVoteRepository(IDatabase database)
	{
		this.database = database;
	}

	public boolean recordVote(RunsafePlayer player, String plot)
	{
		return database.Update(
			"INSERT INTO creative_plot_vote (`plot`, `player`, `rank`) VALUES (?, ?, ?)" +
				"ON DUPLICATE KEY UPDATE rank=VALUES(`rank`)",
			plot, player.getName(), StringUtils.join(player.getGroups(), ",")
		) > 0;
	}

	public int tally(String regionName)
	{
		IRow answer = database.QueryRow("SELECT COUNT(*) AS tally FROM creative_plot_vote WHERE `plot`=?", regionName);
		if (answer == null)
			return 0;
		return answer.Integer("tally");
	}

	@Override
	public String getTableName()
	{
		return "creative_plot_vote";
	}

	@Override
	public HashMap<Integer, List<String>> getSchemaUpdateQueries()
	{
		HashMap<Integer, List<String>> revisions = new HashMap<Integer, List<String>>();
		List<String> sql = new ArrayList<String>();
		sql.add(
			"CREATE TABLE creative_plot_vote (" +
				"`plot` varchar(255) NOT NULL," +
				"`player` varchar(255) NOT NULL," +
				"`rank` varchar(255) NOT NULL," +
				"PRIMARY KEY(`plot`,`player`)" +
				")"
		);
		revisions.put(1, sql);
		return revisions;
	}

	private final IDatabase database;
}
