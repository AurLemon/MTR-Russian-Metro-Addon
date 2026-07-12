package ru.weryskok.mtrrumetro;

import net.minecraft.resources.ResourceLocation;

public final class Constants {
    public static final String MOD_ID = "russianmetro";
    public static final String MOD_NAME = "MTR Russian Metro Addon";

    private Constants() {
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
