package ru.weryskok.mtrrumetro.client;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import ru.weryskok.mtrrumetro.Constants;
import ru.weryskok.mtrrumetro.registry.RMRBlocks;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class RMRBlockRendering {
    private RMRBlockRendering() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(RMRBlocks.MOSCOW_OLD_TICKET_BARRIER_ENTRANCE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(RMRBlocks.MOSCOW_OLD_TICKET_BARRIER_EXIT.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(RMRBlocks.MOSCOW_OLD_TICKET_BARRIER_SIDE_COVER.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(RMRBlocks.MOSCOW_NEW_TICKET_BARRIER_ENTRANCE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(RMRBlocks.MOSCOW_NEW_TICKET_BARRIER_EXIT.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(RMRBlocks.MOSCOW_NEW_TICKET_BARRIER_SIDE_COVER.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(RMRBlocks.MOSCOW_NEW_TICKET_MACHINE.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(RMRBlocks.MOSCOW_METRO_LOGO.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(RMRBlocks.MOSCOW_OLD_INFOSOS_STAND.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(RMRBlocks.SPB_HORIZONTAL_ELEVATOR_DOOR.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(RMRBlocks.SPB_HORIZONTAL_ELEVATOR_DOOR_ODD.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(RMRBlocks.TRAIN_STOP_SIGN.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(RMRBlocks.TRAIN_STOP_SIGN_1.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(RMRBlocks.TRAIN_STOP_SIGN_2.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(RMRBlocks.TRAIN_STOP_SIGN_3.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(RMRBlocks.TRAIN_STOP_SIGN_4.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(RMRBlocks.TRAIN_STOP_SIGN_5.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(RMRBlocks.TRAIN_STOP_SIGN_6.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(RMRBlocks.TRAIN_STOP_SIGN_7.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(RMRBlocks.TRAIN_STOP_SIGN_8.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(RMRBlocks.TRAIN_STOP_SIGN_9.get(), RenderType.cutout());
        });
    }
}
