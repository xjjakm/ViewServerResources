package de.shiewk.viewserverresources.screen;

import de.shiewk.viewserverresources.screen.elements.ManageListWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ManageListScreen<T> extends Screen {
    private final Screen parent;
    private final List<T> list;
    public ManageListScreen(Component title, Screen parent, List<T> list) {
        super(title);
        this.parent = parent;
        this.list = list;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    protected void init() {
        ManageListWidget<T> widget = new ManageListWidget<>(font, width, height, 30, 24, list);
        addRenderableWidget(widget);
    }
}
