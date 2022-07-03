package viamcp;

import cn.asone.endless.Endless;
import cn.asone.endless.option.AbstractOption;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import io.netty.channel.EventLoop;
import io.netty.channel.local.LocalEventLoopGroup;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import viamcp.gui.AsyncVersionSlider;
import viamcp.loader.MCPBackwardsLoader;
import viamcp.loader.MCPRewindLoader;
import viamcp.loader.MCPViaLoader;
import viamcp.platform.MCPViaInjector;
import viamcp.platform.MCPViaPlatform;
import viamcp.utils.JLoggerToLog4j;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

public class ViaMCP {
    public final static int PROTOCOL_VERSION = 47;
    private static final ViaMCP instance = new ViaMCP();

    @Nullable
    public static ViaMCP getInstance() {
        if (Endless.disableVia) {
            return null;
        } else {
            return instance;
        }
    }

    private final Logger jLogger = new JLoggerToLog4j(LogManager.getLogger("ViaMCP"));
    private final CompletableFuture<Void> INIT_FUTURE = new CompletableFuture<>();

    private ExecutorService ASYNC_EXEC;
    private EventLoop EVENT_LOOP;

    private File file;
    //private int version = 47;
    public static final AbstractOption<Integer> versionValue = new AbstractOption<Integer>("ViaClientVersion", 47) {
        @NotNull
        @Override
        public JsonElement toJson() {
            return new JsonPrimitive(getValue());
        }

        @Override
        public void fromJson(@NotNull JsonElement element) {
            if (element.isJsonPrimitive()) {
                set(element.getAsInt());
                if (!Endless.disableVia) {
                    instance.setVersion(element.getAsInt());
                }

            } else
                super.fromJson(element);
        }
    };
    private String lastServer;

    /**
     * Version Slider that works Asynchronously with the Version GUI
     * Please initialize this before usage with initAsyncSlider() or initAsyncSlider(x, y, width (min. 110), height)
     */
    public AsyncVersionSlider asyncSlider;

    public void start() {
        ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ViaMCP-%d").build();
        ASYNC_EXEC = Executors.newFixedThreadPool(8, factory);

        EVENT_LOOP = new LocalEventLoopGroup(1, factory).next();
        EVENT_LOOP.submit(INIT_FUTURE::join);

        //setVersion(PROTOCOL_VERSION);
        this.file = new File("ViaMCP");
        if (this.file.mkdir()) {
            this.getjLogger().info("Creating ViaMCP Folder");
        }

        Via.init(ViaManagerImpl.builder().injector(new MCPViaInjector()).loader(new MCPViaLoader()).platform(new MCPViaPlatform(file)).build());

        MappingDataLoader.enableMappingsCache();
        ((ViaManagerImpl) Via.getManager()).init();

        new MCPBackwardsLoader(file);
        new MCPRewindLoader(file);

        INIT_FUTURE.complete(null);
    }

    public void initAsyncSlider() {
        asyncSlider = new AsyncVersionSlider(-1, 5, 5, 110, 20);
    }

    public void initAsyncSlider(int x, int y, int width, int height) {
        asyncSlider = new AsyncVersionSlider(-1, x, y, Math.max(width, 110), height);
    }

    public Logger getjLogger() {
        return jLogger;
    }

    public CompletableFuture<Void> getInitFuture() {
        return INIT_FUTURE;
    }

    public ExecutorService getAsyncExecutor() {
        return ASYNC_EXEC;
    }

    public EventLoop getEventLoop() {
        return EVENT_LOOP;
    }

    public File getFile() {
        return file;
    }

    public String getLastServer() {
        return lastServer;
    }

    public int getVersion() {
        return versionValue.get();
    }

    public void setVersion(int version) {
        versionValue.set(version);
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setLastServer(String lastServer) {
        this.lastServer = lastServer;
    }
}
