package no.runsafe.creativetoolbox.event;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotTagRepository;
import no.runsafe.creativetoolbox.database.PlotVoteRepository;
import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.event.IAsyncEvent;
import no.runsafe.framework.api.event.player.IPlayerInteractEntityEvent;
import no.runsafe.framework.api.event.player.IPlayerRightClickBlock;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.block.RunsafeBlock;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerInteractEntityEvent;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InteractEvents implements IPlayerRightClickBlock, IPlayerInteractEntityEvent, IConfigurationChanged, IAsyncEvent
{
	public InteractEvents(
		PlotFilter plotFilter,
		WorldGuardInterface worldGuard,
		PlotManager manager,
		ApprovedPlotRepository plotRepository,
		PlotVoteRepository votes, PlotTagRepository tagRepository)
	{
		this.worldGuardInterface = worldGuard;
		this.plotFilter = plotFilter;
		this.manager = manager;
		this.plotRepository = plotRepository;
		this.votes = votes;
		this.tagRepository = tagRepository;
	}

	@Override
	public boolean OnPlayerRightClick(RunsafePlayer player, RunsafeMeta itemInHand, RunsafeBlock block)
	{
		if (extensions.containsKey(player.getName()))
		{
			String target = extensions.get(player.getName());
			extensions.remove(player.getName());
			manager.extendPlot(player, target, block.getLocation());
			return false;
		}

		if (itemInHand != null && itemInHand.getItemId() == listItem)
		{
			this.listPlotsByLocation(block.getLocation(), player);
			return false;
		}
		return true;
	}

	@Override
	public void OnPlayerInteractEntityEvent(RunsafePlayerInteractEntityEvent event)
	{
		if (event.getRightClicked() instanceof RunsafePlayer && event.getPlayer().hasPermission("runsafe.creative.list"))
		{
			if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getItemId() == listItem)
			{
				this.listPlotsByPlayer((RunsafePlayer) event.getRightClicked(), event.getPlayer());
				event.setCancelled(true);
			}
		}
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		listItem = configuration.getConfigValueAsInt("list_item");
	}

	public void startPlotExtension(RunsafePlayer player, String plot)
	{
		extensions.put(player.getName(), plot);
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
			triggerPlayer.sendColouredMessage(Strings.join(
				manager.tag(triggerPlayer, regions),
				"\n"
			));
		else if (triggerPlayer.hasPermission("runsafe.creative.list.showname"))
			triggerPlayer.sendMessage(String.format("%s does not own any regions.", checkPlayer.getPrettyName()));
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

		if (regions != null && !regions.isEmpty())
			for (String regionName : regions)
			{
				player.sendColouredMessage("Region: %s", manager.tag(player, regionName));
				listTags(player, regionName);
				listVotes(player, regionName);
				listPlotMembers(player, regionName);
			}
		else
			player.sendMessage("No regions found at this point.");
	}

	private void listTags(RunsafePlayer player, String regionName)
	{
		if (player.hasPermission("runsafe.creative.tag.read"))
		{
			List<String> tags = tagRepository.getTags(regionName);
			if (tags != null && !tags.isEmpty())
				player.sendColouredMessage("  Tags: &o%s&r", Strings.join(tags, ", "));
		}
	}

	private void listVotes(RunsafePlayer player, String regionName)
	{
		if (player.hasPermission("runsafe.creative.vote.tally"))
		{
			int tally = votes.tally(regionName);
			if (tally > 0)
				player.sendColouredMessage("  This plot has %d vote%s!", tally, tally > 1 ? "s" : "");
		}
	}

	private void listPlotMembers(RunsafePlayer player, String regionName)
	{
		Set<String> owners = worldGuardInterface.getOwners(manager.getWorld(), regionName);
		for (String owner : owners)
			listPlotMember(player, "Owner", owner, true);

		Set<String> members = worldGuardInterface.getMembers(manager.getWorld(), regionName);
		for (String member : members)
			listPlotMember(player, "Member", member, false);
	}

	private void listPlotMember(RunsafePlayer player, String label, String member, boolean showSeen)
	{
		RunsafePlayer plotMember = RunsafeServer.Instance.getPlayer(member);
		if (plotMember != null)
		{
			player.sendColouredMessage("     %s: %s", label, plotMember.getPrettyName());

			if (showSeen && player.hasPermission("runsafe.creative.list.seen"))
			{
				String seen = plotMember.getLastSeen(player);
				player.sendColouredMessage("     %s", (seen == null ? "Player never seen" : seen));
			}
		}
	}

	private final WorldGuardInterface worldGuardInterface;
	private int listItem;
	private final PlotManager manager;
	private final PlotFilter plotFilter;
	private final ApprovedPlotRepository plotRepository;
	private final PlotVoteRepository votes;
	private final PlotTagRepository tagRepository;
	private final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("dd.MM.YYYY");
	private final ConcurrentHashMap<String, String> extensions = new ConcurrentHashMap<String, String>();
}