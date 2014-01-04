package no.runsafe.creativetoolbox.database;

import no.runsafe.framework.api.database.IDatabase;
import no.runsafe.framework.api.database.IRow;
import no.runsafe.framework.api.database.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class PlotLogRepository extends Repository
{
	public PlotLogRepository(IDatabase database)
	{
		this.database = database;
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean log(String plot, String claimer)
	{
		return database.execute("INSERT INTO `creative_plot_log` (`plot`,`claimer`,`claimed`) VALUES (?, ?, NOW())" +
			"ON DUPLICATE KEY UPDATE `claimer`=VALUES(`claimer`), `claimed`=VALUES(`claimed`)",
			plot, claimer);
	}

	public void delete(String plot)
	{
		database.execute("DELETE FROM `creative_plot_log` WHERE `plot`=?", plot);
	}

	public List<String> getPlots()
	{
		return database.queryStrings("SELECT `plot` FROM `creative_plot_log`");
	}

	public String getClaim(String plot)
	{
		IRow data = database.queryRow("SELECT * FROM creative_plot_log WHERE `plot`=?", plot);
		if (data.isEmpty())
			return null;
		return String.format("%s by %s", data.DateTime("claimed").toString("dd.MM.yyyy"), data.String("claimer"));
	}

	@Override
	public String getTableName()
	{
		return "creative_plot_log";
	}

	@Override
	public HashMap<Integer, List<String>> getSchemaUpdateQueries()
	{
		HashMap<Integer, List<String>> revisions = new LinkedHashMap<Integer, List<String>>();
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
