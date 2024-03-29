package no.runsafe.creativetoolbox.database;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.api.database.ISchemaUpdate;
import no.runsafe.framework.api.database.Repository;
import no.runsafe.framework.api.database.SchemaUpdate;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.api.player.IPlayer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PlotMemberBlacklistRepository extends Repository implements IConfigurationChanged
{
	public void add(ICommandExecutor player, IPlayer blacklisted)
	{
		database.execute(
			"INSERT INTO creative_blacklist (`player`,`by`,`time`) VALUES (?, ?, NOW())",
			blacklisted.getName().toLowerCase(), player
		);
		blacklist.add(blacklisted);
	}

	public void remove(IPlayer blacklisted)
	{
		blacklist.remove(blacklisted);

		database.execute("DELETE FROM creative_blacklist WHERE `player`=?", blacklisted);
	}

	public boolean isBlacklisted(IPlayer player)
	{
		return blacklist.contains(player);
	}

	public List<IPlayer> getBlacklist()
	{
		return database.queryPlayers("SELECT `player` FROM creative_blacklist");
	}

	@Override
	public void OnConfigurationChanged(IConfiguration iConfiguration)
	{
		blacklist.clear();
		blacklist.addAll(database.queryPlayers("SELECT `player` FROM creative_blacklist"));
	}

	@Nonnull
	@Override
	public String getTableName()
	{
		return "creative_blacklist";
	}

	@Nonnull
	@Override
	public ISchemaUpdate getSchemaUpdateQueries()
	{
		ISchemaUpdate update = new SchemaUpdate();

		update.addQueries(
			"CREATE TABLE creative_blacklist (" +
				"`player` VARCHAR(255) NOT NULL," +
				"`by` VARCHAR(255) NOT NULL," +
				"`time` DATETIME NOT NULL," +
				"PRIMARY KEY (`player`)" +
			")"
		);

		update.addQueries(
			String.format( // Player names -> Unique IDs
				"UPDATE IGNORE `%s` SET `player` = " +
					"COALESCE((SELECT `uuid` FROM player_db WHERE `name`=`%s`.`player`), `player`) " +
					"WHERE length(`player`) != 36",
				getTableName(), getTableName()
			)
		);

		return update;
	}

	private final List<IPlayer> blacklist = new ArrayList<>();
}
