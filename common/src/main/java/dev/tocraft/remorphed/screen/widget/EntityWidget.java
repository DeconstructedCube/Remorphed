package dev.tocraft.remorphed.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tocraft.remorphed.Remorphed;
import dev.tocraft.remorphed.network.NetworkHandler;
import dev.tocraft.walkers.api.variant.ShapeType;
import dev.tocraft.walkers.traits.ShapeTrait;
import dev.tocraft.walkers.traits.TraitRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class EntityWidget<T extends LivingEntity> extends ShapeWidget {

    private final ShapeType<T> type;
    private final T entity;
    private final int size;
    private final int id;

    public EntityWidget(int id, int x, int y, int width, int height, ShapeType<T> type, @NotNull T entity, Screen parent, boolean isFavorite, boolean current, int availability) {
        super(x, y, width, height, ShapeType.createTooltipText(entity), parent, isFavorite, current, availability);
        this.size = (int) (Remorphed.CONFIG.entity_size * (1 / (Math.max(entity.getBbHeight(), entity.getBbWidth()))));
        this.type = type;
        this.entity = entity;
        this.id = id;
        entity.setGlowingTag(true);
        setTooltip(Tooltip.create(ShapeType.createTooltipText(entity)));
    }

    @Override
    protected void sendFavoriteRequest(boolean isFavorite) {
        NetworkHandler.sendFavoriteRequest(type, isFavorite);
    }

    @Override
    protected void sendSwap2ndShapeRequest() {
        NetworkHandler.sendSwap2ndShapeRequest(type);
    }

    @Override
    protected void renderShape(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Render 3D entity model in the center with clean front-facing perspective
        try {
            int leftPos = getX() + getWidth() / 2;
            int topPos = getY() + getHeight() / 2;
            int x1 = leftPos - 20;
            int y1 = topPos - 16;
            int x2 = leftPos + 20;
            int y2 = topPos + 16;
            InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, x1, y1, x2, y2, size, 0.0625F, leftPos, topPos, entity);
        } catch (Exception e) {
            Remorphed.LOGGER.error("Error while rendering {}", ShapeType.createTooltipText(entity).getString(), e);
            setCrashed();
            MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
            immediate.endBatch();
            RenderSystem.getModelViewStack().popMatrix();
        }

        // Render compact trait badges in the top-left margin without blocking the model
        if (Remorphed.displayDataInMenu) {
            final int iconDisplaySize = 10;
            final float scale = 10.0F / 16.0F; // scale 16x16 icon to 10x10
            int row = 0;
            int column = 0;
            List<Identifier> renderedTraits = new ArrayList<>();
            List<ShapeTrait<T>> traits = TraitRegistry.getAll(entity);
            for (ShapeTrait<T> trait : traits) {
                if (trait != null && (!renderedTraits.contains(trait.getId()) || trait.iconMightDiffer())) {
                    if (row + iconDisplaySize > getHeight() - 2) {
                        column += iconDisplaySize + 1;
                        row = 0;
                    }
                    // Keep badges strictly within the left margin so they never cover the center model
                    if (column + iconDisplaySize > (getWidth() / 3)) {
                        break;
                    }
                    guiGraphics.pose().pushMatrix();
                    guiGraphics.pose().translate(getX() + 2 + column, getY() + 2 + row);
                    guiGraphics.pose().scale(scale, scale);
                    boolean bl = trait.renderIcon(RenderPipelines.GUI_TEXTURED, guiGraphics, 0, 0, 16, 16);
                    guiGraphics.pose().popMatrix();
                    if (bl) {
                        row += iconDisplaySize + 1;
                        renderedTraits.add(trait.getId());
                    }
                }
            }
        }
    }

    @Override
    void sendDeleteShapePacket() {
        NetworkHandler.sendDeleteShapePacket(type);
    }
}
