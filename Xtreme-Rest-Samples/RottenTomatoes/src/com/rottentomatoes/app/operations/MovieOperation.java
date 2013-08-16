package com.rottentomatoes.app.operations;

import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.xtreme.rest.broadcasts.RestBroadcaster;
import com.xtreme.rest.service.Operation;
import com.xtreme.rest.service.ServiceError;
import com.xtreme.rest.service.Task;

public class MovieOperation extends Operation {

	public MovieOperation(final Uri uri) {
		super(uri);
	}

	public MovieOperation(final Parcel in) {
		super(in);
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		super.writeToParcel(dest, flags);
	}

	@Override
	public void onCreateTasks() {
		final String id = getUri().getLastPathSegment();
		executeTask(new MovieTask(id));
	}

	@Override
	public void onSuccess(final Context context, final List<Task<?>> completed) {
		final ContentResolver resolver = context.getContentResolver();
		resolver.notifyChange(getUri(), null);
	}

	@Override
	public void onFailure(final Context context, final ServiceError error) {
		final int errorCode = error.getCode();
		final String errorMessage = error.getMessage();
		RestBroadcaster.broadcast(context, getUri(), errorCode, errorMessage);
	}
	
	public static final Parcelable.Creator<MovieOperation> CREATOR = new Parcelable.Creator<MovieOperation>() {
		@Override
		public MovieOperation createFromParcel(final Parcel in) {
			return new MovieOperation(in);
		}

		@Override
		public MovieOperation[] newArray(final int size) {
			return new MovieOperation[size];
		}
	};

}