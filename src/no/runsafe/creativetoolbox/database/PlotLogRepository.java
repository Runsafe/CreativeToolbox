package no.runsafe.creativetoolbox.database;

import no.runsafe.framework.api.database.IDatabase;
import no.runsafe.framework.api.database.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlotLogRepository extends Repository
{
	public PlotLogRepository(IDatabase database)
	{
		this.database = database;
	}

	public boolean log(String plot, String claimer)
	{
		return database.Execute("INSERT INTO `creative_plot_log` (`plot`,`claimer`,`claimed`) VALUES (?, ?, NOW())" +
			"ON DUPLICATE KEY UPDATE `claimer`=VALUES(`claimer`), `claimed`=VALUES(`claimed`)",
			plot, claimer);
	}

	@Override
	public String getTableName()
	{
		return "creative_plot_log";
	}

	@Override
	public HashMap<Integer, List<String>> getSchemaUpdateQueries()
	{
		HashMap<Integer, List<String>> revisions = new HashMap<Integer, List<String>>();
		List<String> sql = new ArrayList<String>();
		sql.add(
			"CREATE TABLE `creative_plot_log` (" +
				"`plot` varchar(255) not null," +
				"`claimer` varchar(255) not null," +
				"`claimed` datetime NOT NULL," +
				"PRIMARY KEY(`plot`)" +
				")"
		);
		revisions.put(1, sql);
		return revisions;
	}

	private final IDatabase database;
}
