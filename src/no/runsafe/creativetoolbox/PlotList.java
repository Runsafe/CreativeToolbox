package no.runsafe.creativetoolbox;

import com.google.common.collect.Lists;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlotList
{
	public void set(RunsafePlayer player, List<String> list)
	{
		lists.put(player.getName(), list);
	}

	public int current(RunsafePlayer player)
	{
		return lists.get(player.getName()).indexOf(pointer.get(player.getName())) + 1;
	}

	public void wind(RunsafePlayer player, String to)
	{
		pointer.put(player.getName(), to);
	}

	public int count(RunsafePlayer player)
	{
		return lists.get(player.getName()).size();
	}

	public void remove(String plot)
	{
		for (Map.Entry<String, List<String>> list : lists.entrySet())
		{
			if (list.getValue().contains(plot))
			{
				ArrayList<String> tmplist = Lists.newArrayList(list.getValue());
				int i = tmplist.indexOf(plot);
				tmplist.remove(plot);
				lists.put(list.getKey(), tmplist);
				pointer.put(list.getKey(), tmplist.get(i));
			}
		}
	}

	public String previous(RunsafePlayer player)
	{
		if (lists.containsKey(player.getName()))
		{
			List<String> list = lists.get(player.getName());
			if (list == null || list.isEmpty())
				return null;
			int i = list.indexOf(pointer.get(player.getName()));
			pointer.put(player.getName(), list.get(i > 0 ? i - 1 : list.size() - 1));
			return pointer.get(player.getName());
		}
		return null;
	}

	public String next(RunsafePlayer player)
	{
		if (lists.containsKey(player.getName()))
		{
			List<String> list = lists.get(player.getName());
			if (list == null || list.isEmpty())
				return null;
			int i = list.indexOf(pointer.get(player.getName()));
			pointer.put(player.getName(), list.get(i + 1 >= list.size() ? 0 : i + 1));
			return pointer.get(player.getName());
		}
		return null;
	}

	private final ConcurrentHashMap<String, String> pointer = new ConcurrentHashMap<String, String>();
	private final ConcurrentHashMap<String, List<String>> lists = new ConcurrentHashMap<String, List<String>>();
}
