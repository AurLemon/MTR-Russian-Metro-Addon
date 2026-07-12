package ru.weryskok.mtrrumetro.registry;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mtr.block.BlockPSDAPGDoorBase;
import ru.weryskok.mtrrumetro.Constants;
import ru.weryskok.mtrrumetro.block.SPBHorizontalElevatorDoorBlock;

public final class RMRBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SPBHorizontalElevatorDoorBlock.BlockEntity>> SPB_HORIZONTAL_ELEVATOR_DOOR =
            register("spb_horizontal_elevator_door", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new SPBHorizontalElevatorDoorBlock.BlockEntity(pos, state, false),
                    RMRBlocks.SPB_HORIZONTAL_ELEVATOR_DOOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SPBHorizontalElevatorDoorBlock.BlockEntity>> SPB_HORIZONTAL_ELEVATOR_DOOR_ODD =
            register("spb_horizontal_elevator_door_odd", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new SPBHorizontalElevatorDoorBlock.BlockEntity(pos, state, true),
                    RMRBlocks.SPB_HORIZONTAL_ELEVATOR_DOOR_ODD.get()).build(null));

    private RMRBlockEntities() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITY_TYPES.register(modEventBus);
    }

    @SuppressWarnings("unchecked")
    private static <T extends net.minecraft.world.level.block.entity.BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(
            String name, Supplier<BlockEntityType<T>> supplier) {
        return (DeferredHolder<BlockEntityType<?>, BlockEntityType<T>>) (DeferredHolder<?, ?>) BLOCK_ENTITY_TYPES.register(name, supplier);
    }
}
