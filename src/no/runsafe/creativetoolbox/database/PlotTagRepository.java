package no.runsafe.creativetoolbox.database;

import no.runsafe.framework.api.database.IDatabase;
import no.runsafe.framework.api.database.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlotTagRepository extends Repository
{
	public PlotTagRepository(IDatabase database)
	{
		this.database = database;
	}

	public List<String> getTags(String plot)
	{
		return database.queryStrings(
			"SELECT `tag` FROM creative_plot_tags WHERE name=?",
			plot
		);
	}

	public boolean addTag(String plot, String tag)
	{
		return !(tag == null || tag.isEmpty() || tag.trim().isEmpty()) && insertTag(plot, tag);
	}

	public boolean setTags(String plot, List<String> tags)
	{
		boolean success = database.execute("DELETE FROM creative_plot_tags WHERE `name`=?", plot);
		if (tags != null)
			for (String tag : tags)
				success = success && addTag(plot, tag);
		return success;
	}

	public List<String> findPlots(String tag)
	{
		return database.queryStrings(
			"SELECT `name` FROM creative_plot_tags WHERE `tag` LIKE ?",
			tag
		);
	}

	public List<String> getTaggedPlots()
	{
		return database.queryStrings("SELECT DISTINCT `name` FROM creative_plot_tags");
	}

	@Override
	public String getTableName()
	{
		return "creative_plot_tags";
	}

	@Override
	public HashMap<Integer, List<String>> getSchemaUpdateQueries()
	{
		HashMap<Integer, List<String>> revisions = new HashMap<Integer, List<String>>();
		List<String> sql = new ArrayList<String>();
		sql.add(
			"CREATE TABLE creative_plot_tags (" +
				"`name` VARCHAR(255)," +
				"`tag` VARCHAR(255)," +
				"PRIMARY KEY(`name`,`tag`)" +
				")"
		);
		revisions.put(1, sql);
		return revisions;
	}

	private boolean insertTag(String plot, String tag)
	{
		return database.update(
			"INSERT INTO creative_plot_tags (`name`,`tag`) VALUES (?, ?)" +
				"ON DUPLICATE KEY UPDATE `tag`=VALUES(`tag`)",
			plot, tag
		) > 0;
	}

	private final IDatabase database;
}
