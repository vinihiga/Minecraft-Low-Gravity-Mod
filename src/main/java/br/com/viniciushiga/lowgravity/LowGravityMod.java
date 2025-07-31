package br.com.viniciushiga.lowgravity;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Mod(LowGravityMod.MODID)
public final class LowGravityMod {
    public static final String MODID = "low_gravity";
    private static final Logger LOGGER = LogUtils.getLogger();

    public LowGravityMod(FMLJavaModLoadingContext context) {
        var modBusGroup = context.getModBusGroup();
        var modBus = FMLCommonSetupEvent.getBus(modBusGroup);
        modBus.addListener(this::commonSetup);
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {}

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public class ClientModEvents {
        @SubscribeEvent
        public static void onLivingEvent(LivingEvent livingEvent) {
            var entity = livingEvent.getEntity();

            if (entity == null) {
                return;
            }

            if (entity.getDeltaMovement().y < 0) {
                entity.setDeltaMovement(entity.getDeltaMovement().multiply(1, 0.75, 1));
            }
        }

        @SubscribeEvent
        public static void onKeyInputEvent(InputEvent.Key event) {
            var localPlayer = Minecraft.getInstance().player;
            var clientOptions = Minecraft.getInstance().options;

            if (localPlayer == null) {
                return;
            }

            if (clientOptions.keyJump.isDown() && !localPlayer.isJumping()) {
                localPlayer.push(0f, 1.0f, 0f);
            }
        }
    }
}
