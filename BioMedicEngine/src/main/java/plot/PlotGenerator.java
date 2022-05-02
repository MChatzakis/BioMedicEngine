/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plot;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Manos Chatzakis
 */
public class PlotGenerator {

    /**
     * Generates a plot.
     *
     * @param points
     * @param filepath
     * @param title
     */
    public void generatePRPlot(List<PlotPoint> points, String filepath, String title, String labelX, String labelY) {

        ArrayList<Double> xPoints = new ArrayList<>();
        ArrayList<Double> yPoints = new ArrayList<>();

        for (PlotPoint p : points) {
            xPoints.add(p.getX());
            yPoints.add(p.getY());
        }

        Plot plot = Plot.plot(Plot.plotOpts().
                title(title).
                titleFont(new Font("Arial", Font.BOLD, 16)).
                width(800).
                height(600).
                padding(50). // padding for the whole image
                plotPadding(30).
                labelPadding(20).
                labelFont(new Font("Arial", Font.PLAIN, 14)).
                legend(Plot.LegendFormat.BOTTOM)).
                xAxis(labelX, Plot.axisOpts().
                        range(0, 1)).
                yAxis(labelY, Plot.axisOpts().
                        range(0, 1)).
                series(title, Plot.data().
                        xy(xPoints, yPoints),
                        Plot.seriesOpts().
                                marker(Plot.Marker.DIAMOND).
                                markerColor(Color.GREEN).
                                color(Color.BLACK));

        try {
            plot.save(filepath, "png");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
