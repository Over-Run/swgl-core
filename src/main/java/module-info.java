/**
 * The core module.
 *
 * @author squid233
 * @since 0.1.0
 */
module org.overrun.swgl.core {
    requires org.jetbrains.annotations;
    requires transitive org.joml;
    requires org.lwjgl;
    requires org.lwjgl.assimp;
    requires org.lwjgl.glfw;
    requires org.lwjgl.opengl;
    requires org.lwjgl.stb;
    requires transitive org.slf4j;

    exports org.overrun.swgl.core;
    exports org.overrun.swgl.core.asset;
    exports org.overrun.swgl.core.asset.tex;
    exports org.overrun.swgl.core.cfg;
    exports org.overrun.swgl.core.gl;
    exports org.overrun.swgl.core.gl.batch;
    exports org.overrun.swgl.core.gl.ims;
    exports org.overrun.swgl.core.gl.shader;
    exports org.overrun.swgl.core.gui;
    exports org.overrun.swgl.core.gui.font;
    exports org.overrun.swgl.core.io;
    exports org.overrun.swgl.core.level;
    exports org.overrun.swgl.core.model;
    exports org.overrun.swgl.core.model.obj;
    exports org.overrun.swgl.core.model.simple;
    exports org.overrun.swgl.core.phys.p2d;
    exports org.overrun.swgl.core.phys.p3d;
    exports org.overrun.swgl.core.util;
    exports org.overrun.swgl.core.util.math;
    exports org.overrun.swgl.core.util.timing;
}