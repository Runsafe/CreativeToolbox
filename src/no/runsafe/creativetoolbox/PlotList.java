package no.runsafe.creativetoolbox;

import com.google.common.collect.Lists;
import no.runsafe.framework.api.player.IPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlotList
{
	public void set(IPlayer player, List<String> list)
	{
		lists.put(player, list);
	}

	public int current(IPlayer player)
	{
		return lists.get(player).indexOf(pointer.get(player)) + 1;
	}

	public void wind(IPlayer player, String to)
	{
		pointer.put(player, to);
	}

	public int count(IPlayer player)
	{
		return lists.get(player).size();
	}

	public void remove(String plot)
	{
		for (Map.Entry<IPlayer, List<String>> list : lists.entrySet())
		{
			if (!list.getValue().contains(plot))
				continue;

			ArrayList<String> plots = Lists.newArrayList(list.getValue());
			int i = plots.indexOf(plot);
			plots.remove(plot);
			if (plots.isEmpty())
			{
				pointer.remove(list.getKey());
				lists.remove(list.getKey());
				continue;
			}

			lists.put(list.getKey(), plots);
			if (plots.size() > i)
				pointer.put(list.getKey(), plots.get(i));
			else
				pointer.put(list.getKey(), plots.get(0));
		}
	}

	public String previous(IPlayer player)
	{
		if (!lists.containsKey(player))
			return null;

		List<String> list = lists.get(player);
		if (list == null || list.isEmpty())
			return null;
		int i = list.indexOf(pointer.get(player));
		pointer.put(player, list.get(i > 0 ? i - 1 : list.size() - 1));
		return pointer.get(player);
	}

	public String next(IPlayer player)
	{
		if (!lists.containsKey(player))
			return null;

		List<String> list = lists.get(player);
		if (list == null || list.isEmpty())
			return null;
		int i = list.indexOf(pointer.get(player));
		pointer.put(player, list.get(i + 1 >= list.size() ? 0 : i + 1));
		return pointer.get(player);
	}

	private final ConcurrentHashMap<IPlayer, String> pointer = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<IPlayer, List<String>> lists = new ConcurrentHashMap<>();
}
