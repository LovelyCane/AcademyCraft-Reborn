/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.vis.curve;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author WeAthFolD
 */
public class CubicCurve implements IFittedCurve {
    private final List<Point> pts = new ArrayList<>();

    public CubicCurve() {}

    @Override
    public void addPoint(double x, double y) {
        pts.add(new Point(x, y));
        Collections.sort(pts);
    }

    @Override
    public double valueAt(double x) {
        if(pts.isEmpty())
            return 0;

        Point p0 = getPoint(0);
        return p0.y + k() * (x - p0.x);

    }
    
    private double k() {
        double ret;
        ret = pts.size() == 1 ? 0 : ik();

        return ret;
    }
    
    private double ik() {
        Point p1 = pts.get(0), p2 = pts.get(1);
        return (p2.y - p1.y) / (p2.x - p1.x);
    }

    @Override
    public Point getPoint(int i) {
        return pts.get(i);
    }

    @Override
    public void reset() {
        pts.clear();
    }
}
