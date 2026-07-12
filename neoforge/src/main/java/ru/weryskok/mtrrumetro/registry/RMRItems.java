package ru.weryskok.mtrrumetro.registry;

import java.util.function.Consumer;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.weryskok.mtrrumetro.Constants;
import ru.weryskok.mtrrumetro.item.SPBHorizontalElevatorDoorItem;

public final class RMRItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MOD_ID);

    public static final DeferredHolder<Item, Item> SPB_HORIZONTAL_ELEVATOR_DOOR =
            ITEMS.register("spb_horizontal_elevator_door",
                    () -> new SPBHorizontalElevatorDoorItem(false, new Item.Properties()));
    public static final DeferredHolder<Item, Item> SPB_HORIZONTAL_ELEVATOR_DOOR_ODD =
            ITEMS.register("spb_horizontal_elevator_door_odd",
                    () -> new SPBHorizontalElevatorDoorItem(true, new Item.Properties()));

    private RMRItems() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    public static void addCreativeTabItems(Consumer<Item> consumer) {
        consumer.accept(SPB_HORIZONTAL_ELEVATOR_DOOR.get());
        consumer.accept(SPB_HORIZONTAL_ELEVATOR_DOOR_ODD.get());
    }
}
