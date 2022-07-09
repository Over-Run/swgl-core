/*
 * MIT License
 *
 * Copyright (c) 2022 Overrun Organization
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.overrun.swgl.test.lifegame;

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.lwjgl.opengl.GL15C;
import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.asset.tex.NativeImage;
import org.overrun.swgl.core.cfg.WindowConfig;
import org.overrun.swgl.core.gl.GLProgram;
import org.overrun.swgl.core.gl.GLUniformType;
import org.overrun.swgl.core.gl.GLVao;
import org.overrun.swgl.core.gl.IGLBuffer;
import org.overrun.swgl.core.gl.shader.GLShaders;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.io.ResManager;
import org.overrun.swgl.core.model.VertexFormat;

import java.nio.ByteBuffer;
import java.util.function.IntPredicate;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;
import static org.overrun.swgl.core.gl.GLClear.*;

/**
 * @author squid233
 * @since 0.2.0
 */
public final class LifeGame extends GlfwApplication {
    public static void main(String[] args) {
        new LifeGame().launch();
    }

    private static final IFileProvider FILE_PROVIDER = IFileProvider.ofCaller();
    public static final int GRID_HALF = 200;
    public static final int GRID_SIZE = GRID_HALF * 2;
    public static final int GRID_COUNT = GRID_SIZE * GRID_SIZE;
    public static final float CELL_SIZE = 32.0f;
    public static final int CELLS_INST_VBO_SIZE = GRID_COUNT * 3;
    private final Matrix4f projection = new Matrix4f();
    private final Matrix4f view = new Matrix4f();
    private final Matrix4fStack model = new Matrix4fStack(2);
    private GLProgram program;
    private GLVao vao;
    private IGLBuffer.Single cellsVbo;
    private IGLBuffer.Single cellsInstanceVbo;
    private IGLBuffer.Single cellsEbo;
    private float viewX = 0.0f, viewY = 0.0f, scale = 1.0f;
    private final CellStatus[][] cellsStatus = new CellStatus[GRID_SIZE][GRID_SIZE];
    private final float[] cellsVertexData = new float[CELLS_INST_VBO_SIZE];
    private int cellsTick = 0;
    private ByteBuffer cellsInstVboBuf;
    private int tickRate = 2;

    @Override
    public void prepare() {
        WindowConfig.initialTitle = "Game of Life - TickRate: ";
        WindowConfig.setRequiredGlVer(3, 3);
    }

    @Override
    public void start() {
        tickRateTitle();
        clearColor(1.0f, 1.0f, 1.0f, 1.0f);

        resManager = new ResManager();
        program = resManager.addResource(new GLProgram());
        GLShaders.linkSimple(program,
            """
                #version 330 core
                layout (location = 0) in vec2 Position;
                layout (location = 1) in vec3 OffsetColor;
                out float vertexColor;
                uniform mat4 Projection, View, Model;
                void main() {
                    gl_Position = Projection * View * Model * vec4(Position + OffsetColor.xy, 0.0, 1.0);
                    vertexColor = OffsetColor.b;
                }""",
            """
                #version 330 core
                in float vertexColor;
                out vec4 FragColor;
                void main() {
                    FragColor = vec4(vertexColor, vertexColor, vertexColor, 1.0);
                }""");
        program.createUniform("Projection", GLUniformType.M4F).set(projection);
        program.createUniform("View", GLUniformType.M4F).set(view);
        program.createUniform("Model", GLUniformType.M4F).set(model);

        vao = resManager.addResource(new GLVao());
        vao.bind();
        cellsVbo = resManager.addResource(new IGLBuffer.Single());
        cellsVbo.layout(GL_ARRAY_BUFFER, GL_STATIC_DRAW)
            .bind()
            .data(new float[]{
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
            }, GL15C::glBufferData);
        VertexFormat.V2F.beginDraw(0, 0, 0);
        cellsVbo.unbind();
        cellsInstanceVbo = resManager.addResource(new IGLBuffer.Single());
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                cellsStatus[y][x] = new CellStatus();
                setCellStatus(x - GRID_HALF, y - GRID_HALF, false);
            }
        }
        // Initialize
        // Pulsar
        initCells("game_of_life_img/pulsar.png");
        // Glider
        //initCells("game_of_life_img/glider.png");
        // Gosper Glider Gun
        //initCells("game_of_life_img/gosper-glider-gun.png");
        initCellData();
        cellsInstanceVbo.layout(GL_ARRAY_BUFFER, GL_STREAM_DRAW)
            .bind()
            .data(cellsVertexData, GL15C::glBufferData);
        VertexFormat.V3F.beginDraw(1, 0, 0);
        glVertexAttribDivisor(1, 1);
        cellsInstanceVbo.unbind();
        cellsEbo = resManager.addResource(new IGLBuffer.Single());
        cellsEbo.layout(GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW)
            .bind()
            .data(new int[]{0, 1, 2, 2, 3, 0}, GL15C::glBufferData);
        vao.unbind();
    }

    public int cellIndex(int i) {
        return i + GRID_HALF;
    }

    public CellStatus getCellStatus(int x, int y) {
        return cellsStatus[cellIndex(y)][cellIndex(x)];
    }

    public void initCellStatus(int x, int y, boolean live) {
        var status = getCellStatus(x, y);
        status.prevLive = live;
        status.live = live;
        status.dirty = true;
    }

    public void setCellStatus(int x, int y, boolean live) {
        var status = getCellStatus(x, y);
        if (!status.dirty) {
            status.prevLive = status.live;
            status.live = live;
            status.dirty = true;
        }
    }

    public int getCellsVDataIndex(int x, int y) {
        return (cellIndex(y) * GRID_SIZE + cellIndex(x)) * 3;
    }

    public void setCellData(int x, int y, boolean live) {
        final int index = getCellsVDataIndex(x, y);
        cellsInstVboBuf.position((index + 2) * 4).putFloat(live ? 0.0f : 1.0f);
        // don't set array
        //cellsVertexData[index + 2] = live ? 0.0f : 1.0f;
    }

    public void setInitCellData(int x, int y, boolean live) {
        final int index = getCellsVDataIndex(x, y);
        cellsVertexData[index] = x;
        cellsVertexData[index + 1] = y;
        cellsVertexData[index + 2] = live ? 0.0f : 1.0f;
    }

    public void initCellData() {
        for (int y = -GRID_HALF; y < GRID_HALF; y++) {
            for (int x = -GRID_HALF; x < GRID_HALF; x++) {
                var status = getCellStatus(x, y);
                setInitCellData(x, y, status.live);
            }
        }
    }

    private void initCells(int xo, int yo, int w, int h, IntPredicate statusGetter) {
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                initCellStatus(x + xo, y + yo, statusGetter.test((h - 1 - y) * w + x));
            }
        }
    }

    public void initCells(int xo, int yo, int w, boolean[] status) {
        initCells(xo, yo, w, status.length / w, index -> status[index]);
    }

    /**
     * Initialize cells
     *
     * @param xo     origin x from left of array
     * @param yo     origin y from bottom of array
     * @param w      width
     * @param status status
     */
    public void initCells(int xo, int yo, int w, int[] status) {
        initCells(xo, yo, w, status.length / w, index -> status[index] != 0);
    }

    public void initCells(String imgName) {
        try (var img = new NativeImage()) {
            img.load(imgName, FILE_PROVIDER);
            final int w = img.width();
            final int h = img.height();
            boolean[] status = new boolean[w * h];
            var buf = img.buffer();
            for (int i = 0, c = buf.limit() / 4; i < c; i++) {
                int abgr = buf.getInt(i * 4);
                status[i] = (abgr | 0xff000000) != 0xff000000;
            }
            initCells(-w / 2, -h / 2, w, status);
        }
    }

    @Override
    public void run() {
        view.translation(viewX, viewY, 0.0f)
            .scale(scale, scale, 1.0f);
        clear(COLOR_BUFFER_BIT);

        program.bind();

        program.getUniform("View").set(view);
        program.getUniform("Model").set(model);
        program.updateUniforms();

        program.getUniform("Model").set(model.pushMatrix().scale(CELL_SIZE, CELL_SIZE, 1.0f));
        program.updateUniforms();
        model.popMatrix();
        vao.bind();
        glDrawElementsInstanced(GL_TRIANGLES, 12, GL_UNSIGNED_INT, 0L, GRID_COUNT);
        vao.unbind();

        program.unbind();
    }

    public void updateCells() {
        for (int y = -GRID_HALF; y < GRID_HALF; y++) {
            for (int x = -GRID_HALF; x < GRID_HALF; x++) {
                var status = getCellStatus(x, y);
                int lives = 0;
                for (int ya = -1; ya < 2; ya++) {
                    for (int xa = -1; xa < 2; xa++) {
                        if (xa == 0 && ya == 0) continue;
                        int nx = x + xa;
                        int ny = y + ya;
                        if (nx < -GRID_HALF || nx >= GRID_HALF ||
                            ny < -GRID_HALF || ny >= GRID_HALF) continue;
                        var nearStatus = getCellStatus(nx, ny);
                        if (nearStatus.prevLive) ++lives;
                    }
                }
                if (status.prevLive) {
                    if (lives < 2 || lives > 3)
                        setCellStatus(x, y, false);
                } else {
                    if (lives == 3)
                        setCellStatus(x, y, true);
                }
            }
        }
        // map buffer and modify
        cellsInstanceVbo.bind();
        cellsInstVboBuf = cellsInstanceVbo.map(GL_WRITE_ONLY, CELLS_INST_VBO_SIZE * 4, cellsInstVboBuf);
        for (int y = -GRID_HALF; y < GRID_HALF; y++) {
            for (int x = -GRID_HALF; x < GRID_HALF; x++) {
                var status = getCellStatus(x, y);
                if (status.dirty) {
                    setCellData(x, y, status.live);
                    status.prevLive = status.live;
                    status.dirty = false;
                }
            }
        }
        cellsInstanceVbo.unmap();
        cellsInstanceVbo.unbind();
    }

    public void tickRateTitle() {
        window.setTitle(WindowConfig.initialTitle + tickRate);
    }

    @Override
    public void onKeyPress(int key, int scancode, int mods) {
        if (key >= GLFW_KEY_1 && key <= GLFW_KEY_9) {
            tickRate = key - GLFW_KEY_0;
            tickRateTitle();
        }
    }

    @Override
    public void tick() {
        if (cellsTick >= tickRate) {
            updateCells();
            cellsTick = 0;
        }
        ++cellsTick;
    }

    @Override
    public void onResize(int width, int height) {
        super.onResize(width, height);
        projection.setOrthoSymmetric(width, height, -1.0f, 1.0f);
        program.getUniform("Projection").set(projection);
    }

    @Override
    public void onCursorPos(double x, double y, double xd, double yd) {
        if (mouse.isBtnDown(GLFW_MOUSE_BUTTON_LEFT)) {
            viewX += (float) xd;
            viewY -= (float) yd;
        }
    }

    @Override
    public void onScroll(double xoffset, double yoffset) {
        scale += (float) yoffset * 0.05f;
        if (scale < 0.05f) {
            scale = 0.05f;
        }
    }
}
