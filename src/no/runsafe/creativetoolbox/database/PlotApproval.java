package no.runsafe.creativetoolbox.database;

import no.runsafe.framework.database.IDatabase;
import no.runsafe.framework.database.ISchemaUpdater;
import no.runsafe.framework.database.RunsafeEntity;
import no.runsafe.framework.database.SchemaRevisionRepository;
import no.runsafe.framework.output.IOutput;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class PlotApproval extends RunsafeEntity {

	public String getName() {
		return name;
	}

	public void setName(String value) {
		name = value;
	}

	public Date getApproved() {
		return approved;
	}

	public void setApproved(Date value) {
		approved = value;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String value) {
		approvedBy = value;
	}

	private String name;
	private Date approved;
	private String approvedBy;
}
