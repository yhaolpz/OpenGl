package com.wyh.opengl.renderer;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 绘制三角形
 *
 * @author WangYingHao
 * @since 2019-06-15
 */
public class Triangle2 implements GLSurfaceView.Renderer {

    //顶点着色器
    private static final String vertexShaderResource =
            "attribute vec3 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vec4(vPosition.x, vPosition.y, vPosition.z, 1.0);" +
                    "}";
    //片段着色器
    private static final String fragmentShaderResource =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    //顶点
    private final float[] vertexCoords = new float[]{
            0.0f, 0.5f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f  // bottom right
    };

    private final float color[] = {1.0f, 0f, 0f, 1.0f}; //red

    // 着色器程序
    private int mProgram;
    // 顶点坐标数据
    private FloatBuffer vertexFloatBuffer;


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置清空屏幕后的背景色
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        //构建顶点着色器
        int vertexShader = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER);
        GLES30.glShaderSource(vertexShader, vertexShaderResource);
        GLES30.glCompileShader(vertexShader);
        //构建片段着色器
        int fragmentShader = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER);
        GLES30.glShaderSource(fragmentShader, fragmentShaderResource);
        GLES30.glCompileShader(fragmentShader);
        //构建着色器程序，并将顶点着色器和片段着色器链接进来
        mProgram = GLES30.glCreateProgram();
        GLES30.glAttachShader(mProgram, vertexShader);
        GLES30.glAttachShader(mProgram, fragmentShader);
        GLES30.glLinkProgram(mProgram);
        //顶点着色器和片段着色器链接到着色器程序后就无用了
        GLES30.glDeleteShader(vertexShader);
        GLES30.glDeleteShader(fragmentShader);
        //转换为需要的顶点数据格式
        vertexFloatBuffer = floatToBuffer(vertexCoords);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置视窗
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //清空屏幕，擦除屏幕上所有的颜色，用 glClearColor 定义的颜色填充
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        //在当前 EGL 环境激活着色器程序
        GLES30.glUseProgram(mProgram);
        //获取顶点着色器的 vPosition 成员句柄
        int positionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition");
        //启用句柄
        GLES30.glEnableVertexAttribArray(positionHandle);
        //设置顶点坐标数据
        GLES30.glVertexAttribPointer(positionHandle, 3, GLES30.GL_FLOAT,
                false, 3 * 4, vertexFloatBuffer);
        //获取片元着色器的 vColor 成员句柄
        int colorHandle = GLES30.glGetUniformLocation(mProgram, "vColor");
        //设置颜色
        GLES30.glUniform4fv(colorHandle, 1, color, 0);
        //绘制三角形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 3);
        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(positionHandle);
    }

    private FloatBuffer floatToBuffer(float[] a) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(a.length * 4); //float占4个字节
        buffer.order(ByteOrder.nativeOrder());
        FloatBuffer byteBuffer = buffer.asFloatBuffer();
        byteBuffer.put(a);
        byteBuffer.position(0);
        return byteBuffer;
    }
}
