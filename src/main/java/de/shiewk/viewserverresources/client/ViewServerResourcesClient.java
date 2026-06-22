package de.shiewk.viewserverresources.client;

import com.google.gson.Gson;
import de.shiewk.viewserverresources.config.ViewServerResourcesConfig;
import de.shiewk.viewserverresources.event.ChatAnnouncer;
import de.shiewk.viewserverresources.event.ScreenListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import static de.shiewk.viewserverresources.ViewServerResourcesMod.LOGGER;

public class ViewServerResourcesClient implements ClientModInitializer {

    private static ViewServerResourcesConfig config = new ViewServerResourcesConfig();
    private static File whitelistFile;
    private static final Gson gson = new Gson();

    public static boolean allowedURL(URL uRL) {
        if (config.whitelist.urls.contains(uRL.toString())){
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("URL {} is whitelisted", uRL);
            return true;
        } else if (config.whitelist.hosts.contains(uRL.getHost())){
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Host {} is whitelisted", uRL.getHost());
            return true;
        }
        return false;
    }

    public static void addWhitelistURL(URL url){
        final String urls = url.toString();
        LOGGER.info("Whitelisting url {}", urls);
        if (!config.whitelist.urls.contains(urls)){
            config.whitelist.urls.add(urls);
        }
    }

    public static void addWhitelistHost(URL url){
        final String h = url.getHost();
        LOGGER.info("Whitelisting host {}", h);
        if (!config.whitelist.hosts.contains(h)){
            config.whitelist.hosts.add(h);
        }
    }

    public synchronized static void loadConfig(){
        LOGGER.info("Loading config");
        try (FileReader fr = new FileReader(whitelistFile)){
            ViewServerResourcesConfig cfg = gson.fromJson(fr, ViewServerResourcesConfig.class);
            Objects.requireNonNull(cfg, "Configuration");
            config = cfg;
        } catch (FileNotFoundException e) {
            LOGGER.warn("Config file not found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized static void saveConfig() {
        LOGGER.info("Saving config");
        try (FileWriter fw = new FileWriter(whitelistFile)) {
            gson.toJson(config, fw);
        } catch (IOException e) {
            LOGGER.error("Error saving config", e);
        }
    }

    public static List<String> getWhitelistedURLs() {
        return config.whitelist.urls;
    }

    public static List<String> getWhitelistedHosts() {
        return config.whitelist.hosts;
    }

    public static boolean isBroadcastDownloads() {
        return config.broadcastDownloads;
    }

    public static void setBroadcastDownloads(boolean broadcastDownloads) {
        config.broadcastDownloads = broadcastDownloads;
    }

    @Override
    public void onInitializeClient() {
        whitelistFile = new File(Minecraft.getInstance().gameDirectory, "viewserverresources.json");
        ScreenEvents.AFTER_INIT.register(new ScreenListener());
        ClientTickEvents.END_CLIENT_TICK.register(new ChatAnnouncer());
        loadConfig();
    }
}
