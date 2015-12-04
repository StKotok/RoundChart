package co.neatapps.test.roundchart;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import co.neatapps.test.roundchart.achartengine.ChartFactory;
import co.neatapps.test.roundchart.achartengine.GraphicalView;
import co.neatapps.test.roundchart.achartengine.model.CategorySeries;
import co.neatapps.test.roundchart.achartengine.renderer.DefaultRenderer;
import co.neatapps.test.roundchart.achartengine.renderer.SimpleSeriesRenderer;

public class FullscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_fullscreen);
    }

    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.rl) {
            RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);
            GraphicalView pieGraph = getPieGraph(this);
            rl.addView(pieGraph);
        }

    }

    public GraphicalView getPieGraph(Context context) {

        int[] values = {1, 2, 3, 4, 5};
        CategorySeries series = new CategorySeries("Pie Graph");
        int k = 0;
        for (int value : values) {
            series.add("Section " + ++k, value);
        }

        int[] colors = new int[]{Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN};

        DefaultRenderer renderer = new DefaultRenderer();
        for (int color : colors) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(color);
            renderer.addSeriesRenderer(r);
        }
        renderer.setChartTitle("Pie Chart Demo");
        renderer.setChartTitleTextSize(7);

        return ChartFactory.getPieChartView(context, series, renderer);
    }

}
