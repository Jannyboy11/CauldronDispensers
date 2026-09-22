package com.janboerman.cauldrondispensers;

import com.janboerman.cauldrondispensers.behaviours.*;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;
import org.bukkit.plugin.java.JavaPlugin;

/** The plugin's main class */
public class CauldronDispensers extends JavaPlugin {

    public static final Item EMPTY_BUCKET = Items.BUCKET;
    public static final Item WATER_BUCKET = Items.WATER_BUCKET;
    public static final Item LAVA_BUCKET = Items.LAVA_BUCKET;
    public static final Item POWDER_SNOW_BUCKET = Items.POWDER_SNOW_BUCKET;

    private DispenseItemBehavior originalEmptyBucketBehaviour;
    private DispenseItemBehavior originalWaterBucketBehaviour;
    private DispenseItemBehavior originalLavaBucketBehaviour;
    private DispenseItemBehavior originalPowderSnowBucketBehaviour;

    @Override
    public void onEnable() {
        this.originalEmptyBucketBehaviour = getDispenseItemBehaviour(EMPTY_BUCKET);
        this.originalWaterBucketBehaviour = getDispenseItemBehaviour(WATER_BUCKET);
        this.originalLavaBucketBehaviour = getDispenseItemBehaviour(LAVA_BUCKET);
        this.originalPowderSnowBucketBehaviour = getDispenseItemBehaviour(POWDER_SNOW_BUCKET);

        setDispenseItemBehaviour(Items.BUCKET, new EmptyBucketCauldronBehaviour(originalEmptyBucketBehaviour));
        setDispenseItemBehaviour(Items.WATER_BUCKET, new WaterBucketCauldronBehaviour(originalWaterBucketBehaviour));
        setDispenseItemBehaviour(Items.LAVA_BUCKET, new LavaBucketCauldronBehaviour(originalLavaBucketBehaviour));
        setDispenseItemBehaviour(Items.POWDER_SNOW_BUCKET, new PowderSnowBucketCauldronBehaviour(originalPowderSnowBucketBehaviour));
    }

    @Override
    public void onDisable() {
        setDispenseItemBehaviour(Items.BUCKET, originalEmptyBucketBehaviour);
        setDispenseItemBehaviour(Items.WATER_BUCKET, originalWaterBucketBehaviour);
        setDispenseItemBehaviour(Items.LAVA_BUCKET, originalLavaBucketBehaviour);
        setDispenseItemBehaviour(Items.POWDER_SNOW_BUCKET, originalPowderSnowBucketBehaviour);
    }

    private static DispenseItemBehavior getDispenseItemBehaviour(Item item) {
        return DispenserBlock.DISPENSER_REGISTRY.get(item);
    }

    private static void setDispenseItemBehaviour(Item item, DispenseItemBehavior behaviour) {
        DispenserBlock.registerBehavior(item, behaviour);
    }

}
