package ru.weryskok.mtrrumetro.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.weryskok.mtrrumetro.Constants;

public final class RMRSoundEvents {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, Constants.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> MOSCOW_OLD_TICKET_BARRIER_FAIL =
            SOUND_EVENTS.register("moscow_old_ticket_barrier_fail",
                    () -> SoundEvent.createVariableRangeEvent(Constants.id("moscow_old_ticket_barrier_fail")));

    private RMRSoundEvents() {
    }

    public static void register(IEventBus modEventBus) {
        SOUND_EVENTS.register(modEventBus);
    }
}
