package org.intellij.sdk.editor;

/**
 * The DataCharts class is an object to store the charts to be displayed in the main plugin dialog
 */

public class DataCharts {
    private int metric_DomainID;
    private int metric_line;
    private byte[] Chart;

    public int getMetric_DomainID() { return metric_DomainID; }
    public int getMetric_line() { return metric_line; }
    public byte[] getChart() { return Chart; }

    public void setMetric_DomainID(int metric_DomainID) {
        this.metric_DomainID = metric_DomainID;
    }

    public void setMetric_line(int metric_line) {
        this.metric_line = metric_line;
    }

    public void setChart(byte[] chart) {
        Chart = chart;
    }
}
