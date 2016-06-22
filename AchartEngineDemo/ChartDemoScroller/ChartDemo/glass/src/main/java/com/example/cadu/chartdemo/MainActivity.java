package com.example.cadu.chartdemo;


import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;

// AchartEngine lib imports
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.TimeChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;

/**
 * An {@link Activity} showing a tuggable "Hello World!" card.
 * <p/>
 * The main content view is composed of a one-card {@link CardScrollView} that provides tugging
 * feedback to the user when swipe gestures are detected.
 * If your Glassware intends to intercept swipe gestures, you should set the content view directly
 * and use a {@link com.google.android.glass.touchpad.GestureDetector}.
 *
 * @see <a href="https://developers.google.com/glass/develop/gdk/touch">GDK Developer Guide</a>
 */
public class MainActivity extends Activity {
    // setting to generate de data
    private static final int SERIES_NR = 2;

    // to use as the Cardscroller views
    private List<GraphicalView> cViews;
    private CardScrollView mCardScrollView;
    private ChartViewCardScrollAdapter mAdapter;

    /**
     * Chart {@link View} generated by {@link #buildView()}.
     */
    private View mView;
    private ArrayList<String> months;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        createChartViews();

        mCardScrollView = new CardScrollView(this);
        mAdapter = new ChartViewCardScrollAdapter();
        mCardScrollView.setAdapter(mAdapter);
        mCardScrollView.activate();

        // Handle the TAP event.
        mCardScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Plays disallowed sound to indicate that TAP actions are not supported.
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(Sounds.DISALLOWED);
            }
        });

        setContentView(mCardScrollView);
    }

    private void createChartViews(){
        cViews = new ArrayList<GraphicalView>();

        cViews.add(buildView('b'));
        cViews.add(buildView('l'));
        cViews.add(buildView('s'));
        cViews.add(buildView('t'));
    }

    @Override
    protected void onResume() {
        mCardScrollView.activate();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mCardScrollView.deactivate();
        super.onPause();
    }

    /**
     * Builds a graphical view of the chart
     */
    private GraphicalView buildView(char chartType) {
        GraphicalView gView = null;
        XYMultipleSeriesRenderer renderer = null;

        switch (chartType){
            case 'b':
                // bar chart
                renderer = getBarDemoRenderer();
                setChartSettings(renderer, "Bar Chart", "x Values", "y Values");
                gView = ChartFactory.getBarChartView(this, getBarDemoDataset(), renderer, Type.DEFAULT);
                break;
            case 'l':
                // line chart
                gView = ChartFactory.getLineChartView(this, getDemoDataset(), getDemoRenderer("Line Chart", "x Values", "y Values"));
                break;
            case 's':
                // scatter chart
                gView = ChartFactory.getScatterChartView(this, getDemoDataset(), getDemoRenderer("Scatter Chart", "x Values", "y Values"));
                break;
            case 't':
                // time chart
                gView = ChartFactory.getTimeChartView(this, getDateDemoDataset(), getDemoRenderer("Time Chart", "x Values", "y Values"), null);
                break;
        }

        return gView;
    }

    /**
     * Generates Datasets to be added to a chart
     */
    private XYMultipleSeriesDataset getDemoDataset() {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        final int nr = 10;
        Random r = new Random();
        for (int i = 0; i < SERIES_NR; i++) {
            XYSeries series = new XYSeries("Demo series " + (i + 1));
            for (int k = 0; k < nr; k++) {
                series.add(k, 20 + r.nextInt() % 100);
            }
            dataset.addSeries(series);
        }
        return dataset;
    }

    private XYMultipleSeriesDataset getDateDemoDataset() {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        final int nr = 10;
        long value = new Date().getTime() - 3 * TimeChart.DAY;
        Random r = new Random();
        for (int i = 0; i < SERIES_NR; i++) {
            TimeSeries series = new TimeSeries("Demo series " + (i + 1));
            for (int k = 0; k < nr; k++) {
                series.add(new Date(value + k * TimeChart.DAY / 4), 20 + r.nextInt() % 100);
            }
            dataset.addSeries(series);
        }
        return dataset;
    }
    private XYMultipleSeriesDataset getBarDemoDataset() {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        final int nr = 10;
        Random r = new Random();
        for (int i = 0; i < SERIES_NR; i++) {
            CategorySeries series = new CategorySeries("Demo series " + (i + 1));
            for (int k = 0; k < nr; k++) {
                series.add(100 + r.nextInt() % 100);
            }
            dataset.addSeries(series.toXYSeries());
        }
        return dataset;
    }

    /**
     * Generates the chart appearence
     */
    private XYMultipleSeriesRenderer getDemoRenderer(String title, String xTitle, String yTitle) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        renderer.setPointSize(5f);
        renderer.setMargins(new int[]{30, 40, 15, 15});
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(Color.BLUE);
        r.setPointStyle(PointStyle.SQUARE);
        r.setFillBelowLine(true);
        r.setFillBelowLineColor(Color.WHITE);
        r.setFillPoints(true);
        renderer.addSeriesRenderer(r);
        r = new XYSeriesRenderer();
        r.setPointStyle(PointStyle.CIRCLE);
        r.setColor(Color.GREEN);
        r.setFillPoints(true);
        renderer.addSeriesRenderer(r);
        renderer.setAxesColor(Color.DKGRAY);
        renderer.setShowGridY(true);
        renderer.setLabelsColor(Color.LTGRAY);
        setChartSettings(renderer, title, xTitle, yTitle);
        return renderer;
    }
    public XYMultipleSeriesRenderer getBarDemoRenderer() {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        renderer.setMargins(new int[]{30, 40, 15, 15});
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(Color.BLUE);
        renderer.addSeriesRenderer(r);
        r = new XYSeriesRenderer();
        r.setColor(Color.GREEN);
        renderer.addSeriesRenderer(r);
        return renderer;
    }

    /**
     * Generates the chart settings
     */
    private void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle, String yTitle) {
        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        /*renderer.setXAxisMin(0.5);
        renderer.setXAxisMax(10.5);
        renderer.setYAxisMin(0);
        renderer.setYAxisMax(210);*/
    }

    /**
     * Generates the card scroll chart views adapters
     */
    private class ChartViewCardScrollAdapter extends CardScrollAdapter {

        @Override
        public int getPosition(Object item) {
            return cViews.indexOf(item);
        }

        @Override
        public int getCount() {
            return cViews.size();
        }

        @Override
        public Object getItem(int position) {
            return cViews.get(position);
        }

        @Override
        public int getItemViewType(int position){
            return cViews.get(position).getLayerType();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return cViews.get(position);
        }
    }

}