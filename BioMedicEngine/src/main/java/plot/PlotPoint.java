/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package plot;

import lombok.Data;

/**
 *
 * @author manos
 */
@Data
public class PlotPoint {

    double x, y;

    public PlotPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "[" + x + "," + y + "]";
    }

}
