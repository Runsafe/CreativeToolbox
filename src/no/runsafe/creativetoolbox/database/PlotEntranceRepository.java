package no.runsafe.creativetoolbox.database;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.database.IDatabase;
import no.runsafe.framework.api.database.Repository;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class PlotEntranceRepository extends Repository implements IConfigurationChanged
{
	public PlotEntranceRepository(IDatabase database)
	{
		this.database = database;
	}

	public PlotEntrance get(String regionName)
	{
		if (cache.containsKey(regionName.toLowerCase()))
			return cache.get(regionName.toLowerCase());

		ILocation entrance = database.queryLocation(
			"SELECT ? AS world, e.* FROM creativetoolbox_plot_entrance AS e WHERE name=?",
			world.getName(),
			regionName
		);

		if (entrance == null)
			cache.put(regionName.toLowerCase(), null);
		else
		{
			PlotEntrance plotEntrance = new PlotEntrance();
			plotEntrance.setName(regionName);
			plotEntrance.setLocation(entrance);
			cache.put(regionName.toLowerCase(), plotEntrance);
		}

		return cache.get(regionName.toLowerCase());
	}

	public void persist(PlotEntrance entrance)
	{
		if (entrance.getLocation() == null || entrance.getName() == null)
			return;
		database.update(
			"INSERT INTO creativetoolbox_plot_entrance (name, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?)" +
				"ON DUPLICATE KEY UPDATE x=VALUES(x), y=VALUES(y), z=VALUES(z), yaw=VALUES(yaw), pitch=VALUES(pitch)",
			entrance.getName(),
			entrance.getLocation().getX(),
			entrance.getLocation().getY(),
			entrance.getLocation().getZ(),
			entrance.getLocation().getYaw(),
			entrance.getLocation().getPitch()
		);
		cache.put(entrance.getName().toLowerCase(), entrance);
	}

	public void delete(PlotEntrance entrance)
	{
		delete(entrance.getName());
	}

	public void delete(String region)
	{
		database.execute("DELETE FROM creativetoolbox_plot_entrance WHERE name=?", region);
		if (cache.containsKey(region))
			cache.remove(region);
	}

	public List<String> getPlots()
	{
		return database.queryStrings("SELECT name FROM creativetoolbox_plot_entrance");
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		world = configuration.getConfigValueAsWorld("world");
	}

	@Override
	public String getTableName()
	{
		return "creativetoolbox_plot_entrance";
	}

	@Override
	public HashMap<Integer, List<String>> getSchemaUpdateQueries()
	{
		HashMap<Integer, List<String>> queries = new LinkedHashMap<Integer, List<String>>(1);
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
	private IWorld world;
}
