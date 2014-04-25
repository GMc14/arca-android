package com.appnet.app.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.appnet.app.R;
import com.appnet.app.activities.PostActivity;
import com.appnet.app.animators.SimpleAdapterAnimator;
import com.appnet.app.datasets.PostTable;
import com.appnet.app.providers.AppNetContentProvider;
import com.xtreme.rest.adapters.SupportCursorAdapter;
import com.xtreme.rest.broadcasts.RestError;
import com.xtreme.rest.fragments.ContentLoaderAdapterSupportFragment;
import com.xtreme.rest.loader.ContentRequest;
import com.xtreme.rest.loader.ContentResponse;
import com.xtremelabs.imageutils.ImageLoader;

public class PostListFragment extends ContentLoaderAdapterSupportFragment implements OnItemClickListener {

	
	private static final String[] COLUMN_NAMES = new String[] { 
		PostTable.Columns.TEXT,
		PostTable.Columns.IMAGE_URL,
		PostTable.Columns.CREATED_AT,
	};

	private static final int[] VIEW_IDS = new int[] { 
        R.id.list_item_post_text,
        R.id.list_item_post_image,
        R.id.list_item_post_created_at,
	};

	private ImageLoader mImageLoader;
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_post_list, container, false);
		((AbsListView) view.findViewById(R.id.post_list)).setOnItemClickListener(this);
		mImageLoader = ImageLoader.buildImageLoaderForSupportFragment(this);
		return view;
	}
	
	@Override
	public CursorAdapter onCreateAdapter(int itemResourceId, int[] viewIds, String[] columnNames) {
		final SupportCursorAdapter adapter = new SupportCursorAdapter(getActivity(), itemResourceId, columnNames, viewIds);
		adapter.setAdapterAnimator(new SimpleAdapterAnimator());
		adapter.setViewBinder(this);
		return adapter;
	}
	
	@Override
	public void onDestroyView() {
		mImageLoader.destroy();
		super.onDestroyView();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		loadPosts();
	}
	
	@Override
	public String[] getColumnNames() {
		return COLUMN_NAMES;
	}
	
	@Override
	public int[] getViewIds() {
		return VIEW_IDS;
	}
	
	@Override
	protected int getAdapterViewId() {
		return R.id.post_list;
	}
	
	@Override
	protected int getAdapterItemResourceId() {
		return R.layout.list_item_post;
	}
	
	private void loadPosts() {
		final Uri contentUri = AppNetContentProvider.Uris.POSTS_URI;
		final ContentRequest request = new ContentRequest(contentUri);
		request.setSortOrder(PostTable.Columns.CREATED_AT + " desc");
		execute(request);
	}
	
	@Override
	public void onContentChanged(final ContentResponse response) {
		final CursorAdapter adapter = getCursorAdapter();
		if (adapter.getCount() > 0) {
			showResults();
		} else {
			hideLoading();
		}
	}
	
	private void showResults() {
		getView().findViewById(R.id.post_list).setVisibility(View.VISIBLE);
		getView().findViewById(R.id.loading).setVisibility(View.INVISIBLE);
	}
	
	private void hideLoading() {
		getView().findViewById(R.id.loading).setVisibility(View.INVISIBLE);
	}
	
	@Override
	public void onError(final RestError error) {
		Toast.makeText(getActivity(), "ERROR: " + error.getMessage(), Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
		final Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
		final String itemId = cursor.getString(cursor.getColumnIndex(PostTable.Columns.ID));
		PostActivity.newInstance(getActivity(), itemId);
	}

	@Override
	public boolean setViewValue(final View view, final Cursor cursor, final int columnIndex) {
		switch (view.getId()) {
		case R.id.list_item_post_image:
		    final String url = cursor.getString(columnIndex);
		    mImageLoader.loadImage((ImageView) view, url);
		    return true;

		default:
		    return super.setViewValue(view, cursor, columnIndex);
		}
	}
}
