package ru.weryskok.mtrrumetro.client;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.mtr.render.RenderPSDAPGDoor;
import ru.weryskok.mtrrumetro.Constants;
import ru.weryskok.mtrrumetro.registry.RMRBlockEntities;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class RMRBlockEntityRenderers {
    private RMRBlockEntityRenderers() {
    }

    @SubscribeEvent
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer((BlockEntityType) RMRBlockEntities.SPB_HORIZONTAL_ELEVATOR_DOOR.get(), context -> new RenderPSDAPGDoor<>(2));
        event.registerBlockEntityRenderer((BlockEntityType) RMRBlockEntities.SPB_HORIZONTAL_ELEVATOR_DOOR_ODD.get(), context -> new RenderPSDAPGDoor<>(1));
    }
}
