package ru.weryskok.mtrrumetro.registry;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mtr.block.BlockAPGDoor;
import org.mtr.block.BlockTicketBarrier;
import org.mtr.block.BlockTicketMachine;
import ru.weryskok.mtrrumetro.Constants;
import ru.weryskok.mtrrumetro.block.FacingBlock;
import ru.weryskok.mtrrumetro.block.MoscowNewTicketBarrierSideCoverBlock;
import ru.weryskok.mtrrumetro.block.MoscowOldTicketBarrierSideCoverBlock;
import ru.weryskok.mtrrumetro.block.ThreeHighFacingBlock;
import ru.weryskok.mtrrumetro.block.SPBHorizontalElevatorDoorBlock;

public final class RMRBlocks {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MOD_ID);
    private static final Map<String, DeferredItem<BlockItem>> BLOCK_ITEMS = new LinkedHashMap<>();

    public static final DeferredBlock<Block> MOSCOW_OLD_TICKET_BARRIER_ENTRANCE =
            register("moscow_old_ticket_barrier_entrance", () -> new BlockTicketBarrier(baseProperties(3.0F).noOcclusion().lightLevel(state -> 5), true));
    public static final DeferredBlock<Block> MOSCOW_OLD_TICKET_BARRIER_EXIT =
            register("moscow_old_ticket_barrier_exit", () -> new BlockTicketBarrier(baseProperties(3.0F).noOcclusion().lightLevel(state -> 5), false));
    public static final DeferredBlock<Block> MOSCOW_OLD_TICKET_BARRIER_SIDE_COVER =
            register("moscow_old_ticket_barrier_side_cover", () -> new MoscowOldTicketBarrierSideCoverBlock(baseProperties(3.0F).noOcclusion().lightLevel(state -> 5)));
    public static final DeferredBlock<Block> MOSCOW_NEW_TICKET_BARRIER_ENTRANCE =
            register("moscow_new_ticket_barrier_entrance", () -> new BlockTicketBarrier(baseProperties(3.0F).noOcclusion().lightLevel(state -> 5), true));
    public static final DeferredBlock<Block> MOSCOW_NEW_TICKET_BARRIER_EXIT =
            register("moscow_new_ticket_barrier_exit", () -> new BlockTicketBarrier(baseProperties(3.0F).noOcclusion().lightLevel(state -> 5), false));
    public static final DeferredBlock<Block> MOSCOW_NEW_TICKET_BARRIER_SIDE_COVER =
            register("moscow_new_ticket_barrier_side_cover", () -> new MoscowNewTicketBarrierSideCoverBlock(baseProperties(3.0F).noOcclusion().lightLevel(state -> 5)));
    public static final DeferredBlock<Block> MOSCOW_NEW_TICKET_MACHINE =
            register("moscow_new_ticket_machine", () -> new BlockTicketMachine(baseProperties(3.0F).noOcclusion().lightLevel(state -> 5)));
    public static final DeferredBlock<Block> MOSCOW_METRO_LOGO =
            register("moscow_metro_logo", () -> new FacingBlock(baseProperties(2.0F).noOcclusion().lightLevel(state -> 14)));
    public static final DeferredBlock<Block> MOSCOW_OLD_INFOSOS_STAND =
            register("moscow_old_infosos_stand", () -> new ThreeHighFacingBlock(baseProperties(2.0F).noOcclusion()));
    public static final DeferredBlock<Block> TRAIN_STOP_SIGN =
            register("train_stop_sign", () -> new FacingBlock(baseProperties(2.0F).noOcclusion()));
    public static final DeferredBlock<Block> TRAIN_STOP_SIGN_1 =
            register("train_stop_sign_1", () -> new FacingBlock(baseProperties(2.0F).noOcclusion()));
    public static final DeferredBlock<Block> TRAIN_STOP_SIGN_2 =
            register("train_stop_sign_2", () -> new FacingBlock(baseProperties(2.0F).noOcclusion()));
    public static final DeferredBlock<Block> TRAIN_STOP_SIGN_3 =
            register("train_stop_sign_3", () -> new FacingBlock(baseProperties(2.0F).noOcclusion()));
    public static final DeferredBlock<Block> TRAIN_STOP_SIGN_4 =
            register("train_stop_sign_4", () -> new FacingBlock(baseProperties(2.0F).noOcclusion()));
    public static final DeferredBlock<Block> TRAIN_STOP_SIGN_5 =
            register("train_stop_sign_5", () -> new FacingBlock(baseProperties(2.0F).noOcclusion()));
    public static final DeferredBlock<Block> TRAIN_STOP_SIGN_6 =
            register("train_stop_sign_6", () -> new FacingBlock(baseProperties(2.0F).noOcclusion()));
    public static final DeferredBlock<Block> TRAIN_STOP_SIGN_7 =
            register("train_stop_sign_7", () -> new FacingBlock(baseProperties(2.0F).noOcclusion()));
    public static final DeferredBlock<Block> TRAIN_STOP_SIGN_8 =
            register("train_stop_sign_8", () -> new FacingBlock(baseProperties(2.0F).noOcclusion()));
    public static final DeferredBlock<Block> TRAIN_STOP_SIGN_9 =
            register("train_stop_sign_9", () -> new FacingBlock(baseProperties(2.0F).noOcclusion()));
    public static final DeferredBlock<Block> SPB_HORIZONTAL_ELEVATOR_DOOR =
            registerBlockOnly("spb_horizontal_elevator_door", () -> new SPBHorizontalElevatorDoorBlock(baseProperties(3.0F).noOcclusion(), false));
    public static final DeferredBlock<Block> SPB_HORIZONTAL_ELEVATOR_DOOR_ODD =
            registerBlockOnly("spb_horizontal_elevator_door_odd", () -> new SPBHorizontalElevatorDoorBlock(baseProperties(3.0F).noOcclusion(), true));

    private RMRBlocks() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
    }

    public static void addCreativeTabItems(Consumer<Item> consumer) {
        BLOCK_ITEMS.values().forEach(item -> consumer.accept(item.get()));
    }

    private static DeferredBlock<Block> register(String name, Supplier<? extends Block> supplier) {
        DeferredBlock<Block> block = BLOCKS.register(name, supplier);
        BLOCK_ITEMS.put(name, ITEMS.registerSimpleBlockItem(name, block));
        return block;
    }

    private static DeferredBlock<Block> registerBlockOnly(String name, Supplier<? extends Block> supplier) {
        return BLOCKS.register(name, supplier);
    }

    private static BlockBehaviour.Properties baseProperties(float strength) {
        return BlockBehaviour.Properties.of()
                .strength(strength)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops();
    }
}
