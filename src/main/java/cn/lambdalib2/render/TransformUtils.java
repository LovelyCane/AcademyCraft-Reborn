package cn.lambdalib2.render;

import cn.lambdalib2.util.MathUtils;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;

public class TransformUtils {

    public static Matrix4f perspective(float fov, float aspect, float zNear, float zFar) {
        return perspective(fov, aspect, zNear, zFar, null);
    }

    public static Matrix4f perspective(float fov, float aspect, float zNear, float zFar, Matrix4f mat) {
        mat = init(mat);
        float yScale = (float) (1 / (Math.tan(Math.toRadians(fov / 2))));
        float xScale = yScale / aspect;
        float frustrumLength = zFar - zNear;

        mat.m00 = xScale;
        mat.m11 = yScale;
        mat.m22 = -((zFar + zNear) / frustrumLength);
        mat.m23 = -1;
        mat.m32 = -((2 * zFar * zNear) / frustrumLength);
        mat.m33 = 0;

        return mat;
    }

    public static Matrix4f translate(float dx, float dy, float dz) {
        return translate(dx, dy, dz, null);
    }

    public static Matrix4f translate(float dx, float dy, float dz, Matrix4f mat) {
        mat = init(mat);
        mat.setIdentity();
        mat.m30 = dx;
        mat.m31 = dy;
        mat.m32 = dz;
        return mat;
    }

    public static Matrix4f scale(float s) {
        return scale(s, s, s);
    }

    public static Matrix4f scale(float s, Matrix4f mat) {
        return scale(s, s, s, mat);
    }

    public static Matrix4f scale(float sx, float sy, float sz) {
        return scale(sx, sy, sz, null);
    }

    public static Matrix4f scale(float sx, float sy, float sz, Matrix4f mat) {
        mat = init(mat);
        mat.m00 = sx;
        mat.m11 = sy;
        mat.m22 = sz;

        return mat;
    }

    public static Matrix4f rotateEuler(float x, float y, float z) {
        return rotateEuler(x, y, z, null);
    }

    // see: https://en.wikipedia.org/wiki/Euler_angles
    public static Matrix4f rotateEuler(float x, float y, float z, Matrix4f mat) {
        mat = init(mat);

        float a3 = MathUtils.toRadians(x);
        float a2 = MathUtils.toRadians(y);
        float a1 = MathUtils.toRadians(z);

        float c1 = MathHelper.cos(a1), s1 = MathHelper.sin(a1);
        float c2 = MathHelper.cos(a2), s2 = MathHelper.sin(a2);
        float c3 = MathHelper.cos(a3), s3 = MathHelper.sin(a3);

        mat.m00 = c1 * c3 - s1 * s2 * s3;
        mat.m01 = c3 * s1 + c1 * s2 * s3;
        mat.m02 = -c2 * s3;

        mat.m10 = -c2 * s1;
        mat.m11 = c1 * c2;
        mat.m12 = s2;

        mat.m20 = c1 * s3 + c3 * s1 * s2;
        mat.m21 = s1 * s3 - c1 * c3 * s2;
        mat.m22 = c2 * c3;

        return mat;
    }

    public static Quaternion eulerToQuaternion(float x, float y, float z) {
        Quaternion q = new Quaternion();
        float f = (float) (Math.PI / 180);
        x *= f; y *= f; z *= f;
        float cz = MathHelper.cos(z * 0.5f);
        float sz = MathHelper.sin(z * 0.5f);
        float cx = MathHelper.cos(x * 0.5f);
        float sx = MathHelper.sin(x * 0.5f);
        float cy = MathHelper.cos(y * 0.5f);
        float sy = MathHelper.sin(y * 0.5f);

        q.w = cz * cx * cy + sz * sx * sy;
        q.x = cz * sx * cy - sz * cx * sy;
        q.y = cz * cx * sy + sz * sx * cy;
        q.z = sz * cx * cy - cz * sx * sy;
        return q;
    }

    public static Matrix4f quaternionToMatrix(Quaternion q) {
        return quaternionToMatrix(q, null);
    }

    public static Matrix4f quaternionToMatrix(Quaternion q, Matrix4f matrix) {
        matrix = init(matrix);
        matrix.m00 = 1.0f - 2.0f * (q.getY() * q.getY() + q.getZ() * q.getZ());
        matrix.m01 = 2.0f * (q.getX() * q.getY() + q.getZ() * q.getW());
        matrix.m02 = 2.0f * (q.getX() * q.getZ() - q.getY() * q.getW());
        matrix.m03 = 0.0f;

        // Second row
        matrix.m10 = 2.0f * (q.getX() * q.getY() - q.getZ() * q.getW());
        matrix.m11 = 1.0f - 2.0f * (q.getX() * q.getX() + q.getZ() * q.getZ());
        matrix.m12 = 2.0f * (q.getZ() * q.getY() + q.getX() * q.getW());
        matrix.m13 = 0.0f;

        // Third row
        matrix.m20 = 2.0f * (q.getX() * q.getZ() + q.getY() * q.getW());
        matrix.m21 = 2.0f * (q.getY() * q.getZ() - q.getX() * q.getW());
        matrix.m22 = 1.0f - 2.0f * (q.getX() * q.getX() + q.getY() * q.getY());
        matrix.m23 = 0.0f;

        // Fourth row
        matrix.m30 = 0;
        matrix.m31 = 0;
        matrix.m32 = 0;
        matrix.m33 = 1.0f;

        return matrix;
    }

    public static javax.vecmath.Matrix4f toJavax(Matrix4f mat) {
        javax.vecmath.Matrix4f ret = new javax.vecmath.Matrix4f();
        ret.m00 = mat.m00;
        ret.m01 = mat.m01;
        ret.m02 = mat.m02;
        ret.m03 = mat.m03;

        ret.m10 = mat.m10;
        ret.m11 = mat.m11;
        ret.m12 = mat.m12;
        ret.m13 = mat.m13;

        ret.m20 = mat.m20;
        ret.m21 = mat.m21;
        ret.m22 = mat.m22;
        ret.m23 = mat.m23;

        ret.m30 = mat.m30;
        ret.m31 = mat.m31;
        ret.m32 = mat.m32;
        ret.m33 = mat.m33;

        ret.transpose();
        return ret;
    }

    private static Matrix4f init(Matrix4f m) {
        if (m == null)
            m = new Matrix4f();
        else
            m.setIdentity();
        return m;
    }

}
