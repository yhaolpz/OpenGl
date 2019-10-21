package com.wyh.opengl.renderer;

import android.opengl.GLES30;
import android.os.Looper;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author WangYingHao
 * @since 2019-06-09
 */
public class Triangle extends AbsRenderer {
    private static final String TAG = "[GL]Triangle";

    private final float mColor[] = {1.0f, 0f, 0f, 1.0f}; //red


    @Override
    protected String getVertexShaderResource() {
        return "attribute vec4 vPosition;" +
                "void main() {" +
                "  gl_Position = vPosition;" +
                "}";
    }

    @Override
    protected String getFragmentShaderResource() {
        return "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}";
    }

    @Override
    protected float[] getVertexCoords() {
        return new float[]{
                0.0f, 0.5f, 0.0f, // top
                -0.5f, -0.5f, 0.0f, // bottom left
                0.5f, -0.5f, 0.0f  // bottom right
        };
    }

    @Override
    protected int getPerVertex() {
        return 3;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置清空屏幕后的背景色
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        initProgram();
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Log.d(TAG, "onSurfaceCreated: main thread ");
        }else{
            Log.d(TAG, "onSurfaceCreated: other thread:" + Thread.currentThread().getId());
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Log.d(TAG, "onSurfaceChanged: main thread ");
        }else{
            Log.d(TAG, "onSurfaceChanged: other thread:" + Thread.currentThread().getId());
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Log.d(TAG, "onDrawFrame: main thread ");
        }else{
            Log.d(TAG, "onDrawFrame: other thread:" + Thread.currentThread().getId());
        }
        //清空屏幕，擦除屏幕上所有的颜色，用glClearColor定义的颜色填充
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        //将程序加入到OpenGLES环境
        GLES30.glUseProgram(program);
        //获取顶点着色器的vPosition成员句柄
        int positionHandle = GLES30.glGetAttribLocation(program, "vPosition");
        //启用三角形顶点的句柄
        GLES30.glEnableVertexAttribArray(positionHandle);
        //设置三角形的顶点坐标数据
        GLES30.glVertexAttribPointer(positionHandle, getPerVertex(),
                GLES30.GL_FLOAT, false,
                vertexStride, vertexFloatBuffer);
        //获取片元着色器的vColor成员的句柄
        int colorHandle = GLES30.glGetUniformLocation(program, "vColor");
        //设置绘制三角形的颜色
        GLES30.glUniform4fv(colorHandle, 1, mColor, 0);
        //绘制三角形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, vertexCount);
        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(positionHandle);
    }
}
