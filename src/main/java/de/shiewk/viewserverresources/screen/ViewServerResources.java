package de.shiewk.viewserverresources.screen;

import de.shiewk.viewserverresources.client.ViewServerResourcesClient;
import it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ViewServerResources extends Screen {
    private final Screen parent;
    private static final int buttonWidth = 192;
    private boolean cfgDirty = false;
    public ViewServerResources(Screen parent) {
        super(Component.translatable("gui.viewserverresources.config"));
        this.parent = parent;
    }

    @Override
    public void onClose() {
        if (cfgDirty){
            ViewServerResourcesClient.saveConfig();
        }
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    protected void init() {
        {
            final MultiLineTextWidget tw = new MultiLineTextWidget(Component.translatable("viewserverresources.settings"), font);
            tw.setPosition(width / 2 - tw.getWidth() / 2, 10);
            addRenderableWidget(tw);
        }
        
        int y = 40;
        
        Button broadcastBtn = createToggleableLargeButton(
                ViewServerResourcesClient.isBroadcastDownloads(),
                bl -> Component.translatable("viewserverresources.settings.broadcast", Component.translatable(bl ? "gui.yes" : "gui.no")).withColor(bl ? new Color(100, 255, 100).getRGB() : new Color(255, 100, 100).getRGB()),
                bl -> {
                    ViewServerResourcesClient.setBroadcastDownloads(bl);
                    cfgDirty = true;
                }
        );
        broadcastBtn.setPosition((width - broadcastBtn.getWidth()) / 2, y);
        addRenderableWidget(broadcastBtn);
        y += 28;
        
        Button urlBtn = createButton(Component.translatable("viewserverresources.settings.whitelistedURLs"), btn -> {
            btn.active = false;
            cfgDirty = true;
            Minecraft.getInstance().setScreen(new ManageListScreen<>(Component.translatable("viewserverresources.settings.whitelistedURLs"), this, ViewServerResourcesClient.getWhitelistedURLs()));
        });
        urlBtn.setPosition((width - buttonWidth * 2 - 8) / 2, y);
        addRenderableWidget(urlBtn);
        
        Button hostBtn = createButton(Component.translatable("viewserverresources.settings.whitelistedHosts"), btn -> {
            btn.active = false;
            cfgDirty = true;
            Minecraft.getInstance().setScreen(new ManageListScreen<>(Component.translatable("viewserverresources.settings.whitelistedHosts"), this, ViewServerResourcesClient.getWhitelistedHosts()));
        });
        hostBtn.setPosition((width + 8) / 2, y);
        addRenderableWidget(hostBtn);
    }

    private Button createButton(Component m, Button.OnPress action){
        return Button.builder(m, action).width(buttonWidth).build();
    }

    private Button createToggleableLargeButton(boolean state, Boolean2ObjectFunction<Component> function, BooleanConsumer onToggle){
        AtomicBoolean bl = new AtomicBoolean(state);
        return Button.builder(function.get(state), btn -> {
            bl.set(!bl.get());
            onToggle.accept(bl.get());
            btn.setMessage(function.apply(bl.get()));
        }).width(buttonWidth * 2 + 8).build();
    }
}
