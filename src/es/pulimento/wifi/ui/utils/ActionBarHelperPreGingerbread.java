/*
 * Copyright 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.pulimento.wifi.ui.utils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import es.pulimento.wifi.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * A class that implements the action bar pattern for pre-Honeycomb devices.
 * Sightly modified by Javier Pulido.
 */
public class ActionBarHelperPreGingerbread extends ActionBarHelper {
	private static final String MENU_RES_NAMESPACE = "http://schemas.android.com/apk/res/android";
	private static final String MENU_ATTR_ID = "id";
	private static final String MENU_ATTR_SHOW_AS_ACTION = "showAsAction";
	private android.support.v4.app.FragmentActivity mActivity;

	protected Set<Integer> mActionItemIds = new HashSet<Integer>();

	protected ActionBarHelperPreGingerbread(android.support.v4.app.FragmentActivity activity) {
		super(activity);
		mActivity = activity;
	}

	/** {@inheritDoc} */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mActivity.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	}

	/** {@inheritDoc} */
	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		mActivity.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.actionbar_compat);
		setupActionBar();
	}

	/**
	 * Sets up the compatibility action bar with the given title.
	 */
	@SuppressWarnings("deprecation")
	private void setupActionBar() {
		final ViewGroup actionBarCompat = getActionBarCompat();
		if (actionBarCompat == null) {
			return;
		}

		LinearLayout.LayoutParams springLayoutParams = new LinearLayout.LayoutParams(0,
				ViewGroup.LayoutParams.FILL_PARENT);
		springLayoutParams.weight = 1;

		// Add Home button
		SimpleMenu tempMenu = new SimpleMenu(mActivity);
		SimpleMenuItem homeItem = new SimpleMenuItem(tempMenu, android.R.id.home, 0,
				mActivity.getString(R.string.app_name));
		homeItem.setIcon(R.drawable.ic_home);
		addActionItemCompatFromMenuItem(homeItem);

		// Add title text
		TextView titleText = new TextView(mActivity, null, R.attr.actionbarCompatTitleStyle);
		titleText.setLayoutParams(springLayoutParams);
		titleText.setText(mActivity.getTitle());
		actionBarCompat.addView(titleText);

		// Hardcoded Custom actions

		final ViewGroup actionBar = getActionBarCompat();
		ImageButton actionButton = null;
		Resources res = mActivity.getApplicationContext().getResources();
		if (actionBar == null) {
			Log.e("CustomActionBar", "actionBar returned is null :(");
		}

		// Adding Share action button
		actionButton = new ImageButton(mActivity, null, R.attr.actionbarCompatItemStyle);
		actionButton.setLayoutParams(new ViewGroup.LayoutParams((int) mActivity.getResources().getDimension(
				R.dimen.actionbar_compat_button_width), ViewGroup.LayoutParams.FILL_PARENT));
		actionButton.setImageDrawable(res.getDrawable(R.drawable.ic_menu_share));
		actionButton.setScaleType(ImageView.ScaleType.CENTER);
		actionButton.setContentDescription("Compartir");
		actionButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						mActivity.getString(R.string.menu_share_subject));
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, mActivity.getString(R.string.menu_share_text));
				mActivity.startActivity(Intent.createChooser(shareIntent,
						mActivity.getString(R.string.menu_share_title)));
			}
		});
		actionBar.addView(actionButton);

		// Adding Open Options action button
		actionButton = new ImageButton(mActivity, null, R.attr.actionbarCompatItemStyle);
		actionButton.setLayoutParams(new ViewGroup.LayoutParams((int) mActivity.getResources().getDimension(
				R.dimen.actionbar_compat_button_width), ViewGroup.LayoutParams.FILL_PARENT));
		actionButton.setImageDrawable(res.getDrawable(R.drawable.ic_menu_moreoverflow));
		actionButton.setScaleType(ImageView.ScaleType.CENTER);
		actionButton.setContentDescription("Menu");
		actionButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Log.d("CustomActionBar", "Pressed ShowMenu Button :)");
				mActivity.openOptionsMenu();
			}
		});
		actionBar.addView(actionButton);
	}

	/** {@inheritDoc} */
	@Override
	public void setRefreshActionItemState(boolean refreshing) {
		View refreshButton = mActivity.findViewById(R.id.actionbar_compat_item_refresh);
		View refreshIndicator = mActivity.findViewById(R.id.actionbar_compat_item_refresh_progress);

		if (refreshButton != null) {
			refreshButton.setVisibility(refreshing ? View.GONE : View.VISIBLE);
		}
		if (refreshIndicator != null) {
			refreshIndicator.setVisibility(refreshing ? View.VISIBLE : View.GONE);
		}
	}

	/**
	 * Action bar helper code to be run in
	 * {@link Activity#onCreateOptionsMenu(android.view.Menu)}.
	 * NOTE: This code will mark on-screen menu items as invisible.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Hides on-screen action items from the options menu.
		for (Integer id : mActionItemIds) {
			menu.findItem(id).setVisible(false);
		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	protected void onTitleChanged(CharSequence title, int color) {
		TextView titleView = (TextView) mActivity.findViewById(R.id.actionbar_compat_title);
		if (titleView != null) {
			titleView.setText(title);
		}
	}

	/**
	 * Returns a {@link android.view.MenuInflater} that can read action bar
	 * metadata on
	 * pre-Honeycomb devices.
	 */
	public MenuInflater getMenuInflater(MenuInflater superMenuInflater) {
		return new WrappedMenuInflater(mActivity, superMenuInflater);
	}

	/**
	 * Returns the {@link android.view.ViewGroup} for the action bar on phones
	 * (compatibility action
	 * bar). Can return null, and will return null on Honeycomb.
	 */
	private ViewGroup getActionBarCompat() {
		return (ViewGroup) mActivity.findViewById(R.id.actionbar_compat);
	}

	/**
	 * Adds an action button to the compatibility action bar, using menu
	 * information from a {@link android.view.MenuItem}. If the menu item ID is
	 * <code>menu_refresh</code>, the menu item's
	 * state can be changed to show a loading spinner using
	 * {@link com.example.android.actionbarcompat.ActionBarHelperBase#setRefreshActionItemState(boolean)}
	 * .
	 */
	@SuppressWarnings("deprecation")
	private View addActionItemCompatFromMenuItem(final MenuItem item) {
		final int itemId = item.getItemId();

		final ViewGroup actionBar = getActionBarCompat();
		if (actionBar == null) {
			return null;
		}

		// Create the button
		ImageButton actionButton = new ImageButton(mActivity, null,
				itemId == android.R.id.home ? R.attr.actionbarCompatItemHomeStyle : R.attr.actionbarCompatItemStyle);
		actionButton.setLayoutParams(new ViewGroup.LayoutParams((int) mActivity.getResources().getDimension(
				itemId == android.R.id.home ? R.dimen.actionbar_compat_button_home_width
						: R.dimen.actionbar_compat_button_width), ViewGroup.LayoutParams.FILL_PARENT));
		if (itemId == R.id.menu_refresh) {
			actionButton.setId(R.id.actionbar_compat_item_refresh);
		}
		actionButton.setImageDrawable(item.getIcon());
		actionButton.setScaleType(ImageView.ScaleType.CENTER);
		actionButton.setContentDescription(item.getTitle());
		actionButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				mActivity.onMenuItemSelected(Window.FEATURE_OPTIONS_PANEL, item);
			}
		});

		actionBar.addView(actionButton);

		if (item.getItemId() == R.id.menu_refresh) {
			// Refresh buttons should be stateful, and allow for indeterminate
			// progress indicators,
			// so add those.
			ProgressBar indicator = new ProgressBar(mActivity, null, R.attr.actionbarCompatProgressIndicatorStyle);

			final int buttonWidth = mActivity.getResources().getDimensionPixelSize(
					R.dimen.actionbar_compat_button_width);
			final int buttonHeight = mActivity.getResources().getDimensionPixelSize(R.dimen.actionbar_compat_height);
			final int progressIndicatorWidth = buttonWidth / 2;

			LinearLayout.LayoutParams indicatorLayoutParams = new LinearLayout.LayoutParams(progressIndicatorWidth,
					progressIndicatorWidth);
			indicatorLayoutParams.setMargins((buttonWidth - progressIndicatorWidth) / 2,
					(buttonHeight - progressIndicatorWidth) / 2, (buttonWidth - progressIndicatorWidth) / 2, 0);
			indicator.setLayoutParams(indicatorLayoutParams);
			indicator.setVisibility(View.GONE);
			indicator.setId(R.id.actionbar_compat_item_refresh_progress);
			actionBar.addView(indicator);
		}

		return actionButton;
	}

	/**
	 * A {@link android.view.MenuInflater} that reads action bar metadata.
	 */
	private class WrappedMenuInflater extends MenuInflater {
		MenuInflater mInflater;

		public WrappedMenuInflater(Context context, MenuInflater inflater) {
			super(context);
			mInflater = inflater;
		}

		@Override
		public void inflate(int menuRes, Menu menu) {
			loadActionBarMetadata(menuRes);
			mInflater.inflate(menuRes, menu);
		}

		/**
		 * Loads action bar metadata from a menu resource, storing a list of
		 * menu item IDs that
		 * should be shown on-screen (i.e. those with showAsAction set to always
		 * or ifRoom).
		 * 
		 * @param menuResId
		 */
		private void loadActionBarMetadata(int menuResId) {
			XmlResourceParser parser = null;
			try {
				parser = mActivity.getResources().getXml(menuResId);

				int eventType = parser.getEventType();
				int itemId;
				int showAsAction;

				boolean eof = false;
				while (!eof) {
					switch (eventType) {
						case XmlPullParser.START_TAG:
							if (!parser.getName().equals("item")) {
								break;
							}

							itemId = parser.getAttributeResourceValue(MENU_RES_NAMESPACE, MENU_ATTR_ID, 0);
							if (itemId == 0) {
								break;
							}

							showAsAction = parser
									.getAttributeIntValue(MENU_RES_NAMESPACE, MENU_ATTR_SHOW_AS_ACTION, -1);
							if (showAsAction == MenuItem.SHOW_AS_ACTION_ALWAYS
									|| showAsAction == MenuItem.SHOW_AS_ACTION_IF_ROOM) {
								mActionItemIds.add(itemId);
							}
							break;

						case XmlPullParser.END_DOCUMENT:
							eof = true;
							break;
					}

					eventType = parser.next();
				}
			} catch (XmlPullParserException e) {
				throw new InflateException("Error inflating menu XML", e);
			} catch (IOException e) {
				throw new InflateException("Error inflating menu XML", e);
			} finally {
				if (parser != null) {
					parser.close();
				}
			}
		}

	}
}
