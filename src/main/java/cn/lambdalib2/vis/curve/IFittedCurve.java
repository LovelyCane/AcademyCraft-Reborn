/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.vis.curve;

/**
 * This interface represents a Fitted Curve (拟合曲线).
 * Add control points into the curve and the interface generates a reasonable curve for it.
 * @author WeAthFolD
 */
public interface IFittedCurve {
    class Point implements Comparable<Point> {
        public double x, y;
        public Point(double _x, double _y) {
            x = _x;
            y = _y;
        }
        
        @Override
        public int compareTo(Point arg0) {
            return Double.compare(x, arg0.x);
        }
    }
    
    /**
     * Add a point to fit for the curve.
     * Note that normally you shouldn't add the same point twice. Bad things like zero division might occur.
     */
    void addPoint(double x, double y);
    
    /**
     * Get the yval of the curve at given x coordinate.
     */
    double valueAt(double x);

    /**
     * @return The control point with index i
     */
    Point getPoint(int i);
    
    /**
     * Remove all the control points and reset the curve.
     */
    void reset();
}