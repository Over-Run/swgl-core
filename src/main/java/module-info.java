/**
 * @author squid233
 * @since 0.1.0
 */
module org.overrun.swgl.core {
    requires org.jetbrains.annotations;
    requires transitive org.joml;
    requires org.lwjgl;
    requires org.lwjgl.glfw;
    requires org.lwjgl.opengl;
    requires org.lwjgl.stb;

    exports org.overrun.swgl.core;
    exports org.overrun.swgl.core.asset;
    exports org.overrun.swgl.core.cfg;
    exports org.overrun.swgl.core.gl;
    exports org.overrun.swgl.core.io;
    exports org.overrun.swgl.core.level;
    exports org.overrun.swgl.core.model;
    exports org.overrun.swgl.core.model.mesh;
    exports org.overrun.swgl.core.util;
    exports org.overrun.swgl.core.util.math;
}