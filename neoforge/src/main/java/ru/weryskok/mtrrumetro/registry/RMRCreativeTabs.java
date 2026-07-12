package ru.weryskok.mtrrumetro.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.weryskok.mtrrumetro.Constants;

public final class RMRCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN =
            CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.russianmetro.main"))
                    .icon(() -> new ItemStack(RMRBlocks.MOSCOW_OLD_TICKET_BARRIER_ENTRANCE.get()))
                    .displayItems((parameters, output) -> {
                        RMRItems.addCreativeTabItems(output::accept);
                        RMRBlocks.addCreativeTabItems(output::accept);
                    })
                    .build());

    private RMRCreativeTabs() {
    }

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
