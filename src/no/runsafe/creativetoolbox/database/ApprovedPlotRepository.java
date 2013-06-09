package no.runsafe.creativetoolbox.database;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import no.runsafe.framework.database.IDatabase;
import no.runsafe.framework.database.Repository;
import no.runsafe.framework.output.IOutput;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApprovedPlotRepository extends Repository
{
	public ApprovedPlotRepository(IOutput output, IDatabase db)
	{
		console = output;
		database = db;
	}

	public PlotApproval get(String plotName)
	{
		Map<String, Object> data = database.QueryRow("SELECT name, approved, approved_by FROM creativetoolbox_plot_approval WHERE name=?", plotName);
		if (data == null || data.isEmpty())
			return null;

		PlotApproval approval = new PlotApproval();
		approval.setName((String) data.get("name"));
		approval.setApproved(convert(data.get("approved")));
		approval.setApprovedBy((String) data.get("approved_by"));
		return approval;
	}

	public void persist(PlotApproval plotApproval)
	{
		database.Update(
			"INSERT INTO creativetoolbox_plot_approval (name, approved, approved_by) VALUES (?, ?, ?)" +
				"ON DUPLICATE KEY UPDATE approved=VALUES(approved), approved_by=VALUES(approved_by)",
			plotApproval.getName(), convert(plotApproval.getApproved()), plotApproval.getApprovedBy()
		);
	}

	public void delete(PlotApproval plotApproval)
	{
		database.Execute("DELETE FROM creativetoolbox_plot_approval WHERE name=?", plotApproval.getName());
	}

	public List<String> getApprovedPlots()
	{
		return Lists.transform(
			database.QueryColumn("SELECT name FROM creativetoolbox_plot_approval"),
			new Function<Object, String>()
			{
				@Override
				public String apply(@Nullable Object o)
				{
					return (String)o;
				}
			}
		);
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
