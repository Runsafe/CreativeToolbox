package no.runsafe.creativetoolbox;

import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.PlotLogRepository;
import no.runsafe.creativetoolbox.database.PlotMemberRepository;
import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.minecraft.RunsafeWorld;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.List;

public class ImportWorldGuardRegions implements IConfigurationChanged
{
	public ImportWorldGuardRegions(
		PlotManager manager,
		WorldGuardInterface worldGuard,
		PlotLogRepository logRepository,
		PlotMemberRepository memberRepository)
	{
		this.manager = manager;
		this.worldGuard = worldGuard;
		this.logRepository = logRepository;
		this.memberRepository = memberRepository;
	}


	@Override
	public void OnConfigurationChanged(IConfiguration config)
	{
		RunsafeWorld world = manager.getWorld();
		List<String> regions = worldGuard.getRegionsInWorld(world);
		for (String region : regions)
		{
			String claim = logRepository.getClaim(region);
			if (claim == null)
				logRepository.log(region, "unknown");

			for (String member : worldGuard.getMembers(world, region))
				memberRepository.addMember(region, member, false);

			for (String member : worldGuard.getOwners(world, region))
				memberRepository.addMember(region, member, true);
		}
		config.setConfigValue("imported", true);
		config.save();
	}

	private final PlotManager manager;
	private final WorldGuardInterface worldGuard;
	private final PlotLogRepository logRepository;
	private final PlotMemberRepository memberRepository;
}
