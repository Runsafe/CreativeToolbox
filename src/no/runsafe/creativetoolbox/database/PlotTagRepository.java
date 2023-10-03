package no.runsafe.creativetoolbox.database;

import no.runsafe.framework.api.database.ISchemaUpdate;
import no.runsafe.framework.api.database.Repository;
import no.runsafe.framework.api.database.SchemaUpdate;

import javax.annotation.Nonnull;
import java.util.List;

public class PlotTagRepository extends Repository
{
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

	public void renamePlot(String oldName, String newName)
	{
		database.update("UPDATE `" + getTableName() + "` SET name=? WHERE name=?;", newName, oldName);
	}

	@Nonnull
	@Override
	public String getTableName()
	{
		return "creative_plot_tags";
	}

	@Nonnull
	@Override
	public ISchemaUpdate getSchemaUpdateQueries()
	{
		ISchemaUpdate update = new SchemaUpdate();

		update.addQueries(
			"CREATE TABLE creative_plot_tags (" +
				"`name` VARCHAR(255)," +
				"`tag` VARCHAR(255)," +
				"PRIMARY KEY(`name`,`tag`)" +
			")"
		);

		return update;
	}

	private boolean insertTag(String plot, String tag)
	{
		return database.update(
			"INSERT INTO creative_plot_tags (`name`,`tag`) VALUES (?, ?)" +
				"ON DUPLICATE KEY UPDATE `tag`=VALUES(`tag`)",
			plot, tag
		) > 0;
	}
}
