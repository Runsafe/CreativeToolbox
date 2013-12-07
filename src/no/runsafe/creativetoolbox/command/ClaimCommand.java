package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotCalculator;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotApproval;
import no.runsafe.creativetoolbox.database.PlotMemberRepository;
import no.runsafe.framework.api.command.argument.PlayerArgument;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.RunsafeWorld;
import no.runsafe.framework.minecraft.player.RunsafeAmbiguousPlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;

public class ClaimCommand extends PlayerCommand
{
	public ClaimCommand(
		PlotManager manager, PlotCalculator calculator, WorldGuardInterface worldGuard,
		PlotMemberRepository members, ApprovedPlotRepository approvalRepository)
	{
		super("claim", "Claims a plot", null, new PlayerArgument(false));
		this.manager = manager;
		this.calculator = calculator;
		this.worldGuard = worldGuard;
		this.members = members;
		this.approvalRepository = approvalRepository;
	}

	@Override
	public String OnExecute(IPlayer executor, Map<String, String> params) //, String[] args)
	{
		String current = manager.getCurrentRegionFiltered(executor);
		if (current != null)
			return String.format("This plot is already claimed as %s!", current);

		if (!manager.isCurrentClaimable(executor))
			return "You may not claim a plot here.";

		Rectangle2D region = calculator.getPlotArea(executor.getLocation());
		if (region == null)
			return "You need to stand in a plot to use this command.";

		RunsafeWorld world = executor.getWorld();
		IPlayer owner = params.containsKey("player") ? RunsafeServer.Instance.getPlayer(params.get("player")) : null;
		if (owner instanceof RunsafeAmbiguousPlayer)
			return owner.toString();

		if (owner != null && !executor.hasPermission("runsafe.creative.claim"))
			return "You can only claim plots for yourself.";

		if (owner == null)
			owner = executor;

		List<String> existing = worldGuard.getOwnedRegions(owner, world);
		console.debugFine("%s has %d plots.", owner, existing.size());
		if (!existing.isEmpty() && executor.getName().equals(owner.getName()))
		{
			for (String plot : existing)
			{
				PlotApproval approved = approvalRepository.get(plot);
				console.debugFine("Plot %s is %s.", plot, approved != null ? "approved" : "unapproved");
				if (approved == null)
					return "You may not claim another plot before all your current ones have been approved.";
			}
		}

		int n = 1;
		String plotName = String.format("%s_%%d", owner.getName().toLowerCase());
		while (existing.contains(String.format(plotName, n)))
			n++;
		plotName = String.format(plotName, n);

		if (manager.claim(executor, owner, plotName, region))
		{
			members.addMember(plotName, owner.getName(), true);
			if (owner == executor)
				return String.format("New plot \"%s\" created - use /ct teleport %d to get back to it!", plotName, n);

			return String.format("Successfully claimed the plot \"%s\" for %s!", plotName, owner.getPrettyName());
		}

		return String.format("Unable to claim a new plot for %s :(", owner.getPrettyName());
	}

	private final PlotManager manager;
	private final PlotCalculator calculator;
	private final WorldGuardInterface worldGuard;
	private final PlotMemberRepository members;
	private final ApprovedPlotRepository approvalRepository;
}
