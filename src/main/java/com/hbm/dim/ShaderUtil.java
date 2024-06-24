package com.hbm.dim;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.hbm.lib.RefStrings;

public class ShaderUtil {
    private int shaderProgram;
    private int vertexShader;
    private int fragmentShader;
    private int resolutionUniform;
    private int mouseUniform;
    private int timeUniform;
    private int channel1Uniform;

    public ShaderUtil() {
        vertexShader = loadShader(RefStrings.MODID, "shaders/vertex_shader.glsl", GL20.GL_VERTEX_SHADER);
        fragmentShader = loadShader(RefStrings.MODID, "shaders/fragment_shader.glsl", GL20.GL_FRAGMENT_SHADER);
        
        shaderProgram = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgram, vertexShader);
        GL20.glAttachShader(shaderProgram, fragmentShader);
        GL20.glLinkProgram(shaderProgram);
        
        if (GL20.glGetProgrami(shaderProgram, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("Failed to link shader program: " + GL20.glGetProgramInfoLog(shaderProgram, 1024));
        }

        resolutionUniform = GL20.glGetUniformLocation(shaderProgram, "iResolution");
        mouseUniform = GL20.glGetUniformLocation(shaderProgram, "iMouse");
        timeUniform = GL20.glGetUniformLocation(shaderProgram, "iTime");
        channel1Uniform = GL20.glGetUniformLocation(shaderProgram, "iChannel1");
    }

    private int loadShader(String modid, String relativePath, int type) {
        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, readFile(modid, relativePath));
        GL20.glCompileShader(shader);

        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("Failed to compile shader: " + GL20.glGetShaderInfoLog(shader, 1024));
        }

        return shader;
    }

    private String readFile(String modid, String relativePath) {
        String fullPath = "/assets/" + modid + "/" + relativePath;
        InputStream inputStream = getClass().getResourceAsStream(fullPath);
        if (inputStream == null) {
            throw new RuntimeException("Shader file not found: " + fullPath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException("Error reading shader file: " + fullPath, e);
        }
    }

    public void use() {
        GL20.glUseProgram(shaderProgram);
    }

    public void stop() {
        GL20.glUseProgram(0);
    }

    public void cleanup() {
        GL20.glDetachShader(shaderProgram, vertexShader);
        GL20.glDetachShader(shaderProgram, fragmentShader);
        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);
        GL20.glDeleteProgram(shaderProgram);
    }

    public void setUniforms(int width, int height, float mouseX, float mouseY, float time, int textureUnit) {
        GL20.glUniform2f(resolutionUniform, width, height);
        GL20.glUniform2f(mouseUniform, mouseX, mouseY);
        GL20.glUniform1f(timeUniform, time);
        GL20.glUniform1i(channel1Uniform, textureUnit);
    }
}