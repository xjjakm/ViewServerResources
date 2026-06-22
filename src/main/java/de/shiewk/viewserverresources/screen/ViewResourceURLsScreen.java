package de.shiewk.viewserverresources.screen;

import de.shiewk.viewserverresources.event.ScreenListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.awt.*;
import java.util.List;

public class ViewResourceURLsScreen extends Screen {

    private final Screen parent;
    private final List<ScreenListener.PackInfo> infos;
    public ViewResourceURLsScreen(Screen parent, List<ScreenListener.PackInfo> infos) {
        super(Component.translatable("gui.viewserverresources.viewURL"));
        this.parent = parent;
        this.infos = infos;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    protected void init() {
        Button doneButton = Button.builder(Component.translatable("gui.done"), btn -> this.onClose())
                .bounds(width / 2 - 150, height - 30, 300, 20)
                .build();
        addRenderableWidget(doneButton);

        final MultiLineTextWidget text = new MultiLineTextWidget(getMessage(), font);
        text.setCentered(true);
        text.setPosition(width / 2 - (text.getWidth() / 2), height / 2 - (text.getHeight() / 2));
        addRenderableWidget(text);
    }

    private Component getMessage(){
        MutableComponent msg = Component.empty();
        for (ScreenListener.PackInfo info : infos) {
            msg = msg.append(Component.literal("\n"+info.url()));
        }
        return Component.translatable(infos.size() == 1 ? "gui.viewserverresources.url" : "gui.viewserverresources.urls", msg).withColor(Color.GREEN.getRGB());
    }
}
