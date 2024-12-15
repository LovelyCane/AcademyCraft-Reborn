/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.vis.curve;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.lambdalib2.vis.curve.IFittedCurve;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * @author WeAthFolD
 */
public class CubicCurve implements IFittedCurve {
    
    private List<Point> pts = new ArrayList();

    public CubicCurve() {}

    @Override
    public void addPoint(double x, double y) {
        pts.add(new Point(x, y));
        Collections.sort(pts);
    }

    @Override
    public double valueAt(double x) {
        if(pts.size() == 0)
            return 0;
        
        int index = 0;
        for(; index < pts.size() && pts.get(index).x < x; ++index);
        // index being 1st point where pt.x >= x, or index==size
        if(index == pts.size()) {
            Point p2 = pts.get(pts.size() - 1);
            double k = pts.size() >= 2 ? ik(index - 1, index - 2) : 0;
            return p2.y + (x - p2.x) * k;
        }
        
        if(index == 0) {
            Point p0 = getPoint(0);
            return p0.y + k(0, 1) * (x - p0.x);
        }
        
        Point p0 = getPoint(index - 1), p1 = getPoint(index);
        double l = p1.x - p0.x;
        double t = (x - p0.x) / l, t2 = t * t, t3 = t2 * t;
        double y0 = p0.y, y1 = p1.y, m0 = k(index - 1, l), m1 = k(index, l);
        
        double val = 
                t3 * (m0 + m1 + 2*y0 - 2*y1)    +
                t2 * (-2*m0 - m1 - 3*y0 + 3*y1) +
                t  * (m0)                         +
                y0;
        //System.out.printf("%.2f #(%d, %d) [%.3f, %.3f]\n", x, index-1, index, t, val);
        return val;
    }
    
    private double k(int i, double l) {
        double ret;
        if(i == 0) {
            ret = pts.size() == 1 ? 0 : ik(i, i + 1);
        } else if(i == pts.size() - 1){
            ret = ik(i, i - 1);
        } else {
            ret = 0.5 * (ik(i+1, i) + ik(i, i-1));
        }
        
        return ret * l;
    }
    
    private double ik(int i1, int i2) {
        Point p1 = pts.get(i1), p2 = pts.get(i2);
        return (p2.y - p1.y) / (p2.x - p1.x);
    }

    @Override
    public int pointCount() {
        return pts.size();
    }

    @Override
    public Point getPoint(int i) {
        return pts.get(i);
    }
    
    public static TypeAdapter<CubicCurve> adapter = new TypeAdapter<CubicCurve>() {

        @Override
        public void write(JsonWriter out, CubicCurve value) throws IOException {
            out.beginArray();
            for(Point pt : value.pts) {
                out.beginArray()
                    .value(pt.x).value(pt.y)
                .endArray();
            }
            out.endArray();
        }

        @Override
        public CubicCurve read(JsonReader in) throws IOException {
            CubicCurve ret = new CubicCurve();
            in.beginArray();
            JsonToken token;
            while((token = in.peek()) != JsonToken.END_ARRAY) {
                in.beginArray();
                ret.addPoint(in.nextDouble(), in.nextDouble());
                in.endArray();
            }
            in.endArray();
            return ret;
        }
        
    };

    @Override
    public void reset() {
        pts.clear();
    }

}
