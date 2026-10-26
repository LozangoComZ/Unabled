package net.lozangocomz.unabled.network;

import net.lozangocomz.unabled.Settings;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;

public record ConfigSyncPayload(String serialized) implements CustomPacketPayload {
    public static final Type<ConfigSyncPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("unabled", "config_sync")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigSyncPayload> CODEC = StreamCodec.of(
            (buffer, payload) -> buffer.writeUtf(payload.serialized()),
            buffer -> new ConfigSyncPayload(buffer.readUtf(32767))
    );

    public static ConfigSyncPayload from(Settings settings) {
        String value = String.join("|",
                Integer.toString(settings.damageMode()),
                Double.toString(settings.damagePlayer()),
                Integer.toString(settings.unequippedMode()),
                Integer.toString(settings.digToolsMode()),
                Integer.toString(settings.requiredToolsMode()),
                Double.toString(settings.breakingSpeedThreshold()),
                String.join(",", settings.ignoredIds())
        );
        return new ConfigSyncPayload(value);
    }

    public Settings settings() {
        String[] values = serialized.split("\\|", -1);
        if (values.length != 7) {
            return Settings.defaults();
        }
        try {
            return new Settings(
                    bounded(Integer.parseInt(values[0]), 1, 3),
                    bounded(Double.parseDouble(values[1]), 0.0, 20.0, 0.5),
                    bounded(Integer.parseInt(values[2]), 1, 3),
                    bounded(Integer.parseInt(values[3]), 1, 3),
                    bounded(Integer.parseInt(values[4]), 1, 3),
                    bounded(Double.parseDouble(values[5]), 0.0, 20.0, 1.25),
                    Arrays.stream(values[6].split(","))
                            .map(String::trim)
                            .filter(entry -> !entry.isEmpty())
                            .toList()
            );
        } catch (NumberFormatException ignored) {
            return Settings.defaults();
        }
    }

    private static int bounded(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static double bounded(double value, double min, double max, double fallback) {
        return Double.isFinite(value) ? Math.max(min, Math.min(max, value)) : fallback;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}