package de.shiewk.viewserverresources.screen.elements;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class ManageListWidget<T> extends ObjectSelectionList<ManageListWidget.ManageListEntry<T>> {
    private final List<T> list;
    private final Font font;

    public ManageListWidget(Font font, int width, int height, int top, int itemHeight, List<T> list) {
        super(Minecraft.getInstance(), width, height, top, itemHeight);
        this.font = font;
        this.list = list;
        refreshElements();
    }

    private void refreshElements() {
        clearEntries();
        for (T t : new ObjectArrayList<>(list)) {
            addEntry(new ManageListEntry<>(t, this));
        }
    }

    public void removeItem(T item) {
        list.remove(item);
        refreshElements();
    }

    @Override
    public int getRowWidth() {
        return width - 10;
    }

    protected int getScrollbarPosition() {
        return width - 6;
    }

    public static class ManageListEntry<T> extends ObjectSelectionList.Entry<ManageListEntry<T>> {
        private final T item;
        private final ManageListWidget<T> parent;
        private int x, y, entryWidth, entryHeight;

        public ManageListEntry(T item, ManageListWidget<T> parent) {
            this.item = item;
            this.parent = parent;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            String text = item.toString();
            int textWidth = parent.font.width(text);
            int textX = x + (entryWidth - 34) / 2 - textWidth / 2;
            int textY = y + (entryHeight - 9) / 2;
            
            graphics.text(parent.font, FormattedCharSequence.forward(text, net.minecraft.network.chat.Style.EMPTY), textX, textY, hovered ? 0xFFFFFF : 0xA0A0A0, false);
            
            int deleteX = x + entryWidth - 22;
            int deleteY = y + 2;
            graphics.text(parent.font, FormattedCharSequence.forward("x", net.minecraft.network.chat.Style.EMPTY), deleteX, deleteY, isMouseOverDelete(mouseX, mouseY) ? 0xFF5555 : 0xFF8888, false);
        }

        private boolean isMouseOverDelete(double mouseX, double mouseY) {
            int deleteX = x + entryWidth - 22;
            int deleteY = y + 2;
            return mouseX >= deleteX && mouseX < deleteX + 16 && mouseY >= deleteY && mouseY < deleteY + 12;
        }

        @Override
        public void setX(int x) {
            this.x = x;
        }

        @Override
        public void setY(int y) {
            this.y = y;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        @Override
        public int getWidth() {
            return entryWidth;
        }

        @Override
        public int getHeight() {
            return entryHeight;
        }

        @Override
        public ScreenRectangle getRectangle() {
            return new ScreenRectangle(x, y, entryWidth, entryHeight);
        }

        @Override
        public void visitWidgets(java.util.function.Consumer<net.minecraft.client.gui.components.AbstractWidget> widgetVisitor) {
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            double mouseX = event.x();
            double mouseY = event.y();
            if (isMouseOverDelete(mouseX, mouseY)) {
                parent.removeItem(item);
                return true;
            }
            return super.mouseClicked(event, doubleClick);
        }

        @Override
        public Component getNarration() {
            return Component.empty();
        }
    }
}
