package no.runsafe.creativetoolbox.events;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotApproval;
import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.event.player.IPlayerInteractEntityEvent;
import no.runsafe.framework.event.player.IPlayerInteractEvent;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.event.player.RunsafePlayerInteractEntityEvent;
import no.runsafe.framework.server.event.player.RunsafePlayerInteractEvent;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;
import org.bukkit.event.block.Action;

import java.util.List;
import java.util.Set;

public class InteractEvents implements IPlayerInteractEvent, IPlayerInteractEntityEvent, IConfigurationChanged
{
	public InteractEvents(
		IConfiguration configuration,
		PlotFilter plotFilter,
		WorldGuardInterface worldGuard,
		ApprovedPlotRepository plotRepository
	)
	{
		this.configuration = configuration;
		this.worldGuardInterface = worldGuard;
		this.plotFilter = plotFilter;
		this.plotRepository = plotRepository;
	}

	@Override
	public void OnPlayerInteractEvent(RunsafePlayerInteractEvent event)
	{
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			if (event.getItemStack() != null && event.getItemStack().getTypeId() == listItem)
			{
				this.listPlotsByLocation(new RunsafeLocation(event.getBlock().getLocation()), event.getPlayer());
				event.setCancelled(true);
			}
		}
	}

	@Override
	public void OnPlayerInteractEntityEvent(RunsafePlayerInteractEntityEvent event)
	{
		if (event.getRightClicked() instanceof RunsafePlayer)
		{
			if (event.getPlayer().getItemInHand().getItemId() == listItem)
			{
				this.listPlotsByPlayer((RunsafePlayer) event.getRightClicked(), event.getPlayer());
				event.setCancelled(true);
			}
		}
	}

	@Override
	public void OnConfigurationChanged()
	{
		listItem = this.configuration.getConfigValueAsInt("list_item");
	}

	private void listPlotsByPlayer(RunsafePlayer checkPlayer, RunsafePlayer triggerPlayer)
	{
		if (!this.worldGuardInterface.serverHasWorldGuard())
		{
			triggerPlayer.sendMessage("Error: No WorldGuard installed.");
			return;
		}

		List<String> regions = plotFilter.apply(worldGuardInterface.getOwnedRegions(checkPlayer, checkPlayer.getWorld()));

		if (!regions.isEmpty())
			for (String regionName : regions)
				this.listRegion(regionName, triggerPlayer, true);
		else
			triggerPlayer.sendMessage("No regions owned by this player.");
	}

	private void listPlotsByLocation(RunsafeLocation location, RunsafePlayer player)
	{
		if (!this.worldGuardInterface.serverHasWorldGuard())
		{
			player.sendMessage("Error: No WorldGuard installed.");
			return;
		}

		List<String> regions = plotFilter.apply(worldGuardInterface.getRegionsAtLocation(location));

		if (!regions.isEmpty())
			for (String regionName : regions)
				this.listRegion(regionName, player, false);
		else
			player.sendMessage("No regions found at this point.");
	}

	private void listRegion(String regionName, RunsafePlayer player, Boolean simple)
	{
		if (player.hasPermission("runsafe.creative.approval.read"))
		{
			PlotApproval approval = plotRepository.get(regionName);
			if (approval == null || approval.getApproved() == null)
				player.sendMessage("Region: " + regionName);
			else
				player.sendMessage(String.format("Region: %s [Approved %s]", regionName, approval.getApproved()));
		}
		else
			player.sendMessage("Region: " + regionName);

		if (!simple)
		{
			Set<String> owners = worldGuardInterface.getOwners(player.getWorld(), regionName);
			Set<String> members = worldGuardInterface.getMembers(player.getWorld(), regionName);

			for (String owner : owners)
				player.sendMessage("     Owner: " + owner);

			for (String member : members)
				player.sendMessage("     Member: " + member);
		}
	}

	private IConfiguration configuration;
	private WorldGuardInterface worldGuardInterface;
	private int listItem;
	private PlotFilter plotFilter;
	private ApprovedPlotRepository plotRepository;
}
