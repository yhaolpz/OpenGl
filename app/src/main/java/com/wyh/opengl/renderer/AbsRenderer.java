package com.wyh.opengl.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.wyh.opengl.GLUtil;

import java.nio.FloatBuffer;

/**
 * @author WangYingHao
 * @since 2019-06-09
 */
public abstract class AbsRenderer implements GLSurfaceView.Renderer {
    /**
     * float 类型所占字节数
     */
    private static final int BYTE_PER_FLOAT = 4;
    /**
     * 着色器程序
     */
    protected int program;
    /**
     * 顶点着色器
     */
    protected int vertexShader;
    /**
     * 片元着色器
     */
    protected int fragmentShader;
    /**
     * 顶点坐标数据
     */
    protected FloatBuffer vertexFloatBuffer;
    /**
     * 顶点之间的偏移量
     */
    protected int vertexStride;
    /**
     * 顶点个数
     */
    protected int vertexCount;

    /**
     * 设置顶点着色器源码
     */
    protected abstract String getVertexShaderResource();

    /**
     * 设置片元着色器源码
     */
    protected abstract String getFragmentShaderResource();
    /**
     * 设置顶点坐标
     */
    protected abstract float[] getVertexCoords();

    /**
     * 用几个数值来表示一个顶点
     */
    protected abstract int getPerVertex();


    /**
     * 初始化着色器程序
     */
    protected void initProgram() {
        vertexShader = GLUtil.compileShader(GLES20.GL_VERTEX_SHADER, getVertexShaderResource());
        fragmentShader = GLUtil.compileShader(GLES20.GL_FRAGMENT_SHADER, getFragmentShaderResource());
        program = GLUtil.buildProgram(vertexShader, fragmentShader);
        vertexFloatBuffer = GLUtil.floatToBuffer(getVertexCoords());
        vertexCount = getVertexCoords().length / getPerVertex();
        vertexStride = vertexCount * BYTE_PER_FLOAT;
    }

    /**
     * 删除着色器程序
     */
    public final void deleteProgram() {
        if (program != GLUtil.NO_PROGRAM) {
            GLES20.glDeleteProgram(program);
            program = 0;
            vertexShader = 0;
            fragmentShader = 0;
        }
    }

}
