package com.xtreme.rest.dispatcher;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

public class SupportRequestDispatcher extends AbstractRequestDispatcher {

	private final Context mContext;
	private final LoaderManager mLoaderManager;

	public SupportRequestDispatcher(final RequestExecutor executor, final Context context, final LoaderManager manager) {
		super(executor);
		mContext = context;
		mLoaderManager = manager;
	}

	@Override
	public void execute(final Query request, final QueryListener listener) {
		final LoaderCallbacks<?> callbacks = new QueryLoaderCallbacks(listener);
		execute(request, callbacks);
	}
	
	@Override
	public void execute(final Update request, final UpdateListener listener) {
		final LoaderCallbacks<?> callbacks = new UpdateLoaderCallbacks(listener);
		execute(request, callbacks);
	}

	@Override
	public void execute(final Insert request, final InsertListener listener) {
		final LoaderCallbacks<?> callbacks = new InsertLoaderCallbacks(listener);
		execute(request, callbacks);
	}

	@Override
	public void execute(final Delete request, final DeleteListener listener) {
		final LoaderCallbacks<?> callbacks = new DeleteLoaderCallbacks(listener);
		execute(request, callbacks);
	}

	
	private void execute(final ContentRequest<?> request, final LoaderCallbacks<?> callbacks) {
		final int identifier = request.getIdentifier();
		final Bundle bundle = createRequestBundle(request);
		mLoaderManager.restartLoader(identifier, bundle, callbacks);
	}
	
	
	// ========================================
	


	private class QueryLoaderCallbacks extends NotififierCallbacks<QueryResult> {
		
		public QueryLoaderCallbacks(final QueryListener listener) {
			super(listener);
		}

		@Override
		public Loader<QueryResult> onCreateLoader(final int id, final Bundle args) {
			final RequestExecutor executor = getRequestExecutor();
			final Query request = args.getParcelable(Extras.REQUEST);
			return new SupportQueryLoader(mContext, executor, request);
		}
	}
	
	private class UpdateLoaderCallbacks extends NotififierCallbacks<UpdateResult> {
		
		public UpdateLoaderCallbacks(final UpdateListener listener) {
			super(listener);
		}

		@Override
		public Loader<UpdateResult> onCreateLoader(final int id, final Bundle args) {
			final RequestExecutor executor = getRequestExecutor();
			final Update request = args.getParcelable(Extras.REQUEST);
			return new SupportUpdateLoader(mContext, executor, request);
		}
	}
	
	private class InsertLoaderCallbacks extends NotififierCallbacks<InsertResult> {
		
		public InsertLoaderCallbacks(final InsertListener listener) {
			super(listener);
		}

		@Override
		public Loader<InsertResult> onCreateLoader(final int id, final Bundle args) {
			final RequestExecutor executor = getRequestExecutor();
			final Insert request = args.getParcelable(Extras.REQUEST);
			return new SupportInsertLoader(mContext, executor, request);
		}
	}
	
	private class DeleteLoaderCallbacks extends NotififierCallbacks<DeleteResult> {
		
		public DeleteLoaderCallbacks(final DeleteListener listener) {
			super(listener);
		}

		@Override
		public Loader<DeleteResult> onCreateLoader(final int id, final Bundle args) {
			final RequestExecutor executor = getRequestExecutor();
			final Delete request = args.getParcelable(Extras.REQUEST);
			return new SupportDeleteLoader(mContext, executor, request);
		}
	}
	
	private abstract class NotififierCallbacks<T extends ContentResult<?>> implements LoaderCallbacks<T> {
		
		private final ContentRequestListener<T> mListener;

		public NotififierCallbacks(final ContentRequestListener<T> listener) {
			mListener = listener;
		}

		@Override
		public void onLoadFinished(final Loader<T> loader, final T result) {
			mListener.onRequestComplete(result);
		}

		@Override
		public void onLoaderReset(final Loader<T> loader) {
			mListener.onRequestReset();
		}
	}
}