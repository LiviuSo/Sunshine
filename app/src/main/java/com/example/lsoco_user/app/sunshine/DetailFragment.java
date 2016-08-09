package com.example.lsoco_user.app.sunshine;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lsoco_user.app.sunshine.data.WeatherContract;

import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int    DETAIL_LOADER          = 0;
    private final String LOG_TAG                = DetailFragment.class.getSimpleName();
    private final String FORECAST_SHARE_HASHTAG = " #unshineApp";
    private String              mForecast;
    private ShareActionProvider mShareActionProvider;
    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection change
    private static final int COL_WEATHER_ID       = 0;
    private static final int COL_WEATHER_DATE     = 1;
    private static final int COL_WEATHER_DESC     = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUM      = 5;
    private static final int COL_WEATHER_SPEED    = 6;
    private static final int COL_WEATHER_DIR      = 7;
    private static final int COL_WEATHER_PRESS    = 8;
    private static final int COL_WEATHER_COND_ID  = 9;

    private TextView  tvDate;
    private TextView  tvMax;
    private TextView  tvMin;
    private TextView  tvDescr;
    private TextView  tvHumidity;
    private TextView  tvPress;
    private TextView  tvWind;
    private ImageView ivIcon;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        tvDate = (TextView) view.findViewById(R.id.details_date);
        tvDescr = (TextView) view.findViewById(R.id.details_forecast);
        tvHumidity = (TextView) view.findViewById(R.id.details_humidity);
        tvMax = (TextView) view.findViewById(R.id.details_high);
        tvMin = (TextView) view.findViewById(R.id.details_low);
        tvPress = (TextView) view.findViewById(R.id.details_pressure);
        tvWind = (TextView) view.findViewById(R.id.details_wind);
        ivIcon = (ImageView) view.findViewById(R.id.details_icon);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if (mForecast != null) {
            mShareActionProvider.setShareIntent(createForecastShareIntent());
        }
    }

    private Intent createForecastShareIntent() {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);
        return sendIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        return new CursorLoader(getActivity(),
                                intent.getData(),
                                FORECAST_COLUMNS,
                                null,
                                null,
                                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) {
            return;
        }

        String dateString = Utility.formatDate(
                data.getLong(COL_WEATHER_DATE));

        String weatherDescription =
                data.getString(COL_WEATHER_DESC);

        boolean isMetric = Utility.isMetric(getActivity());

        String high = Utility.formatTemperature(getActivity(),
                                                data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);

        String low = Utility.formatTemperature(getActivity(),
                                               data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

        String humidityString = getString(R.string.format_humidity, data.getFloat(COL_WEATHER_HUM));

        String pressureString = getString(R.string.format_pressure, data.getFloat(COL_WEATHER_PRESS));

        String windString = Utility.getFormattedWind(getActivity(),
                                                     data.getFloat(COL_WEATHER_SPEED),
                                                     data.getFloat(COL_WEATHER_DIR));
        int weatherId = data.getInt(COL_WEATHER_COND_ID);

        // use for sharing
        mForecast = String.format("%s - %s - %s/%s %s %s %s",
                                  dateString, weatherDescription, high, low,
                                  humidityString, pressureString, windString);

        tvDate.setText(dateString);
        tvDescr.setText(weatherDescription);
        tvMax.setText(high);
        tvMin.setText(low);
        tvHumidity.setText(humidityString);
        tvPress.setText(pressureString);
        tvWind.setText(windString);
        ivIcon.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createForecastShareIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}