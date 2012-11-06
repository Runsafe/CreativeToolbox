package no.runsafe.creativetoolbox.database;

import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.database.IDatabase;
import no.runsafe.framework.database.IRepository;
import no.runsafe.framework.database.ISchemaUpdater;
import no.runsafe.framework.database.SchemaRevisionRepository;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.RunsafeWorld;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class PlotEntranceRepository implements ISchemaUpdater, IRepository<PlotEntrance, String>, IConfigurationChanged
{
	public PlotEntranceRepository(IOutput output, IDatabase database)
	{
		this.database = database;
		this.console = output;
	}

	@Override
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

	@Override
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

	@Override
	public void delete(PlotEntrance entrance)
	{
		PreparedStatement delete = database.prepare("DELETE FROM creativetoolbox_plot_entrance WHERE name=?");
		try
		{
			delete.setString(1, entrance.getName());
			delete.execute();
		}
		catch (SQLException e)
		{
			console.write(e.getMessage());
		}
	}

	@Override
	public void Run(SchemaRevisionRepository repository, IDatabase database)
	{
		int revision = repository.getRevision("creativetoolbox_plot_entrance");
		if (revision < 1)
		{
			console.write("Creating table creativetoolbox_plot_entrance");
			PreparedStatement create = database.prepare(
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
			try
			{
				create.execute();
				revision = 1;
			}
			catch (SQLException e)
			{
				console.write(e.getMessage());
			}
		}
		repository.setRevision("creativetoolbox_plot_entrance", revision);
	}

	private final IDatabase database;
	private final HashMap<String, PlotEntrance> cache = new HashMap<String, PlotEntrance>();
	private final IOutput console;

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		world = RunsafeServer.Instance.getWorld(configuration.getConfigValueAsString("world"));
	}

	private RunsafeWorld world;
}
