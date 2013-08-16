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
import com.xtreme.rest.utils.Logger;

public class MovieListOperation extends Operation {


	public MovieListOperation(final Uri uri) {
		super(uri);
	}

	public MovieListOperation(final Parcel in) {
		super(in);
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		super.writeToParcel(dest, flags);
	}

	@Override
	public void onCreateTasks() {
		final String type = getUri().getLastPathSegment();
		executeTask(new MovieListTask(type));
	}

	@Override
	public void onSuccess(final Context context, final List<Task<?>> completed) {
		Logger.v("notifyChange : %s", getUri());
		final ContentResolver resolver = context.getContentResolver();
		resolver.notifyChange(getUri(), null, false);
	}

	@Override
	public void onFailure(final Context context, final ServiceError error) {
		final int errorCode = error.getCode();
		final String errorMessage = error.getMessage();
		RestBroadcaster.broadcast(context, getUri(), errorCode, errorMessage);
	}
	
	public static final Parcelable.Creator<MovieListOperation> CREATOR = new Parcelable.Creator<MovieListOperation>() {
		@Override
		public MovieListOperation createFromParcel(final Parcel in) {
			return new MovieListOperation(in);
		}

		@Override
		public MovieListOperation[] newArray(final int size) {
			return new MovieListOperation[size];
		}
	};

}