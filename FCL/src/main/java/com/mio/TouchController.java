package com.mio;

import android.content.Context;
import android.os.Vibrator;
import android.system.Os;
import android.util.Log;

import androidx.annotation.Nullable;

import top.fifthlight.touchcontroller.proxy.client.LauncherProxyClient;
import top.fifthlight.touchcontroller.proxy.client.MessageTransport;
import top.fifthlight.touchcontroller.proxy.client.android.SimpleVibrationHandler;
import top.fifthlight.touchcontroller.proxy.client.android.transport.UnixSocketTransportKt;

public class TouchController {
    private static LauncherProxyClient instance = null;

    private TouchController() {}

    @Nullable
    public static LauncherProxyClient getProxyClient(Context context) {
        if (instance == null) {
            instance = createProxy(context);
        }
        return instance;
    }

    private static final String socketName = "FoldCraftLauncher";

    private static LauncherProxyClient createProxy(Context context) {
        try {
            MessageTransport transport = UnixSocketTransportKt.UnixSocketTransport(socketName);
            Os.setenv("TOUCH_CONTROLLER_PROXY_SOCKET", socketName, true);
            LauncherProxyClient client = new LauncherProxyClient(transport);
            Vibrator vibrator = context.getSystemService(Vibrator.class);
            SimpleVibrationHandler handler = new SimpleVibrationHandler(vibrator);
            client.setVibrationHandler(handler);
            client.run();
            return client;
        } catch (Throwable ex) {
            Log.w("TouchController", "TouchController proxy client create failed", ex);
            return null;
        }
    }
}
