package ru.weryskok.mtrrumetro.entrypoint;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.weryskok.mtrrumetro.Constants;
import ru.weryskok.mtrrumetro.registry.RMRBlockEntities;
import ru.weryskok.mtrrumetro.registry.RMRBlocks;
import ru.weryskok.mtrrumetro.registry.RMRCreativeTabs;
import ru.weryskok.mtrrumetro.registry.RMRItems;
import ru.weryskok.mtrrumetro.registry.RMRSoundEvents;

@Mod(Constants.MOD_ID)
public final class MainNeoForge {
    private static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_NAME);

    public MainNeoForge(IEventBus modEventBus) {
        RMRBlocks.register(modEventBus);
        RMRItems.register(modEventBus);
        RMRBlockEntities.register(modEventBus);
        RMRCreativeTabs.register(modEventBus);
        RMRSoundEvents.register(modEventBus);
        LOGGER.info("{} NeoForge MVP initialized", Constants.MOD_NAME);
    }
}
