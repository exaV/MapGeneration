package ch.fhnw.ether.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class SyncGroup {
	private static final AtomicLong                  nextGroup = new AtomicLong();
	private static final Map<Long,List<IStateProxy>> groups    = new HashMap<>();
	private static final Map<Object, Long>           syncs     = new WeakHashMap<>();

	public static long newGroupId(Object syncObject) {
		Long result = Long.valueOf(nextGroup.incrementAndGet());
		synchronized(syncs) {
			syncs.put(syncObject, result);
		}
		return result.longValue();
	}

	public static void sync(Object syncObject) {
		Long groupId = null;
		synchronized(syncs) {
			groupId = syncs.get(syncObject);
		}
		if(groupId != null)
			sync(groupId.longValue());
	}

	public static void sync(long groupId) {
		Long groupID = Long.valueOf(groupId);
		synchronized(groups) {
			List<IStateProxy> group = groups.get(groupID);
			if(group != null) {
				for(IStateProxy mp : group)
					mp.sync();
			}
		}
	}

	public static void addToGroup(Long groupId, IStateProxy proxy) {
		synchronized(groups) {
			List<IStateProxy> group = groups.get(groupId);
			if(group == null) {
				group = new ArrayList<>();
				groups.put(groupId, group);
			}
			group.add(proxy);
		}
	}

	public static void removeFromGroup(Long groupId, IStateProxy proxy) {
		synchronized(groups) {
			List<IStateProxy> group = groups.get(groupId);
			if(group != null) {
				group.remove(proxy);
				if(group.isEmpty())
					groups.remove(groupId);
			}
		}
	}
}
