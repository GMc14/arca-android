package com.appnet.app.sync;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.appnet.app.operations.SyncPostsOperation;
import com.appnet.app.providers.AppNetContentProvider;
import com.xtreme.rest.service.RestService;
import com.xtreme.rest.sync.RestSyncAdapter;

public class AppNetSyncAdapter extends RestSyncAdapter {

	private static final long MANUAL_SYNC_INTERVAL = 5 * 1000;

	public AppNetSyncAdapter(final Context context, final boolean autoInitialize) {
		super(context, autoInitialize);
	}

	@Override
	protected void onSetupSync(final Account account, final Bundle extras, final String authority, final ContentProviderClient provider) {

		final long lastSyncTime = getLastSyncTime(account, authority);

		if (System.currentTimeMillis() > (lastSyncTime + MANUAL_SYNC_INTERVAL)) { 
			ContentResolver.requestSync(account, authority, createPostsBundle());
		}
	}
	
	@Override
	protected boolean onPerformSync(final Uri uri, final Account account, final Bundle extras, final String authority, final ContentProviderClient provider) {
		return RestService.start(getContext(), new SyncPostsOperation(uri));
	}
	
	
	// =============================================
	

	private static Bundle createPostsBundle() {
		final Bundle bundle = new Bundle();
		final Uri uri = AppNetContentProvider.Uris.POSTS_URI;
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		bundle.putString(RestSyncAdapter.Extras.URI, uri.toString());
		return bundle;
	}
}
