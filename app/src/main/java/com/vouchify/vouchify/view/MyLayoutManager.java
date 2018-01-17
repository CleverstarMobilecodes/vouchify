package com.vouchify.vouchify.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Hai Nguyen - 12/22/15.
 */
public class MyLayoutManager extends LinearLayoutManager {

	private RecyclerView recyclerView;

	public MyLayoutManager(Context context, RecyclerView recyclerView,
                           int orientation, boolean reverseLayout) {
		super(context, orientation, reverseLayout);
		this.recyclerView = recyclerView;
	}

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

	/**
	 * 
	 * @param y
	 *            Y scroll
	 */
	public void scrollByY(int y) {

		try {

			recyclerView.scrollBy(0, y);
		} catch (Exception ignore) {

		}
	}
}
