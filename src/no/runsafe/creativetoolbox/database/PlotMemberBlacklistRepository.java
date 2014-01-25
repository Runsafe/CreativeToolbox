package no.runsafe.creativetoolbox.database;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.api.database.IDatabase;
import no.runsafe.framework.api.database.ISchemaUpdate;
import no.runsafe.framework.api.database.Repository;
import no.runsafe.framework.api.database.SchemaUpdate;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.api.player.IPlayer;

import java.util.ArrayList;
import java.util.List;

public class PlotMemberBlacklistRepository extends Repository implements IConfigurationChanged
{
	public void add(ICommandExecutor player, IPlayer blacklisted)
	{
		database.execute(
			"INSERT INTO creative_blacklist (`player`,`by`,`time`) VALUES (?, ?, NOW())",
			blacklisted.getName().toLowerCase(), player.getName()
		);
		blacklist.add(blacklisted.getName().toLowerCase());
	}

	public void remove(IPlayer blacklisted)
	{
		String playerName = blacklisted.getName().toLowerCase();
		if (blacklist.contains(playerName))
			blacklist.remove(playerName);

		database.execute("DELETE FROM creative_blacklist WHERE `player`=?", playerName);
	}

	public boolean isBlacklisted(IPlayer player)
	{
		return blacklist.contains(player.getName().toLowerCase());
	}

	public List<IPlayer> getBlacklist()
	{
		return database.queryPlayers("SELECT `player` FROM creative_blacklist");
	}

	@Override
	public void OnConfigurationChanged(IConfiguration iConfiguration)
	{
		blacklist.clear();
		blacklist.addAll(database.queryStrings("SELECT `player` FROM creative_blacklist"));
	}

	@Override
	public String getTableName()
	{
		return "creative_blacklist";
	}

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

		return update;
	}

	private final List<String> blacklist = new ArrayList<String>();
}
