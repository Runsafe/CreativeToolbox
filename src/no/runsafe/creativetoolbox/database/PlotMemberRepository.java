package no.runsafe.creativetoolbox.database;

import no.runsafe.framework.api.database.ISchemaUpdate;
import no.runsafe.framework.api.database.Repository;
import no.runsafe.framework.api.database.SchemaUpdate;
import no.runsafe.framework.api.player.IPlayer;

import javax.annotation.Nonnull;
import java.util.List;

public class PlotMemberRepository extends Repository
{
	@Nonnull
	@Override
	public String getTableName()
	{
		return "creative_plot_member";
	}

	public void addMember(String plot, IPlayer player, boolean isOwner)
	{
		database.execute(
			"INSERT INTO creative_plot_member (`plot`,`player`,`owner`) VALUES (?,?,?)" +
				"ON DUPLICATE KEY UPDATE owner=VALUES(owner)",
			plot, player.getName(), isOwner ? 1 : 0
		);
	}

	public void removeMember(String region, IPlayer member)
	{
		database.execute("DELETE FROM creative_plot_member WHERE `plot`=? AND `player`=?", region, member.getName());
	}

	public List<IPlayer> getMembers(String plot, boolean owners, boolean members)
	{
		if (!owners && !members)
			return null;

		if (owners && members)
			return database.queryPlayers("SELECT `player` FROM creative_plot_member WHERE plot=?", plot);
		else
			return database.queryPlayers("SELECT `player` FROM creative_plot_member WHERE plot=? AND owner=?", plot, owners ? 1 : 0);
	}

	public List<String> getPlots(IPlayer player, boolean owner, boolean member)
	{
		if (!owner && !member)
			return null;

		if (owner && member)
			return database.queryStrings("SELECT DISTINCT `plot` FROM creative_plot_member WHERE player=?", player.getName());
		else
			return database.queryStrings("SELECT DISTINCT `plot` FROM creative_plot_member WHERE player=? AND owner=?", player.getName(), owner ? 1 : 0);
	}

	public int cleanStaleData()
	{
		return database.update("DELETE FROM creative_plot_member WHERE `plot` NOT IN (SELECT `plot` FROM creative_plot_log)");
	}

	@Nonnull
	@Override
	public ISchemaUpdate getSchemaUpdateQueries()
	{
		ISchemaUpdate update = new SchemaUpdate();

		update.addQueries(
			"CREATE TABLE creative_plot_member (" +
				"`plot` VARCHAR(250)," +
				"`player` VARCHAR(250)," +
				"`owner` INT," +
				"PRIMARY KEY(`plot`,`player`)" +
				");"
		);

		return update;
	}
}
