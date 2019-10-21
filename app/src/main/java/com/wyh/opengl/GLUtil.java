package com.wyh.opengl;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author WangYingHao
 * @since 2019/6/6
 */
public class GLUtil {
    /**
     * 空纹理
     */
    public static final int NO_TEXTURE = 0;
    public static final int NO_FRAMEBUFFER = 0;
    public static final int NO_PROGRAM = 0;
    private static final String TAG = "[GLUtil]";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    /**
     * 日志输出
     */
    public static void d(String tag, String content) {
        Log.d(TAG, tag + "--->" + content);
    }

    /**
     * 日志输出
     */
    private static void e(String tag, String content) {
        Log.e(TAG, tag + "--->" + content);
    }


    public static FloatBuffer floatToBuffer(float[] a) {
        //先初始化buffer，数组的长度*4，因为一个float占4个字节
        ByteBuffer mbb = ByteBuffer.allocateDirect(a.length * 4);
        //数组排序用nativeOrder
        mbb.order(ByteOrder.nativeOrder());
        FloatBuffer byteBuffer = mbb.asFloatBuffer();
        byteBuffer.put(a);
        byteBuffer.position(0);
        return byteBuffer;
    }

    /**
     * 读取assets目录下文件
     */
    public static String readAssetsText(String filePath) {
        try {
            InputStream is = App.get().getAssets().open(filePath);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取assets目录下图片
     */
    public static Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = App.get().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 加载纹理
     *
     * @param image   Bitmap
     * @param recycle 是否自动回收Bitmap
     * @return 纹理
     */
    public static int loadTexture(@NonNull final Bitmap image, final boolean recycle) {
        final int[] textureObjectId = new int[1];
        GLES20.glGenTextures(1, textureObjectId, 0);
        if (textureObjectId[0] <= 0) {
            GLUtil.e(TAG, "创建纹理失败");
            return 0;
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectId[0]);
        //下面是设置图片放大缩小后如何选择像素点进行优化处理来让图片尽量保持清晰
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, image, 0);
        if (recycle) {
            image.recycle();
        }
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return textureObjectId[0];
    }

    /**
     * 加载纹理
     *
     * @param textureWidth  宽
     * @param textureHeight 高
     * @return 纹理
     */
    public static int loadTexture(final int textureWidth, final int textureHeight) {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, textureWidth, textureHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);    //copy到bind的纹理对象中
        return textures[0];
    }


    /**
     * 编译着色器
     *
     * @param type       着色器类型 {@link GLES20#GL_VERTEX_SHADER} {@link GLES20#GL_FRAGMENT_SHADER}
     * @param shaderCode 着色器源码
     * @return 着色器 shader id
     */
    public static int compileShader(int type, String shaderCode) {
        //根据type创建顶点着色器或者片元着色器
        final int shaderObjectId = GLES20.glCreateShader(type);
        if (shaderObjectId <= 0) {
            GLUtil.e(TAG, "创建shader失败");
            return 0;
        }
        //将资源加入到着色器中，并编译
        GLES20.glShaderSource(shaderObjectId, shaderCode);
        GLES20.glCompileShader(shaderObjectId);
        if (GLUtil.DEBUG) {
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
            GLUtil.d(TAG, "GL_COMPILE_STATUS: " + compileStatus[0]
                    + "\n日志:" + GLES20.glGetShaderInfoLog(shaderObjectId));
            if (compileStatus[0] == 0) {
                GLES20.glDeleteShader(shaderObjectId);
                return 0;
            }
        }
        return shaderObjectId;
    }

    /**
     * 构建program
     *
     * @param vertexShaderId   顶点着色器shader id
     * @param fragmentShaderId 片段着色器shader id
     * @return program
     */
    public static int buildProgram(int vertexShaderId, int fragmentShaderId) {
        int program;
        program = createAndLinkProgram(vertexShaderId, fragmentShaderId);
        boolean validated = validateProgram(program);
        if (!validated) {
            GLUtil.e(TAG, "buildProgram createAndLinkProgram validated");
            return 0;
        }
        return program;
    }

    /**
     * 构建program
     *
     * @param vertexShaderId   顶点着色器
     * @param fragmentShaderId 片段着色器
     * @return program
     */
    private static int createAndLinkProgram(int vertexShaderId, int fragmentShaderId) {
        final int programObjectId = GLES20.glCreateProgram();
        if (programObjectId <= 0) {
            GLUtil.e(TAG, "创建program失败");
            return 0;
        }
        GLES20.glAttachShader(programObjectId, vertexShaderId);
        GLES20.glAttachShader(programObjectId, fragmentShaderId);
        GLES20.glLinkProgram(programObjectId);
        if (GLUtil.DEBUG) {
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programObjectId, GLES20.GL_LINK_STATUS, linkStatus, 0);
            GLUtil.d(TAG, "GL_LINK_STATUS：" + linkStatus[0]
                    + "\n日志:" + GLES20.glGetProgramInfoLog(programObjectId));
            if (linkStatus[0] == 0) {
                GLES20.glDeleteProgram(programObjectId);
                return 0;
            }
        }
        return programObjectId;
    }

    /**
     * 判断 program 是否可用
     *
     * @param programObjectId program
     * @return 是否可用
     */
    private static boolean validateProgram(int programObjectId) {
        GLES20.glValidateProgram(programObjectId);
        final int[] validateStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0);
        GLUtil.d(TAG, "GL_VALIDATE_STATUS：" + validateStatus[0]
                + "\n日志:" + GLES20.glGetProgramInfoLog(programObjectId));
        return validateStatus[0] != 0;
    }


}
