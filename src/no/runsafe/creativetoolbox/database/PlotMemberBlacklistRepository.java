package no.runsafe.creativetoolbox.database;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.api.database.IDatabase;
import no.runsafe.framework.api.database.Repository;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.api.player.IPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class PlotMemberBlacklistRepository extends Repository implements IConfigurationChanged
{
	public PlotMemberBlacklistRepository(IDatabase database)
	{
		this.database = database;
	}

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
	public HashMap<Integer, List<String>> getSchemaUpdateQueries()
	{
		HashMap<Integer, List<String>> revisions = new LinkedHashMap<Integer, List<String>>(1);
		List<String> sql = new ArrayList<String>();
		sql.add(
			"CREATE TABLE creative_blacklist (" +
				"`player` VARCHAR(255) NOT NULL," +
				"`by` VARCHAR(255) NOT NULL," +
				"`time` DATETIME NOT NULL," +
				"PRIMARY KEY (`player`)" +
				")"
		);
		revisions.put(1, sql);
		return revisions;
	}

	private final IDatabase database;
	private final List<String> blacklist = new ArrayList<String>();
}
