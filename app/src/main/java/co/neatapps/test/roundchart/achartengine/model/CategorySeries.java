/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package co.neatapps.test.roundchart.achartengine.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import co.neatapps.test.roundchart.achartengine.model.*;

/**
 * A series for the category charts like the pie ones.
 */
public class CategorySeries implements Serializable {
    /** The series title. */
    private String mTitle;
    /** The series categories. */
    private List<String> mCategories = new ArrayList<String>();
    /** The series values. */
    private List<Double> mValues = new ArrayList<Double>();

    /**
     * Builds a new category series.
     *
     * @param title the series title
     */
    public CategorySeries(String title) {
        mTitle = title;
    }

    /**
     * Returns the series title.
     *
     * @return the series title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Adds a new value to the series
     *
     * @param value the new value
     */
    public synchronized void add(double value) {
        add(mCategories.size() + "", value);
    }

    /**
     * Adds a new value to the series.
     *
     * @param category the category
     * @param value the new value
     */
    public synchronized void add(String category, double value) {
        mCategories.add(category);
        mValues.add(value);
    }

    /**
     * Replaces the value at the specific index in the series.
     *
     * @param index the index in the series
     * @param category the category
     * @param value the new value
     */
    public synchronized void set(int index, String category, double value) {
        mCategories.set(index, category);
        mValues.set(index, value);
    }

    /**
     * Removes an existing value from the series.
     *
     * @param index the index in the series of the value to remove
     */
    public synchronized void remove(int index) {
        mCategories.remove(index);
        mValues.remove(index);
    }

    /**
     * Removes all the existing values from the series.
     */
    public synchronized void clear() {
        mCategories.clear();
        mValues.clear();
    }

    /**
     * Returns the value at the specified index.
     *
     * @param index the index
     * @return the value at the index
     */
    public synchronized double getValue(int index) {
        return mValues.get(index);
    }

    /**
     * Returns the category name at the specified index.
     *
     * @param index the index
     * @return the category name at the index
     */
    public synchronized String getCategory(int index) {
        return mCategories.get(index);
    }

    /**
     * Returns the series item count.
     *
     * @return the series item count
     */
    public synchronized int getItemCount() {
        return mCategories.size();
    }

    /**
     * Transforms the category series to an XY series.
     *
     * @return the XY series
     */
    public co.neatapps.test.roundchart.achartengine.model.XYSeries toXYSeries() {
        co.neatapps.test.roundchart.achartengine.model.XYSeries xySeries = new co.neatapps.test.roundchart.achartengine.model.XYSeries(mTitle);
        int k = 0;
        for (double value : mValues) {
            xySeries.add(++k, value);
        }
        return xySeries;
    }
}
