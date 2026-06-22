package de.shiewk.viewserverresources.event;

import de.shiewk.viewserverresources.client.ViewServerResourcesClient;
import de.shiewk.viewserverresources.mixin.AccessorConfirmScreen;
import de.shiewk.viewserverresources.mixin.AccessorConfirmServerResourcePackScreen;
import de.shiewk.viewserverresources.mixin.AccessorConfirmServerResourcePackScreenPack;
import de.shiewk.viewserverresources.mixin.AccessorScreen;
import de.shiewk.viewserverresources.screen.ViewResourceURLsScreen;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static de.shiewk.viewserverresources.ViewServerResourcesMod.LOGGER;

public class ScreenListener implements ScreenEvents.AfterInit {

    private static final int buttonWidth = 150;

    public record PackInfo(UUID id, URL url, String hash){}

    @Override
    public void afterInit(Minecraft client, Screen screen, int scaledWidth, int scaledHeight) {
        try {
            if (!(screen instanceof ConfirmScreen)) {
                return;
            }
            
            if (!isResourcePackScreen(screen)) {
                return;
            }
            
            final List<PackInfo> infos = getPackInfos((AccessorConfirmServerResourcePackScreen) screen);
            if (infos.isEmpty()) return;
            
            List<Button> buttons = new ArrayList<>();
            
            buttons.add(createButton(Component.translatable(infos.size() == 1 ? "gui.viewserverresources.viewURL" : "gui.viewserverresources.viewURLs"), btn -> viewURLs(client, screen, infos)));
            buttons.add(createButton(Component.translatable(infos.size() == 1 ? "gui.viewserverresources.alwaysURL" : "gui.viewserverresources.alwaysURLs"), btn -> whitelistURLsAndAccept(btn, screen, infos)));
            buttons.add(createLargeButton(Component.translatable("gui.viewserverresources.alwaysHost", Component.literal(infos.getFirst().url().getHost()).withColor(Color.GREEN.getRGB())), btn -> whitelistHostsAndAccept(btn, screen, infos)));

            int y = scaledHeight - 30 - 24 * buttons.size();
            for (Button btn : buttons) {
                btn.setPosition((scaledWidth - btn.getWidth()) / 2, y);
                ((AccessorScreen) screen).callAddWidget(btn);
                y += 24;
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to add buttons to resource pack screen (compatibility mode)", e);
        }
    }

    private boolean isResourcePackScreen(Screen screen) {
        try {
            Class<?> screenClass = screen.getClass();
            String className = screenClass.getName();
            return className.contains("ResourcePack") || className.contains("ConfirmServerResourcePack");
        } catch (Exception e) {
            return false;
        }
    }

    private void whitelistURLsAndAccept(Button btn, Screen screen, List<PackInfo> infos){
        btn.active = false;
        for (PackInfo info : infos) {
            ViewServerResourcesClient.addWhitelistURL(info.url());
        }
        ViewServerResourcesClient.saveConfig();
        accept(screen);
    }

    private void accept(Screen screen){
        try {
            ((AccessorConfirmScreen) screen).getCallback().accept(true);
        } catch (Exception e) {
            LOGGER.warn("Failed to accept resource pack (compatibility mode)", e);
        }
    }

    private void whitelistHostsAndAccept(Button btn, Screen screen, List<PackInfo> infos){
        btn.active = false;
        for (PackInfo info : infos) {
            ViewServerResourcesClient.addWhitelistHost(info.url());
        }
        ViewServerResourcesClient.saveConfig();
        accept(screen);
    }

    private void viewURLs(Minecraft client, Screen screen, List<PackInfo> infos) {
        try {
            client.setScreen(new ViewResourceURLsScreen(screen, infos));
        } catch (Exception e) {
            LOGGER.warn("Failed to open URL view screen", e);
        }
    }

    private static @NotNull List<PackInfo> getPackInfos(AccessorConfirmServerResourcePackScreen screen) {
        final List<PackInfo> infos = new ArrayList<>();
        try {
            final List<?> packs = screen.getPacks();
            for (Object packObj : packs) {
                AccessorConfirmServerResourcePackScreenPack pack = (AccessorConfirmServerResourcePackScreenPack) packObj;
                infos.add(new PackInfo(pack.getId(), pack.getURL(), pack.getHash()));
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to get pack info", e);
        }
        return infos;
    }

    private Button createButton(Component m, Button.OnPress action){
        return Button.builder(m, action).width(buttonWidth).build();
    }

    private Button createLargeButton(Component m, Button.OnPress action){
        return Button.builder(m, action).width(buttonWidth*2+8).build();
    }
}
