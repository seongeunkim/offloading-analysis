package com.seongekim.tcc.server.shader;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenGLContext {
    private static OpenGLContext INSTANCE = null;

    private static boolean SHOW_WINDOW = false;
    private static int WINDOW_WIDTH = 800;
    private static int WINDOW_HEIGHT = 600;

    private long window;

    private OpenGLContext() { }

    public static OpenGLContext getInstance() {
        if (INSTANCE == null)
            INSTANCE = new OpenGLContext();
        return INSTANCE;
    }

    public static OpenGLContext getInitializedInstance() {
        OpenGLContext context = getInstance();
        context.init();
        return context;
    }

    public void init() {
        if(window != NULL) {
            long currentWindow = glfwGetCurrentContext();
            if(currentWindow != window) {
                glfwMakeContextCurrent(window);
                GL.createCapabilities();
            }
            return;
        }
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        if(SHOW_WINDOW)
            glfwShowWindow(window);
        else
            glfwHideWindow(window);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
    }

    public void swap() {
        glfwSwapBuffers(window); // swap the color buffers
    }

    public void submit(Runnable runnable) {
        runnable.run();
        swap();
    }

    public void waitForWindowAndDispose() {
        if(SHOW_WINDOW) {
            // Run the rendering loop until the user has attempted to close
            // the window or has pressed the ESCAPE key.
            while ( !glfwWindowShouldClose(window) ) {
                // Poll for window events. The key callback above will only be
                // invoked during this call.
                glfwPollEvents();
            }
        }
        dispose();
    }

    public void dispose() {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();

        window = NULL;
    }
}
