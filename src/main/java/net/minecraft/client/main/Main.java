package net.minecraft.client.main;

import cn.asone.endless.Endless;
import cn.asone.endless.config.ConfigManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;

public class Main {
    public static boolean safelyQuit = false;
    private final static Logger logger = LogManager.getLogger("Endless");
    public static LoadWindow window = null;

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        OptionParser optionparser = new OptionParser();
        optionparser.allowsUnrecognizedOptions();
        optionparser.accepts("demo");
        optionparser.accepts("fullscreen");
        optionparser.accepts("checkGlErrors");
        optionparser.accepts("disableViaVersion");
        optionparser.accepts("disableLoadingWindow");
        OptionSpec<String> server = optionparser.accepts("server").withRequiredArg();
        OptionSpec<Integer> port = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565);
        OptionSpec<File> gameDir = optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
        OptionSpec<File> assetsDir = optionparser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        OptionSpec<File> resourcePackDir = optionparser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        OptionSpec<String> proxyHost = optionparser.accepts("proxyHost").withRequiredArg();
        OptionSpec<Integer> proxyPort = optionparser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
        OptionSpec<String> proxyUser = optionparser.accepts("proxyUser").withRequiredArg();
        OptionSpec<String> proxyPass = optionparser.accepts("proxyPass").withRequiredArg();
        OptionSpec<String> username = optionparser.accepts("username").withRequiredArg().defaultsTo("EndlessUser" + Minecraft.getSystemTime() % 1000L);
        OptionSpec<String> uuid = optionparser.accepts("uuid").withRequiredArg();
        OptionSpec<String> accessToken = optionparser.accepts("accessToken").withRequiredArg().required();
        OptionSpec<String> version = optionparser.accepts("version").withRequiredArg().required();
        OptionSpec<Integer> width = optionparser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854);
        OptionSpec<Integer> height = optionparser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480);
        OptionSpec<String> userProperties = optionparser.accepts("userProperties").withRequiredArg().defaultsTo("{}");
        OptionSpec<String> profileProperties = optionparser.accepts("profileProperties").withRequiredArg().defaultsTo("{}");
        OptionSpec<String> assetIndex = optionparser.accepts("assetIndex").withRequiredArg();
        OptionSpec<String> userType = optionparser.accepts("userType").withRequiredArg().defaultsTo("legacy");
        OptionSpec<String> optionspec19 = optionparser.nonOptions();
        OptionSet argsOptions = optionparser.parse(args);
        List<String> list = argsOptions.valuesOf(optionspec19);

        if (!list.isEmpty()) {
            System.out.println("Completely ignored arguments: " + list);
        }

        if (!argsOptions.has("disableLoadingWindow")) {
            window = new LoadWindow();
            window.init();
            window.setEnabled(true);
            window.setVisible(true);
        }

        String s = argsOptions.valueOf(proxyHost);
        Proxy proxy = Proxy.NO_PROXY;

        if (s != null) {
            try {
                proxy = new Proxy(Type.SOCKS, new InetSocketAddress(s, argsOptions.valueOf(proxyPort)));
            } catch (Exception ignored) {
            }
        }

        final String proxyUserValue = argsOptions.valueOf(proxyUser);
        final String proxyPassValue = argsOptions.valueOf(proxyPass);

        if (!proxy.equals(Proxy.NO_PROXY) && isNullOrEmpty(proxyUserValue) && isNullOrEmpty(proxyPassValue)) {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(proxyUserValue, proxyPassValue.toCharArray());
                }
            });
        }

        int displayWidth = argsOptions.valueOf(width);
        int displayHeight = argsOptions.valueOf(height);
        boolean fullScreen = argsOptions.has("fullscreen");
        boolean checkGLErrors = argsOptions.has("checkGlErrors");
        boolean demo = argsOptions.has("demo");
        Endless.disableVia = argsOptions.has("disableViaVersion");
        if (Endless.disableVia) {
            logger.info("ViaVersion has been disabled!");
        }
        String versionValue = argsOptions.valueOf(version);
        Gson gson = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new Serializer()).create();
        PropertyMap userPropertyMap = gson.fromJson(argsOptions.valueOf(userProperties), PropertyMap.class);
        PropertyMap profilePropertyMap = gson.fromJson(argsOptions.valueOf(profileProperties), PropertyMap.class);
        File gameDirFile = argsOptions.valueOf(gameDir);
        File assetsFile = argsOptions.has(assetsDir) ? argsOptions.valueOf(assetsDir) : new File(gameDirFile, "assets/");
        File resourcePacksFile = argsOptions.has(resourcePackDir) ? argsOptions.valueOf(resourcePackDir) : new File(gameDirFile, "resourcepacks/");
        String uuidValue = argsOptions.has(uuid) ? uuid.value(argsOptions) : username.value(argsOptions);
        String assetIndexValue = argsOptions.has(assetIndex) ? assetIndex.value(argsOptions) : null;
        String serverValue = argsOptions.valueOf(server);
        Integer portValue = argsOptions.valueOf(port);
        Session session = new Session(username.value(argsOptions), uuidValue, accessToken.value(argsOptions), userType.value(argsOptions));
        GameConfiguration gameconfiguration = new GameConfiguration(new GameConfiguration.UserInformation(session, userPropertyMap, profilePropertyMap, proxy), new GameConfiguration.DisplayInformation(displayWidth, displayHeight, fullScreen, checkGLErrors), new GameConfiguration.FolderInformation(gameDirFile, resourcePacksFile, assetsFile, assetIndexValue), new GameConfiguration.GameInformation(demo, versionValue), new GameConfiguration.ServerInformation(serverValue, portValue));
        /*
         * 注册Endless保存配置文件hook
         */
        Runtime.getRuntime().addShutdownHook(new Thread("Endless Shutdown Thread") {
            public void run() {
                if (!Main.safelyQuit) {
                    if (!Endless.inited) {
                        Main.logger.error(Endless.CLIENT_NAME + " 加载失败! 如果需要帮助, 请将日志发送给作者.");
                    } else {
                        Main.logger.warn("检测到异常的退出. 紧急保存配置文件!");
                        ConfigManager.INSTANCE.saveAllConfigs();
                    }
                } else
                    Main.logger.info("检测到已正常退出客户端.");
            }
        });
        /*
         * 保存单人世界、游戏配置文件
         */
        Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {
            public void run() {
                if (Minecraft.getMinecraft() != null) {
                    Main.logger.info("[Debug] Shutting down server...");
                    Minecraft.stopIntegratedServer();
                    Main.logger.info("正在保存Minecraft配置文件...");
                    if (Minecraft.getMinecraft().gameSettings != null) {
                        Minecraft.getMinecraft().gameSettings.saveOptions();
                    }
                }
            }
        });
        Thread.currentThread().setName("Client thread");
        (new Minecraft(gameconfiguration)).run();
    }

    private static boolean isNullOrEmpty(String str) {
        return str != null && !str.isEmpty();
    }
}
