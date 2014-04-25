package com.appnet.app.operations;

import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncStats;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.xtreme.rest.broadcasts.RestBroadcaster;
import com.xtreme.rest.service.Operation;
import com.xtreme.rest.service.ServiceError;
import com.xtreme.rest.service.Task;

public class SyncPostsOperation extends Operation {

	public SyncPostsOperation(final Uri uri) {
		super(uri);
	}

	public SyncPostsOperation(final Parcel in) {
		super(in);
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		super.writeToParcel(dest, flags);
	}
	
	@Override
	public void onCreateTasks() {
		executeTask(new PostListTask());
	}

	@Override
	public void onSuccess(final Context context, final List<Task<?>> completed) {
		final ContentResolver resolver = context.getContentResolver();
		resolver.notifyChange(getUri(), null, false);
		
		RestBroadcaster.broadcast(context, getUri(), new SyncStats());
	}

	@Override
	public void onFailure(final Context context, final ServiceError error) {
		final SyncStats stats = new SyncStats();
		stats.numIoExceptions++;
		
		RestBroadcaster.broadcast(context, getUri(), stats);
	}

	public static final Parcelable.Creator<SyncPostsOperation> CREATOR = new Parcelable.Creator<SyncPostsOperation>() {
		@Override
		public SyncPostsOperation createFromParcel(final Parcel in) {
			return new SyncPostsOperation(in);
		}

		@Override
		public SyncPostsOperation[] newArray(final int size) {
			return new SyncPostsOperation[size];
		}
	};
}
