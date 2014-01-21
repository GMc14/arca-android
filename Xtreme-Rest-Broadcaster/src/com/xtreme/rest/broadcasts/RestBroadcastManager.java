package com.xtreme.rest.broadcasts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class RestBroadcastManager {

	private static final int SEND_BROADCAST = 100;

	private static final Map<BroadcastReceiver, Set<String>> RECEIVERS = new HashMap<BroadcastReceiver, Set<String>>();
	private static final Map<String, Set<BroadcastReceiver>> ACTIONS = new HashMap<String, Set<BroadcastReceiver>>();
	
	private static final List<Broadcast> BROADCASTS = new ArrayList<Broadcast>();
	private static final Object LOCK = new Object();

	private static Context sContext;
	private static Handler sHandler;
	
	
	// ====================================================
	

	public static synchronized void registerReceiver(final BroadcastReceiver receiver, final String action) {
		addToActions(receiver, action);
		addToReceivers(receiver, action);
	}
	
	public static synchronized void unregisterReceiver(final BroadcastReceiver receiver) {
		final Set<String> actions = RECEIVERS.remove(receiver);
		removeReceiverFromActions(receiver, actions);
	}
	
	
	// ====================================================
	

	private static void addToReceivers(final BroadcastReceiver receiver, final String action) {
		Set<String> actions = RECEIVERS.get(receiver);
        if (actions == null) {
        	actions = new HashSet<String>(1);
            RECEIVERS.put(receiver, actions);
        }
        actions.add(action);
	}

	private static void addToActions(final BroadcastReceiver receiver, final String action) {
		Set<BroadcastReceiver> receivers = ACTIONS.get(action);
		if (receivers == null) {
			receivers = new HashSet<BroadcastReceiver>(1);
			ACTIONS.put(action, receivers);
		}
		receivers.add(receiver);
	}

	private static synchronized void removeReceiverFromActions(final BroadcastReceiver receiver, final Set<String> actions) {
		for (final String action : actions) {
			final Set<BroadcastReceiver> receivers = ACTIONS.get(action);
			if (receivers != null) {
				receivers.remove(receiver);
				
				if (receivers.size() <= 0) {
					ACTIONS.remove(action);
				}
			}
		}
	}

	
	// ====================================================
	
	
	public static synchronized void sendBroadcast(final Context context, final Intent intent) {
		final String action = intent.getAction();
		final Set<BroadcastReceiver> receivers = ACTIONS.get(action);
		
		if (receivers != null) {
			for (final BroadcastReceiver receiver : receivers) {
				BROADCASTS.add(new Broadcast(receiver, intent));
			}
			notifyMessageHandler(context);
		}
	}
	
	private static void notifyMessageHandler(final Context context) {
		synchronized (LOCK) {
			if (sHandler == null) {
				sHandler = new BroadcastHandler(context.getMainLooper());
				sContext = context.getApplicationContext();
			}
			if (!sHandler.hasMessages(SEND_BROADCAST)) {
				sHandler.sendEmptyMessage(SEND_BROADCAST);
			}
		}
	}
	
	private static synchronized void sendAndClearBroadcasts() {
		for (final Broadcast broadcast : BROADCASTS) {
			broadcast.getReceiver().onReceive(sContext, broadcast.getIntent());
		}
		BROADCASTS.clear();
	}
	
	
	// ====================================================

	
	private static final class BroadcastHandler extends Handler {
		
		private BroadcastHandler(final Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case SEND_BROADCAST:
				sendAndClearBroadcasts();
				break;
			}
		}
	}
	
	
	// ====================================================

	
	private static final class Broadcast {

		private final Intent mIntent;
		private final BroadcastReceiver mReceiver;
		
		public Broadcast(final BroadcastReceiver receiver, final Intent intent) {
			mReceiver = receiver;
			mIntent = intent;
		}
		
		public BroadcastReceiver getReceiver() {
			return mReceiver;
		}

		public Intent getIntent() {
			return mIntent;
		}
	}
}