package no.runsafe.creativetoolbox.database;

import no.runsafe.framework.database.IDatabase;
import no.runsafe.framework.database.Repository;
import no.runsafe.framework.output.IOutput;
import org.joda.time.DateTime;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApprovedPlotRepository extends Repository
{
	public ApprovedPlotRepository(IOutput output, IDatabase db)
	{
		console = output;
		database = db;
	}

	public PlotApproval get(String plotName)
	{
		PreparedStatement select = database.prepare("SELECT name, approved, approved_by FROM creativetoolbox_plot_approval WHERE name=?");
		try
		{
			select.setString(1, plotName);
			ResultSet set = select.executeQuery();
			if (set.first())
			{
				PlotApproval approval = new PlotApproval();
				approval.setName(set.getString(1));
				approval.setApproved(new DateTime(set.getTimestamp(2)));
				approval.setApprovedBy(set.getString(3));
				return approval;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public void persist(PlotApproval plotApproval)
	{
		PreparedStatement insert = database.prepare(
			"INSERT INTO creativetoolbox_plot_approval (name, approved, approved_by) VALUES (?, ?, ?)" +
				"ON DUPLICATE KEY UPDATE approved=VALUES(approved), approved_by=VALUES(approved_by)"
		);
		try
		{
			insert.setString(1, plotApproval.getName());
			insert.setTimestamp(2, new Timestamp(plotApproval.getApproved().getMillis()));
			insert.setString(3, plotApproval.getApprovedBy());
			insert.execute();
		}
		catch (SQLException e)
		{
			console.write(e.getMessage());
		}
	}

	public void delete(PlotApproval plotApproval)
	{
		PreparedStatement delete = database.prepare(
			"DELETE FROM creativetoolbox_plot_approval WHERE name=?"
		);
		try
		{
			delete.setString(1, plotApproval.getName());
			delete.execute();
		}
		catch (SQLException e)
		{
			console.write(e.getMessage());
		}
	}

	public List<String> getApprovedPlots()
	{
		PreparedStatement select = database.prepare(
			"SELECT name FROM creativetoolbox_plot_approval"
		);
		try
		{
			ArrayList<String> result = new ArrayList<String>();
			ResultSet set = select.executeQuery();
			while (set.next())
				result.add(set.getString(1));
			return result;
		}
		catch (SQLException e)
		{
			console.write(e.getMessage());
		}
		return null;
	}

	@Override
	public String getTableName()
	{
		return "creativetoolbox_plot_approval";
	}

	@Override
	public HashMap<Integer, List<String>> getSchemaUpdateQueries()
	{
		HashMap<Integer, List<String>> queries = new HashMap<Integer, List<String>>();
		List<String> sql = new ArrayList<String>();
		sql.add(
			"CREATE TABLE creativetoolbox_plot_approval (" +
				"`name` varchar(255) NOT NULL," +
				"`approved` datetime NOT NULL," +
				"`approved_by` varchar(255) NOT NULL," +
				"PRIMARY KEY(`name`)" +
				")"
		);
		queries.put(1, sql);
		return queries;
	}

	private final IOutput console;
	private final IDatabase database;
}
