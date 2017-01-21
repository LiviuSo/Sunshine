package com.example.lsoco_user.app.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private final int VIEW_TYPE_TODAY      = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;
    private boolean mUseTodaySpecialLayout;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public void setUseTodaySpecialLayout(boolean useTodaySpecialLayout) {
        mUseTodaySpecialLayout = useTodaySpecialLayout;
    }

    @Override
    public int getItemViewType(int position) {
        int id = VIEW_TYPE_FUTURE_DAY;
        if(mUseTodaySpecialLayout && position == 0) {
            id = VIEW_TYPE_TODAY;
        }
        return id;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /*
            Remember that these views are reused as needed.
         */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = (viewType == VIEW_TYPE_TODAY) ? R.layout.list_item_forecast_today : R.layout.list_item_forecast;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        TextView tvDate = ((ViewHolder) view.getTag()).dateView;
        TextView tvForecast = ((ViewHolder) view.getTag()).descrView;
        TextView tvHigh = ((ViewHolder) view.getTag()).highTempView;
        TextView tvLow = ((ViewHolder) view.getTag()).lowTempView;
        ImageView ivIcon = ((ViewHolder) view.getTag()).iconView;

        boolean isMetric = Utility.isMetric(context);
        tvDate.setText(Utility.getFriendlyDayString(context, Long.parseLong(cursor.getString(ForecastFragment.COL_WEATHER_DATE))));
        tvForecast.setText(cursor.getString(ForecastFragment.COL_WEATHER_DESC).trim());
        tvHigh.setText(Utility.formatTemperature(context, Double.parseDouble(cursor.getString(ForecastFragment.COL_WEATHER_MAX_TEMP)), isMetric));
        tvLow.setText(Utility.formatTemperature(context, Double.parseDouble(cursor.getString(ForecastFragment.COL_WEATHER_MIN_TEMP)), isMetric));

        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        int iconId;
        if ((getItemViewType(cursor.getPosition()) == VIEW_TYPE_TODAY)) {
            iconId = Utility.getArtResourceForWeatherCondition(weatherId);
        } else {
            iconId = Utility.getIconResourceForWeatherCondition(weatherId);
        }
        ivIcon.setImageResource(iconId);
    }

    public static class ViewHolder {

        public final ImageView iconView;
        public final TextView  dateView;
        public final TextView  descrView;
        public final TextView  highTempView;
        public final TextView  lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descrView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }
}