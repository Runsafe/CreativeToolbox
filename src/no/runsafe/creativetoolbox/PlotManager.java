package no.runsafe.creativetoolbox;

import no.runsafe.creativetoolbox.database.*;
import no.runsafe.creativetoolbox.event.PlotApprovedEvent;
import no.runsafe.creativetoolbox.event.PlotDeletedEvent;
import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.event.IServerReady;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.api.hook.IPlayerDataProvider;
import no.runsafe.framework.api.log.IConsole;
import no.runsafe.framework.api.log.IDebug;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.worldguardbridge.IRegionControl;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormat;

import java.awt.geom.Rectangle2D;
import java.time.Instant;
import java.util.*;

public class PlotManager implements IConfigurationChanged, IServerReady, IPlayerDataProvider
{
	public PlotManager(
		PlotFilter plotFilter,
		IRegionControl worldGuardInterface,
		PlotEntranceRepository plotEntranceRepository,
		ApprovedPlotRepository approvedPlotRepository,
		PlotVoteRepository voteRepository, PlotTagRepository tagRepository, PlotMemberRepository memberRepository, PlotCalculator plotCalculator,
		PlotMemberBlacklistRepository blackList, PlotList plotList,
		IConsole console, IDebug debugger, PlotLogRepository plotLog)
	{
		filter = plotFilter;
		worldGuard = worldGuardInterface;
		plotEntrance = plotEntranceRepository;
		plotApproval = approvedPlotRepository;
		this.voteRepository = voteRepository;
		this.tagRepository = tagRepository;
		this.memberRepository = memberRepository;
		calculator = plotCalculator;
		this.blackList = blackList;
		this.plotList = plotList;
		this.console = console;
		this.debugger = debugger;
		this.plotLog = plotLog;
	}

	public String getLatestPlot(IPlayer player)
	{
		return plotLog.getLatest(player);
	}

	public String getCurrentRegionFiltered(IPlayer player)
	{
		if (!world.equals(player.getWorld()))
			return null;
		List<String> regions = filter.apply(worldGuard.getRegionsAtLocation(player.getLocation()));
		if (regions == null || regions.isEmpty())
			return null;
		return regions.get(0);
	}

	public boolean isCurrentClaimable(IPlayer player)
	{
		if (!world.equals(player.getWorld()))
			return false;
		List<String> regions = worldGuard.getRegionsAtLocation(player.getLocation());
		return regions == null || new HashSet<>(ignoredRegions).containsAll(regions);
	}

	public java.util.List<ILocation> getPlotEntrances()
	{
		ArrayList<ILocation> entrances = new ArrayList<>();
		for (String plot : filter.getFiltered())
			entrances.add(plotEntrance.get(plot).getLocation());
		return entrances;
	}

	public java.util.List<ILocation> getFreePlotEntrances()
	{
		return freePlots;
	}

	public boolean plotIsTaken(ILocation location)
	{
		List<String> regions = worldGuard.getRegionsAtLocation(location);
		if (regions == null || regions.isEmpty())
			return false;
		boolean ok = true;
		for (String region : regions)
			if (!ignoredRegions.contains(region))
			{
				ok = false;
				break;
			}
		if (!ok)
		{
			setTaken(calculator.getColumn(location.getBlockX()), calculator.getRow(location.getBlockZ()));
			freePlots.remove(location);
		}
		return !ok;
	}

	public ILocation getPlotEntrance(String plotName)
	{
		String plot = filter.apply(plotName);
		if (world == null || plot == null)
			return null;
		PlotEntrance entrance = plotEntrance.get(plot);
		Rectangle2D rect = worldGuard.getRectangle(world, plot);
		if (rect == null)
			return null;
		if (entrance != null)
		{
			if (!rect.contains(entrance.getLocation().getBlockX(), entrance.getLocation().getBlockZ()))
				plotEntrance.delete(plot);
			else
				return entrance.getLocation();
		}
		return calculator.getDefaultEntrance(worldGuard.getRegionLocation(world, plot));
	}

	public Map<String, String> getOldPlots()
	{
		if (!worldGuard.serverHasWorldGuard())
			return null;

		List<String> approvedPlots = plotApproval.getApprovedPlots();
		Map<String, Set<IPlayer>> checkList = worldGuard.getAllRegionsWithOwnersInWorld(getWorld());
		Map<String, String> hits = new HashMap<>();
		for (String region : filter.apply(new ArrayList<>(checkList.keySet())))
		{
			if (approvedPlots.contains(region))
				continue;

			Duration status = getPlotStatus(checkList.get(region));
			if (status != null && (status.equals(Duration.ZERO) || status.isShorterThan(limit)))
				continue;
			hits.put(region, formatReason(status));
		}
		return hits;
	}

	private String formatReason(Duration status)
	{
		if (status == null)
			return "&cnull&r";

		if (status.equals(BANNED))
			return "&cbanned&r";

		return PeriodFormat.getDefault().print(new Period(status, DateTime.now(), PeriodType.yearMonthDay()));
	}

	private Duration getPlotStatus(Set<IPlayer> owners)
	{
		Duration result = null;
		for (IPlayer owner : owners)
		{
			Duration ownerSeen = getSeen(owner);
			if (ownerSeen == null)
				return null;
			if (ownerSeen.isEqual(Duration.ZERO))
				return Duration.ZERO;
			if (result == null || !result.isEqual(BANNED))
				result = ownerSeen;
		}
		return result;
	}

	private Duration getSeen(IPlayer player)
	{
		if (player == null)
			return null;

		if (lastSeen.containsKey(player))
			return lastSeen.get(player);

		if (player.isOnline())
		{
			lastSeen.put(player, Duration.ZERO);
			return Duration.ZERO;
		}

		if (!player.isNotBanned())
		{
			lastSeen.put(player, BANNED);
			return BANNED;
		}

		DateTime logout = player.lastLogout();
		if (logout == null)
		{
			lastSeen.put(player, null);
			return null;
		}

		lastSeen.put(player, new Duration(logout, DateTime.now()));
		return lastSeen.get(player);
	}

	public boolean disallowVote(IPlayer player, String region)
	{
		return !world.equals(player.getWorld())
			|| (voteBlacklist.containsKey(region) && voteBlacklist.get(region).contains(player))
			|| worldGuard.getOwnerPlayers(world, region).contains(player)
			|| worldGuard.getMemberPlayers(world, region).contains(player);
	}

	public boolean vote(IPlayer player, String region)
	{
		boolean voted = voteRepository.recordVote(player, region);
		int score = voteRepository.tally(region, voteRanks);
		if (score >= autoApprove)
		{
			PlotApproval approval = plotApproval.get(region);
			if (approval == null)
				approve("Popular vote", region);
		}
		return voted;
	}

	public List<String> tag(IPlayer player, List<String> plotNames)
	{
		if (plotNames == null)
			return null;
		List<String> tagged = new ArrayList<>();
		for (String plot : plotNames)
			tagged.add(tag(player, plot));
		return tagged;
	}

	public String tag(IPlayer player, String plot)
	{
		List<String> tags = new ArrayList<>();
		tags.add(plot);
		if (player.hasPermission("runsafe.creative.approval.read"))
		{
			PlotApproval approved = plotApproval.get(plot);
			if (approved != null && approved.getApproved() != null)
				tags.add(String.format("&2[approved &a%s&2]&r", dateFormat.print(new DateTime(approved.getApproved().toEpochMilli()))));
		}
		if (player.hasPermission("runsafe.creative.vote.tally"))
		{
			int voteCount = voteRepository.tally(plot);
			if (voteCount > 0)
				tags.add(String.format("&2[&a%d&2 vote%s]&r", voteCount, voteCount > 1 ? "s" : ""));
		}
		return StringUtils.join(tags, " ");
	}

	public PlotApproval approve(String approver, String plot)
	{
		PlotApproval approval = new PlotApproval();
		approval.setApproved(Instant.now());
		approval.setApprovedBy(approver);
		approval.setName(plot);
		plotApproval.persist(approval);
		approval = plotApproval.get(plot);
		if (approval == null)
			return approval;

		for (IPlayer owner : worldGuard.getOwnerPlayers(world, plot))
		{
			int approved = 0;
			for (String region : worldGuard.getOwnedRegions(owner, world))
				if (plotApproval.get(region) != null)
					approved++;

			new PlotApprovedEvent(owner, approval, approved).Fire();
		}
		return approval;
	}

	public boolean claim(IPlayer claimer, IPlayer owner, String plotName, Rectangle2D region)
	{
		if (!world.equals(claimer.getWorld()))
			return false;
		if (!worldGuard.createRegion(
			owner, world, plotName,
			calculator.getMinPosition(world, region),
			calculator.getMaxPosition(world, region)
		))
			return false;

		voteRepository.clear(plotName);
		PlotApproval approval = plotApproval.get(plotName);
		if (approval != null)
			plotApproval.delete(approval);
		if (!plotLog.log(plotName, claimer.getName()))
			console.logWarning("Unable to log plot %s claimed by %s", plotName, claimer.getPrettyName());
		setTaken(calculator.getColumn((long) region.getCenterX()), calculator.getRow((long) region.getCenterY()));
		PlotEntrance entrance = new PlotEntrance();
		entrance.setName(plotName);
		entrance.setLocation(calculator.getDefaultEntrance(worldGuard.getRegionLocation(world, plotName)));
		plotEntrance.persist(entrance);
		return true;
	}

	public void extendPlot(IPlayer player, String target, ILocation location)
	{
		if (!world.equals(player.getWorld()))
			return;

		Rectangle2D area = worldGuard.getRectangle(world, target);
		PlotDimension currentSize = calculator.getPlotDimensions(area);
		PlotDimension targetSize = currentSize.expandToInclude(calculator.getPlotArea(location));
		ScanTakenPlots();
		long firstCol = currentSize.getMinimumColumn();
		long lastCol = currentSize.getMaximumColumn();
		long firstRow = currentSize.getMinimumRow();
		long lastRow = currentSize.getMaximumRow();
		long targetCol = targetSize.getMaximumColumn();
		long targetRow = targetSize.getMaximumRow();
		debugger.debugFine("Extending plot %s to %s", currentSize, targetSize);
		for (long column = targetSize.getMinimumColumn(); column <= targetCol; ++column)
		{
			for (long row = targetSize.getMinimumRow(); row <= targetRow; ++row)
			{
				if (column >= firstCol && column <= lastCol && row >= firstRow && row <= lastRow)
					continue;

				if (!isTaken(column, row))
					continue;

				debugger.debugFine("Plot (%d,%d) is taken!", column, row);
				player.sendColouredMessage("Unable to extend plot here, overlap detected!");
				return;
			}
		}
		if (!worldGuard.redefineRegion(world, target, targetSize.getMinPosition(), targetSize.getMaxPosition()))
		{
			player.sendColouredMessage("An error occurred while extending plot.");
			return;
		}

		for (long column = targetSize.getMinimumColumn(); column <= targetCol; ++column)
			for (long row = targetSize.getMinimumRow(); row <= targetRow; ++row)
				setTaken(column, row);

		player.sendColouredMessage("The plot has been extended!");
	}

	public void delete(IPlayer deletor, String region)
	{
		Rectangle2D area = worldGuard.getRectangle(world, region);
		long col = calculator.getColumn((int) area.getCenterX());
		long row = calculator.getRow((int) area.getCenterY());
		setFree(col, row);
		worldGuard.deleteRegion(filter.getWorld(), region);
		plotEntrance.delete(region);
		tagRepository.setTags(region, null);
		voteRepository.clear(region);
		plotLog.delete(region);
		plotList.remove(region);
		new PlotDeletedEvent(deletor, region).Fire();
	}

	public boolean renamePlot(String oldName, String newName)
	{
		// Make sure the plot exists.
		if (worldGuard.getRegionLocation(world, oldName) == null)
			return false;

		// Make sure there isn't already a plot with the new name.
		if (worldGuard.getRegionLocation(world, newName) != null)
			return false;

		worldGuard.renameRegion(world, oldName, newName);
		changeRepositoryPlotName(oldName, newName);
		return true;
	}

	public void changeRepositoryPlotName(String oldName, String newName)
	{
		plotApproval.renamePlot(oldName, newName);
		plotEntrance.renamePlot(oldName, newName);
		plotLog.renamePlot(oldName, newName);
		memberRepository.renamePlot(oldName, newName);
		tagRepository.renamePlot(oldName, newName);
		voteRepository.renamePlot(oldName, newName);
	}

	public IWorld getWorld()
	{
		return world;
	}

	public boolean isInWrongWorld(IPlayer player)
	{
		return !world.equals(player.getWorld());
	}

	@Override
	public void OnConfigurationChanged(IConfiguration config)
	{
		world = config.getConfigValueAsWorld("world");
		debugger.debugFine("World %s is %s", config.getConfigValueAsString("world"), world);
		ignoredRegions = config.getConfigValueAsList("free.ignore");
		limit = new Period(0, 0, 0, config.getConfigValueAsInt("old_after"), 0, 0, 0, 0).toStandardDuration();
		autoApprove = config.getConfigValueAsInt("vote.approved");
		voteRanks = config.getConfigValuesAsIntegerMap("vote.rank");
	}

	@Override
	public void OnServerReady()
	{
		ScanTakenPlots();
		ScanFreePlots();
		CleanStaleData();
	}

	@Override
	public HashMap<String, String> GetPlayerData(IPlayer player)
	{
		HashMap<String, String> data = new HashMap<>();
		data.put("runsafe.creative.blacklisted", blackList.isBlacklisted(player) ? "true" : "false");

		List<String> plots = memberRepository.getPlots(player, true, false);
		if (!plots.isEmpty())
			data.put("runsafe.creative.owner", plots.toString());

		plots = memberRepository.getPlots(player, false, true);
		if (!plots.isEmpty())
			data.put("runsafe.creative.member", plots.toString());

		return data;
	}

	public void removeMember(IPlayer player)
	{
		for (String region : worldGuard.getRegionsInWorld(world))
		{
			Set<IPlayer> members = worldGuard.getMemberPlayers(world, region);
			if (members == null || !members.contains(player))
				continue;
			debugger.debugFiner("Removing member %s from %s.", player.getPrettyName(), region);
			worldGuard.removeMemberFromRegion(world, region, player);
			memberRepository.removeMember(region, player);
		}
	}

	public void memberRemoved(String plot, IPlayer player)
	{
		if (!voteBlacklist.containsKey(plot))
			voteBlacklist.put(plot, new ArrayList<>());
		voteBlacklist.get(plot).add(player);
	}

	private void CleanStaleData()
	{
		IWorld world = filter.getWorld();
		if (world == null)
			debugger.debugFine("No world defined!");
		Map<String, Rectangle2D> regions = worldGuard.getRegionRectanglesInWorld(world);
		if (regions == null)
		{
			debugger.debugFine("No regions in world!");
			return;
		}
		Set<String> current = regions.keySet();

		List<String> loggedPlots = plotLog.getPlots();
		int deleted = 0;
		for (String plot : loggedPlots)
			if (!current.contains(plot))
			{
				plotLog.delete(plot);
				deleted++;
			}

		List<String> taggedPlots = tagRepository.getTaggedPlots();
		int cleared = 0;
		for (String plot : taggedPlots)
			if (!current.contains(plot))
			{
				tagRepository.setTags(plot, null);
				cleared++;
			}

		List<String> approvedPlots = plotApproval.getApprovedPlots();
		int nuked = 0;
		for (String plot : approvedPlots)
			if (!current.contains(plot))
			{
				plotApproval.delete(plotApproval.get(plot));
				nuked++;
			}

		int cleaned = memberRepository.cleanStaleData();

		for (IPlayer player : blackList.getBlacklist())
		{
			removeMember(player);
			cleaned++;
		}

		console.logInformation(
			"Deleted &a%d&r plots, unapproved &a%d&r non-existing plots, cleared tags from &a%d&r deleted plots and &a%d&r members.",
			deleted, nuked, cleared, cleaned
		);
	}

	private void ScanTakenPlots()
	{
		Map<String, Rectangle2D> taken = worldGuard.getRegionRectanglesInWorld(filter.getWorld());
		if (taken == null)
			return;

		for (String region : taken.keySet())
		{
			if (ignoredRegions.contains(region))
				continue;
			long col = calculator.getColumn((int) taken.get(region).getCenterX());
			long row = calculator.getRow((int) taken.get(region).getCenterY());
			setTaken(col, row);
		}
	}

	private boolean isTaken(long col, long row)
	{
		return takenPlots.containsKey(col) && takenPlots.get(col).contains(row);
	}

	private void setFree(long col, long row)
	{
		if (takenPlots.containsKey(col))
			takenPlots.get(col).remove(row);
	}

	private void setTaken(long col, long row)
	{
		if (!takenPlots.containsKey(col))
			takenPlots.put(col, new ArrayList<>());
		if (!takenPlots.get(col).contains(row))
			takenPlots.get(col).add(row);
	}

	private void ScanFreePlots()
	{
		if (world == null)
			return;

		for (long column : calculator.getColumns())
			for (long row : calculator.getRows())
				if (!(takenPlots.containsKey(column) && takenPlots.get(column).contains(row)))
					freePlots.add(calculator.getDefaultEntrance(column, row));
	}

	private static final Duration BANNED = new Duration(Long.MAX_VALUE);
	private final PlotFilter filter;
	private final IRegionControl worldGuard;
	private final PlotEntranceRepository plotEntrance;
	private final ApprovedPlotRepository plotApproval;
	private final PlotVoteRepository voteRepository;
	private final PlotTagRepository tagRepository;
	private final PlotMemberRepository memberRepository;
	private final PlotCalculator calculator;
	private final PlotMemberBlacklistRepository blackList;
	private final PlotList plotList;
	private final IConsole console;
	private final IDebug debugger;
	private final PlotLogRepository plotLog;
	private final HashMap<IPlayer, Duration> lastSeen = new HashMap<>();
	private final HashMap<Long, ArrayList<Long>> takenPlots = new HashMap<>();
	private final ArrayList<ILocation> freePlots = new ArrayList<>();
	private final Map<String, List<IPlayer>> voteBlacklist = new HashMap<>();
	private IWorld world;
	private List<String> ignoredRegions;
	private Duration limit;
	private int autoApprove;
	private Map<String, Integer> voteRanks;
	private final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("dd.MM.YYYY");
}
