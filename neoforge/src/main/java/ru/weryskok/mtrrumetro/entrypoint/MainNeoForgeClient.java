package ru.weryskok.mtrrumetro.entrypoint;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.weryskok.mtrrumetro.Constants;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public final class MainNeoForgeClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_NAME);

    public MainNeoForgeClient(IEventBus modEventBus) {
        LOGGER.info("{} NeoForge client initialized", Constants.MOD_NAME);
    }
}
