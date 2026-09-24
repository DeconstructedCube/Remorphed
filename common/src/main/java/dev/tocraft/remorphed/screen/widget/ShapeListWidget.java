package dev.tocraft.remorphed.screen.widget;

import dev.tocraft.remorphed.Remorphed;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("UnusedReturnValue")
@Environment(EnvType.CLIENT)
public class ShapeListWidget extends ContainerObjectSelectionList<ShapeListWidget.ShapeRow> {
    private static final int ITEM_HEIGHT = 35;

    public ShapeListWidget(Minecraft minecraft, int width, @NotNull HeaderAndFooterLayout layout) {
        super(minecraft, width, layout.getContentHeight(), layout.getHeaderHeight(), ITEM_HEIGHT);
    }

    public int addRow(ShapeWidget[] widgets) {
        return addEntry(new ShapeRow(widgets));
    }

    public int rowHeight() {
        return defaultEntryHeight;
    }

    @Override
    public void clearEntries() {
        super.clearEntries();
    }

    public static class ShapeRow extends ContainerObjectSelectionList.Entry<ShapeRow> {
        private final ShapeWidget[] widgets;

        public ShapeRow(ShapeWidget[] widgets) {
            this.widgets = widgets;
        }

        @Override
        public void renderContent(@NotNull GuiGraphics guiGraphics, int index, int top, boolean hovering, float delta) {
            int width = Remorphed.CONFIG.row_width;
            int left = (guiGraphics.guiWidth() - width) / 2;
            int height = ITEM_HEIGHT;
            int mouseX = (int) (Minecraft.getInstance().mouseHandler.xpos() * (double) guiGraphics.guiWidth() / (double) Minecraft.getInstance().getWindow().getScreenWidth());
            int mouseY = (int) (Minecraft.getInstance().mouseHandler.ypos() * (double) guiGraphics.guiHeight() / (double) Minecraft.getInstance().getWindow().getScreenHeight());
            for (int i = 0; i < widgets.length; i++) {
                ShapeWidget widget = widgets[i];

                if (widget != null) {
                    int w = width / Remorphed.CONFIG.shapes_per_row;

                    widget.setPosition(left + w * i, top);
                    widget.setSize(w, height);
                    widget.render(guiGraphics, mouseX, mouseY, delta);
                }
            }
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return List.of(widgets);
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return List.of(widgets);
        }

        @Override
        public boolean keyPressed(net.minecraft.client.input.KeyEvent keyEvent) {
            for (GuiEventListener child : children()) {
                if (child.keyPressed(keyEvent)) {
                    return true;
                }
            }
            return super.keyPressed(keyEvent);
        }
    }

    @Override
    public int getRowWidth() {
        return Remorphed.CONFIG.row_width;
    }

    @Override
    protected void renderListBackground(GuiGraphics guiGraphics) {
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent keyEvent) {
        for (ShapeRow child : children()) {
            if (child.keyPressed(keyEvent)) {
                return true;
            }
        }
        return super.keyPressed(keyEvent);
    }
}
