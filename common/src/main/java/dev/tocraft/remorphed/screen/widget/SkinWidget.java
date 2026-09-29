package dev.tocraft.remorphed.screen.widget;

import com.mojang.authlib.GameProfile;
import dev.tocraft.remorphed.Remorphed;
import dev.tocraft.remorphed.impl.FakeClientPlayer;
import dev.tocraft.remorphed.network.NetworkHandler;
import dev.tocraft.walkers.api.PlayerShape;
import dev.tocraft.walkers.network.impl.SwapPackets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class SkinWidget extends ShapeWidget {
    private final GameProfile skin;
    private final FakeClientPlayer fakePlayer;
    private final int size;

    public SkinWidget(int x, int y, int width, int height, @NotNull GameProfile skin, @NotNull FakeClientPlayer fakePlayer, Screen parent, boolean isFavorite, boolean isCurrent, int availability) {
        super(x, y, width, height, Component.literal(skin.name()), parent, isFavorite, isCurrent, availability);
        this.size = (int) (Remorphed.CONFIG.entity_size * (1 / (Math.max(fakePlayer.getBbHeight(), fakePlayer.getBbWidth()))));
        this.skin = skin;
        this.fakePlayer = fakePlayer;
        setTooltip(Tooltip.create(Component.literal(skin.name())));
    }

    @Override
    protected void sendFavoriteRequest(boolean isFavorite) {
        NetworkHandler.sendFavoriteRequest(skin, isFavorite);
    }

    @Override
    protected void sendSwap2ndShapeRequest() {
        NetworkHandler.sendSwapSkinRequest(skin);
        Player player = Minecraft.getInstance().player;
        if (player != null && PlayerShape.getCurrentShape(player) != null) {
            SwapPackets.sendSwapRequest();
        }
    }

    @Override
    protected void renderShape(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (skin != null) {
            int leftPos = getX() + getWidth() / 2;
            int topPos = getY() + getHeight() / 2;
            int x1 = leftPos - 20;
            int y1 = topPos - 16;
            int x2 = leftPos + 20;
            int y2 = topPos + 16;
            InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, x1, y1, x2, y2, size, 0.0625F, leftPos, topPos, fakePlayer);
        }
    }
    @Override
    void sendDeleteShapePacket() {
        NetworkHandler.sendDeleteShapePacket(skin.id());
    }
}
