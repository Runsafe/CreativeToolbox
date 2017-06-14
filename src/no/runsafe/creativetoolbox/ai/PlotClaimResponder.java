package no.runsafe.creativetoolbox.ai;

import no.runsafe.creativetoolbox.PlotCalculator;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotApproval;
import no.runsafe.framework.api.IServer;
import no.runsafe.framework.api.ai.IChatResponseTrigger;
import no.runsafe.framework.api.log.IDebug;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlotClaimResponder implements IChatResponseTrigger
{
	public PlotClaimResponder(PlotManager manager, WorldGuardInterface worldGuard, IServer server, IDebug debug, ApprovedPlotRepository approvalRepository, PlotCalculator calculator)
	{
		this.manager = manager;
		this.worldGuard = worldGuard;
		this.server = server;
		this.debug = debug;
		this.approvalRepository = approvalRepository;
		this.calculator = calculator;
	}

	@Override
	public String getResponse(String playerName, Matcher message)
	{
		IPlayer player = server.getPlayerExact(playerName);
		if (player == null || player.hasPermission("runsafe.creative.claim.others"))
			return null;

		if (!player.hasPermission("runsafe.creative.claim.self"))
		{
			List<IPlayer> onlineStaff = server.getPlayersWithPermission("runsafe.creative.claim.others");
			for (IPlayer staff : onlineStaff)
				if (!player.shouldNotSee(staff))
					return String.format("First you will need permission to build, %s - I am sure %s would help you out!", playerName, staff.getName());

			return String.format("Sorry, %s, but there are no staff online now to give you permission. Please come back a little later!", playerName);
		}

		List<String> existing = worldGuard.getOwnedRegions(player, manager.getWorld());
		debug.debugFine("%s has %d plots.", playerName, existing.size());
		if (!existing.isEmpty())
		{
			for (String plot : existing)
			{
				PlotApproval approved = approvalRepository.get(plot);
				debug.debugFine("Plot %s is %s.", plot, approved != null ? "approved" : "unapproved");
				if (approved == null)
					return String.format("You must either get all your plots approved, or ask a member of staff to grant you a plot, %s.", playerName);
			}
		}

		boolean claimable = manager.isCurrentClaimable(player);
		Rectangle2D region = calculator.getPlotArea(player.getLocation());
		if (!claimable || region  == null)
			return String.format("First you need a free plot, %s, use /ct f to find one! Once there, please use /ct claim.", playerName);

		return String.format("You can claim the plot you are in now, %s; just type /ct claim!", playerName);
	}

	@Override
	public Pattern getRule()
	{
		return Pattern.compile("(how|can).*(have|claim|get).*plot");
	}

	private final PlotManager manager;
	private final WorldGuardInterface worldGuard;
	private final IServer server;
	private final IDebug debug;
	private final ApprovedPlotRepository approvalRepository;
	private final PlotCalculator calculator;
}
