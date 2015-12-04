/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package co.neatapps.test.roundchart.achartengine.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;

import java.util.ArrayList;
import java.util.List;

import co.neatapps.test.roundchart.achartengine.model.CategorySeries;
import co.neatapps.test.roundchart.achartengine.model.Point;
import co.neatapps.test.roundchart.achartengine.model.SeriesSelection;
import co.neatapps.test.roundchart.achartengine.renderer.DefaultRenderer;
import co.neatapps.test.roundchart.achartengine.renderer.SimpleSeriesRenderer;

/**
 * The pie chart rendering class.
 */
public class PieChartStickOut extends RoundChart {
    /**
     * Handles returning values when tapping on PieChart.
     */
    private PieMapper mPieMapper;

    /**
     * Builds a new pie chart instance.
     *
     * @param dataset  the series dataset
     * @param renderer the series renderer
     */
    public PieChartStickOut(CategorySeries dataset, DefaultRenderer renderer) {
        super(dataset, renderer);
        mPieMapper = new PieMapper();
    }

    /**
     * The graphical representation of the pie chart.
     *
     * @param canvas the canvas to paint to
     * @param x      the top left x value of the view to draw to
     * @param y      the top left y value of the view to draw to
     * @param width  the width of the view to draw to
     * @param height the height of the view to draw to
     * @param paint  the paint
     */
    @Override
    public void draw(Canvas canvas, int x, int y, int width, int height, Paint paint) {
        paint.setAntiAlias(mRenderer.isAntialiasing());
        paint.setStyle(Style.FILL);
        paint.setTextSize(mRenderer.getLabelsTextSize());
        int legendSize = getLegendSize(mRenderer, height / 5, 0);
        int left = x;
        int top = y;
        int right = x + width;
        int sLength = mDataset.getItemCount();
        double total = 0;
        String[] titles = new String[sLength];
        for (int i = 0; i < sLength; i++) {
            total += mDataset.getValue(i);
            titles[i] = mDataset.getCategory(i);
        }
        if (mRenderer.isFitLegend()) {
            legendSize = drawLegend(canvas, mRenderer, titles, left, right, y, width, height, legendSize,
                    paint, true);
        }
        int bottom = y + height - legendSize;
        drawBackground(mRenderer, canvas, x, y, width, height, paint, false, DefaultRenderer.NO_COLOR);

        float currentAngle = mRenderer.getStartAngle();
        int mRadius = Math.min(Math.abs(right - left), Math.abs(bottom - top));
        int radius = (int) (mRadius * 0.35 * mRenderer.getScale());

        if (mCenterX == NO_VALUE) {
            mCenterX = (left + right) / 2;
        }
        if (mCenterY == NO_VALUE) {
            mCenterY = (bottom + top) / 2;
        }

        // Hook in clip detection after center has been calculated
        mPieMapper.setDimensions(radius, mCenterX, mCenterY);
        boolean loadPieCfg = !mPieMapper.areAllSegmentPresent(sLength);
        if (loadPieCfg) {
            mPieMapper.clearPieSegments();
        }

        float shortRadius = radius * 0.9f;
        float longRadius = radius * 1.1f;
        RectF oval = new RectF(mCenterX - radius, mCenterY - radius, mCenterX + radius, mCenterY + radius);
        List<RectF> prevLabelsBounds = new ArrayList<RectF>();

        for (int i = 0; i < sLength; i++) {
            SimpleSeriesRenderer seriesRenderer = mRenderer.getSeriesRendererAt(i);
            if (seriesRenderer.isGradientEnabled()) {
                RadialGradient grad = new RadialGradient(mCenterX, mCenterY, longRadius,
                        seriesRenderer.getGradientStartColor(), seriesRenderer.getGradientStopColor(),
                        TileMode.MIRROR);
                paint.setShader(grad);
            } else {
                paint.setColor(seriesRenderer.getColor());
            }

            float value = (float) mDataset.getValue(i);
            float angle = (float) (value / total * 360);
            if (seriesRenderer.isHighlighted()) {
                double rAngle = Math.toRadians(90 - (currentAngle + angle / 2));
                float translateX = (float) (radius * 0.1 * Math.sin(rAngle));
                float translateY = (float) (radius * 0.1 * Math.cos(rAngle));
                oval.offset(translateX, translateY);
                canvas.drawArc(oval, currentAngle, angle, true, paint);
                oval.offset(-translateX, -translateY);
            } else {
                canvas.drawArc(oval, currentAngle, angle, true, paint);
            }
            paint.setColor(seriesRenderer.getColor());
            paint.setShader(null);
            drawLabel(canvas, mDataset.getCategory(i), mRenderer, prevLabelsBounds, mCenterX, mCenterY,
                    shortRadius, longRadius, currentAngle, angle, left, right, mRenderer.getLabelsColor(),
                    paint, true, false);
            if (mRenderer.isDisplayValues()) {
                drawLabel(
                        canvas,
                        getLabel(mRenderer.getSeriesRendererAt(i).getChartValuesFormat(), mDataset.getValue(i)),
                        mRenderer, prevLabelsBounds, mCenterX, mCenterY, shortRadius / 2, longRadius / 2,
                        currentAngle, angle, left, right, mRenderer.getLabelsColor(), paint, false, true);
            }

            // Save details for getSeries functionality
            if (loadPieCfg) {
                mPieMapper.addPieSegment(i, value, currentAngle, angle);
            }
            currentAngle += angle;
        }
        prevLabelsBounds.clear();
        drawLegend(canvas, mRenderer, titles, left, right, y, width, height, legendSize, paint, false);
        drawTitle(canvas, x, y, width, paint);
    }

    protected void drawLabel(Canvas canvas, String labelText, DefaultRenderer renderer,
                             List<RectF> prevLabelsBounds, int centerX, int centerY, float shortRadius, float longRadius,
                             float currentAngle, float angle, int left, int right, int color, Paint paint, boolean line,
                             boolean display) {
        if (renderer.isShowLabels() || display) {
            paint.setColor(color);
            double rAngle = Math.toRadians(90 - (currentAngle + angle / 2));
            double sinValue = Math.sin(rAngle);
            double cosValue = Math.cos(rAngle);
            int x1 = Math.round(centerX + (float) (shortRadius * sinValue));
            int y1 = Math.round(centerY + (float) (shortRadius * cosValue));
            int x2 = Math.round(centerX + (float) (longRadius * sinValue));
            int y2 = Math.round(centerY + (float) (longRadius * cosValue));

            float size = renderer.getLabelsTextSize();
            float extra = Math.max(size / 2, 10);
            paint.setTextAlign(Paint.Align.LEFT);
            if (x1 > x2) {
                extra = -extra;
                paint.setTextAlign(Paint.Align.RIGHT);
            }
            float xLabel = x2 + extra;
            float yLabel = y2;
            float width = right - xLabel;
            if (x1 > x2) {
                width = xLabel - left;
            }
            labelText = AbstractChart.getFitText(labelText, width, paint);
            float widthLabel = paint.measureText(labelText);
            boolean okBounds = false;
            while (!okBounds && line) {
                boolean intersects = false;
                int length = prevLabelsBounds.size();
                for (int j = 0; j < length && !intersects; j++) {
                    RectF prevLabelBounds = prevLabelsBounds.get(j);
                    if (prevLabelBounds.intersects(xLabel, yLabel, xLabel + widthLabel, yLabel + size)) {
                        intersects = true;
                        yLabel = Math.max(yLabel, prevLabelBounds.bottom);
                    }
                }
                okBounds = !intersects;
            }

            if (line) {
                y2 = (int) (yLabel - size / 2);
                canvas.drawLine(centerX, centerY, x2, y2, paint);
                canvas.drawLine(x2, y2, x2 + extra, y2, paint);
                int radius = 4;
                RectF rectF = new RectF(x2 - radius, y2 - radius, x2 + radius, y2 + radius);
                Paint paintSmallOval = new Paint();
                paintSmallOval.setColor(Color.WHITE);
                canvas.drawOval(rectF, paintSmallOval);
            } else {
                paint.setTextAlign(Paint.Align.CENTER);
            }
            canvas.drawText(labelText, xLabel, yLabel, paint);
            if (line) {
                prevLabelsBounds.add(new RectF(xLabel, yLabel, xLabel + widthLabel, yLabel + size));
            }
        }
    }

    public SeriesSelection getSeriesAndPointForScreenCoordinate(Point screenPoint) {
        return mPieMapper.getSeriesAndPointForScreenCoordinate(screenPoint);
    }

}
