package br.com.viniciushiga.lowgravity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import static br.com.viniciushiga.lowgravity.LowGravityMod.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientModEvents {
    /// Less, more gravity force we have
    private static double forceRatio = 0;

    @SubscribeEvent
    public static void onLivingMoveEvent(LivingEvent event) {
        var entity = event.getEntity();

        if (entity == null) {
            return;
        }

        if (entity.getDeltaMovement().y < 0) {
            double verticalDeltaAccel = 1.0 - forceRatio;
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(1, verticalDeltaAccel, 1));
        }
    }

    @SubscribeEvent
    public static void onLivingFallEvent(LivingFallEvent event) {
        var entity = event.getEntity();
        if (entity == null) {
            return;
        }

        event.setDamageMultiplier((float) (1.0 - forceRatio));
    }

    @SubscribeEvent
    public static void onKeyInputEvent(InputEvent.Key event) {
        var localPlayer = Minecraft.getInstance().player;
        var clientOptions = Minecraft.getInstance().options;

        if (localPlayer == null) {
            return;
        }

        handleImpulseChanges(event.getKey(), event.getAction(), localPlayer);
        handleJump(clientOptions, localPlayer);
    }

    private static void handleImpulseChanges(
        int keyIdentifier,
        int keyAction,
        LocalPlayer localPlayer
    ) {
        boolean isValueChanged = false;

        if (keyIdentifier == GLFW.GLFW_KEY_F9 && keyAction == GLFW.GLFW_PRESS) {
            double newValue = forceRatio + 0.25;
            forceRatio = Math.min(0.5, newValue);
            isValueChanged = true;
        } else if (keyIdentifier == GLFW.GLFW_KEY_F10 && keyAction == GLFW.GLFW_PRESS) {
            double newValue = forceRatio - 0.25;
            forceRatio = Math.max(0, newValue);
            isValueChanged = true;
        }

        if (isValueChanged) {
            String message = Locale.gravityForce.replace(
                "%x",
                String.valueOf((1 - forceRatio) * 100)
            );

            localPlayer.displayClientMessage(
                Component.literal(message),
                true
            );
        }
    }

    private static void handleJump(Options clientOptions, LocalPlayer localPlayer) {
        if (forceRatio != 0 && clientOptions.keyJump.isDown() && localPlayer.fallDistance == 0) {
            localPlayer.push(0f, 0.5 + forceRatio, 0f);
        }
    }
}