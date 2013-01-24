package no.runsafe.creativetoolbox.database;

import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.database.IDatabase;
import no.runsafe.framework.database.Repository;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.RunsafeWorld;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlotEntranceRepository extends Repository implements IConfigurationChanged
{
	public PlotEntranceRepository(IOutput output, IDatabase database)
	{
		this.database = database;
		this.console = output;
	}

	public PlotEntrance get(String regionName)
	{
		if (cache.containsKey(regionName.toLowerCase()))
			return cache.get(regionName.toLowerCase());

		PreparedStatement select = database.prepare(
			"SELECT * FROM creativetoolbox_plot_entrance WHERE name=?"
		);

		try
		{
			select.setString(1, regionName);
			ResultSet result = select.executeQuery();
			if (result.first())
			{
				RunsafeLocation location = new RunsafeLocation(
					world,
					result.getDouble("x"),
					result.getDouble("y"),
					result.getDouble("z"),
					result.getFloat("yaw"),
					result.getFloat("pitch")
				);
				PlotEntrance entrance = new PlotEntrance();
				entrance.setName(regionName);
				entrance.setLocation(location);
				cache.put(regionName.toLowerCase(), entrance);
			}
			else
				cache.put(regionName.toLowerCase(), null);

			return cache.get(regionName.toLowerCase());
		}
		catch (SQLException e)
		{
			console.write(e.getMessage());
		}
		return null;
	}

	public void persist(PlotEntrance entrance)
	{
		PreparedStatement insert = database.prepare(
			"INSERT INTO creativetoolbox_plot_entrance (name, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?)" +
				"ON DUPLICATE KEY UPDATE x=VALUES(x), y=VALUES(y), z=VALUES(z), yaw=VALUES(yaw), pitch=VALUES(pitch)"
		);
		try
		{
			insert.setString(1, entrance.getName());
			insert.setDouble(2, entrance.getLocation().getX());
			insert.setDouble(3, entrance.getLocation().getY());
			insert.setDouble(4, entrance.getLocation().getZ());
			insert.setFloat(5, entrance.getLocation().getYaw());
			insert.setFloat(6, entrance.getLocation().getPitch());
			insert.execute();
			cache.put(entrance.getName().toLowerCase(), entrance);
		}
		catch (SQLException e)
		{
			console.write(e.getMessage());
		}
	}

	public void delete(PlotEntrance entrance)
	{
		delete(entrance.getName());
	}

	public void delete(String region)
	{
		PreparedStatement delete = database.prepare("DELETE FROM creativetoolbox_plot_entrance WHERE name=?");
		try
		{
			delete.setString(1, region);
			delete.execute();
			if (cache.containsKey(region))
				cache.remove(region);
		}
		catch (SQLException e)
		{
			console.write(e.getMessage());
		}
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		world = RunsafeServer.Instance.getWorld(configuration.getConfigValueAsString("world"));
	}

	@Override
	public String getTableName()
	{
		return "creativetoolbox_plot_entrance";
	}

	@Override
	public HashMap<Integer, List<String>> getSchemaUpdateQueries()
	{
		HashMap<Integer, List<String>> queries = new HashMap<Integer, List<String>>();
		List<String> sql = new ArrayList<String>();
		sql.add(
			"CREATE TABLE creativetoolbox_plot_entrance (" +
				"`name` varchar(255) NOT NULL," +
				"`x` double NOT NULL," +
				"`y` double NOT NULL," +
				"`z` double NOT NULL," +
				"`pitch` float NOT NULL," +
				"`yaw` float NOT NULL," +
				"PRIMARY KEY(`name`)" +
				")"
		);
		queries.put(1, sql);
		return queries;
	}

	private final IDatabase database;
	private final HashMap<String, PlotEntrance> cache = new HashMap<String, PlotEntrance>();
	private final IOutput console;
	private RunsafeWorld world;
}
