package com.janboerman.cauldrondispensers;

import dev.faststats.bukkit.BukkitContext;
import dev.faststats.data.Metric;

import java.time.Duration;
import java.time.Instant;

final class Metrics {

    private final String FASTSTATS_API_TOKEN = "6cfde41408e66e70f85ac6bea8a74f48";

    private static final int MAJOR_JAVA_VERSION = majorJavaVersion();

    private final BukkitContext bukkitContext;

    Metrics(CauldronDispensers plugin) {
        Instant start = Instant.now();

        bukkitContext = new BukkitContext.Factory(plugin, FASTSTATS_API_TOKEN)
                .metrics(factory -> factory
                        .addMetric(Metric.number("major_java_version", () -> MAJOR_JAVA_VERSION))
                        .addMetric(Metric.number("uptime_days", () -> getDaysSince(start)))
                        .create())
                .create();
    }

    void ready() {
        bukkitContext.ready();
    }

    void shutdown() {
        bukkitContext.shutdown();
    }

    private static int majorJavaVersion() {
        String version = System.getProperty("java.version");

        if (version.startsWith("1.")) {
            // Java 8 or lower
            return Integer.parseInt(version.substring(2, 3));
        } else {
            // Java 9 or higher
            int dot = version.indexOf(".");
            return dot != -1 ? Integer.parseInt(version.substring(0, dot)) : Integer.parseInt(version);
        }
    }

    private static int getDaysSince(Instant from) {
        Instant now = Instant.now();
        return (int) Duration.between(from, now).toDays();
    }
}
