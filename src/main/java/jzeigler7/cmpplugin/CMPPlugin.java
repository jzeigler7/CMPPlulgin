package jzeigler7.cmpplugin;
import jzeigler7.cmpplugin.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *
 * Implements the CMPPlugin plugin, which manages a custom game mode that progresses
 * through a series of phases, during which, players are scored according the items
 * they have accumulated in a potentially infinite series of adjacent (non-diagonal)
 * storage containers, the location of which is indicated by the placement of a
 * reference point unique to each player.
 *
 * @author Jacob Zeigler
 * @version 1.0
 */
public final class CMPPlugin extends JavaPlugin implements Listener {
    public static HashMap<Player, Location> referenceCoordinates = new HashMap<>();
    public static HashMap<Player, ArrayList<Location>> registries = new HashMap<>();
    public static HashMap<Material, Double> pointsMap = new HashMap<>();
    public static ArrayList<Location> recursivePlaceholder = new ArrayList<>();
    public static ArrayList<Location> stales = new ArrayList<>();
    public static gamePhase currentPhase = gamePhase.NONE;
    public static boolean gameInProgress = false;
    public static boolean scoreCheckInProgress = false;
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new CMPEvents(), this);
        getCommand("startCMP").setExecutor(new startCMP());
        getCommand("getPhase").setExecutor(new getPhase());
        getCommand("setRef").setExecutor(new setRef());
        getCommand("scoreMe").setExecutor(new scoreMe());
        getCommand("appraise").setExecutor(new appraise());
        pointsMap.put(Material.SNOWBALL, 0.0025);
        pointsMap.put(Material.COARSE_DIRT, 0.01);
        pointsMap.put(Material.DIRT, 0.01);
        pointsMap.put(Material.MUD, 0.01);
        pointsMap.put(Material.PACKED_MUD, 2.01);
        pointsMap.put(Material.GRASS_BLOCK, 0.01);
        pointsMap.put(Material.DIRT_PATH, 0.01);
        pointsMap.put(Material.PODZOL, 0.01);
        pointsMap.put(Material.ROOTED_DIRT, 0.01);
        pointsMap.put(Material.SAND, 0.01);
        pointsMap.put(Material.GLASS_BOTTLE, 0.01);
        pointsMap.put(Material.MOSS_BLOCK, 0.01);
        pointsMap.put(Material.RED_SAND, 0.02);
        pointsMap.put(Material.TERRACOTTA, 0.02);
        pointsMap.put(Material.WHITE_TERRACOTTA, 0.02);
        pointsMap.put(Material.ORANGE_TERRACOTTA, 0.02);
        pointsMap.put(Material.MAGENTA_TERRACOTTA, 0.02);
        pointsMap.put(Material.LIGHT_BLUE_TERRACOTTA, 0.02);
        pointsMap.put(Material.YELLOW_TERRACOTTA, 0.02);
        pointsMap.put(Material.LIME_TERRACOTTA, 0.02);
        pointsMap.put(Material.PINK_TERRACOTTA, 0.02);
        pointsMap.put(Material.GRAY_TERRACOTTA, 0.02);
        pointsMap.put(Material.LIGHT_GRAY_TERRACOTTA, 0.02);
        pointsMap.put(Material.CYAN_TERRACOTTA, 0.02);
        pointsMap.put(Material.PURPLE_TERRACOTTA, 0.02);
        pointsMap.put(Material.BLUE_TERRACOTTA, 0.02);
        pointsMap.put(Material.BROWN_TERRACOTTA, 0.02);
        pointsMap.put(Material.GREEN_TERRACOTTA, 0.02);
        pointsMap.put(Material.RED_TERRACOTTA, 0.02);
        pointsMap.put(Material.BLACK_TERRACOTTA, 0.02);
        pointsMap.put(Material.CALCITE, 0.03);
        pointsMap.put(Material.DRIPSTONE_BLOCK, 0.03);
        pointsMap.put(Material.COBBLESTONE, 0.03);
        pointsMap.put(Material.POINTED_DRIPSTONE, 0.03 / 4.0);
        pointsMap.put(Material.KELP, 0.03);
        pointsMap.put(Material.DRIED_KELP, 0.04);
        pointsMap.put(Material.SANDSTONE, 0.04);
        pointsMap.put(Material.CHISELED_SANDSTONE, 0.04);
        pointsMap.put(Material.CUT_SANDSTONE, 0.04);
        pointsMap.put(Material.SMOOTH_SANDSTONE, 0.04);
        pointsMap.put(Material.COBBLED_DEEPSLATE, 0.04);
        pointsMap.put(Material.MOSSY_COBBLESTONE, 0.04);
        pointsMap.put(Material.RED_SANDSTONE, 0.08);
        pointsMap.put(Material.CHISELED_RED_SANDSTONE, 0.08);
        pointsMap.put(Material.CUT_RED_SANDSTONE, 0.08);
        pointsMap.put(Material.SMOOTH_RED_SANDSTONE, 0.08);
        pointsMap.put(Material.TUFF, 0.04);
        pointsMap.put(Material.POISONOUS_POTATO, 0.05);
        pointsMap.put(Material.MANGROVE_ROOTS, 0.05);
        pointsMap.put(Material.MUDDY_MANGROVE_ROOTS, 0.01);
        pointsMap.put(Material.ROTTEN_FLESH, 0.06);
        pointsMap.put(Material.ANDESITE, 0.1);
        pointsMap.put(Material.POLISHED_ANDESITE, 0.1);
        pointsMap.put(Material.BAMBOO, 0.1);
        pointsMap.put(Material.BASALT, 0.1);
        pointsMap.put(Material.POLISHED_BASALT, 0.1);
        pointsMap.put(Material.SMOOTH_BASALT, 0.1);
        pointsMap.put(Material.BLACKSTONE, 0.1);
        pointsMap.put(Material.CHISELED_POLISHED_BLACKSTONE, 0.1);
        pointsMap.put(Material.DIORITE, 0.1);
        pointsMap.put(Material.POLISHED_DIORITE, 0.1);
        pointsMap.put(Material.GRANITE, 0.1);
        pointsMap.put(Material.POLISHED_GRANITE, 0.1);
        pointsMap.put(Material.MYCELIUM, 0.1);
        pointsMap.put(Material.SPIDER_EYE, 0.1);
        pointsMap.put(Material.NETHERRACK, 0.04);
        pointsMap.put(Material.CRIMSON_NYLIUM, 0.2);
        pointsMap.put(Material.WARPED_NYLIUM, 0.2);
        pointsMap.put(Material.SOUL_SAND, 0.06);
        pointsMap.put(Material.SOUL_SOIL, 0.06);
        pointsMap.put(Material.GRASS, 0.25);
        pointsMap.put(Material.FERN, 0.25);
        pointsMap.put(Material.STICK, 0.25);
        pointsMap.put(Material.DRIED_KELP_BLOCK, 0.36);
        pointsMap.put(Material.BOWL, 0.375);
        pointsMap.put(Material.END_STONE, 0.4);
        pointsMap.put(Material.END_STONE_BRICKS, 0.4);
        pointsMap.put(Material.BONE_MEAL, 0.5);
        pointsMap.put(Material.TALL_GRASS, 0.5);
        pointsMap.put(Material.LARGE_FERN, 0.5);
        pointsMap.put(Material.BONE, 1.5);
        pointsMap.put(Material.BONE_BLOCK, 13.5);
        pointsMap.put(Material.WHITE_DYE, 0.5);
        pointsMap.put(Material.RED_DYE, 0.5);
        pointsMap.put(Material.ORANGE_DYE, 0.5);
        pointsMap.put(Material.PINK_DYE, 0.5);
        pointsMap.put(Material.YELLOW_DYE, 0.5);
        pointsMap.put(Material.LIME_DYE, 0.5);
        pointsMap.put(Material.LIGHT_BLUE_DYE, 0.5);
        pointsMap.put(Material.CYAN_DYE, 0.5);
        pointsMap.put(Material.BLUE_DYE, 0.5);
        pointsMap.put(Material.MAGENTA_DYE, 0.5);
        pointsMap.put(Material.PURPLE_DYE, 0.5);
        pointsMap.put(Material.BROWN_DYE, 0.5);
        pointsMap.put(Material.GRAY_DYE, 0.5);
        pointsMap.put(Material.LIGHT_GRAY_DYE, 0.5);
        pointsMap.put(Material.BLACK_DYE, 0.5);
        pointsMap.put(Material.ALLIUM, 0.5);
        pointsMap.put(Material.AZURE_BLUET, 0.5);
        pointsMap.put(Material.BLUE_ORCHID, 0.5);
        pointsMap.put(Material.CORNFLOWER, 0.5);
        pointsMap.put(Material.DANDELION, 0.5);
        pointsMap.put(Material.LILAC, 0.5);
        pointsMap.put(Material.LILY_OF_THE_VALLEY, 0.5);
        pointsMap.put(Material.OXEYE_DAISY, 0.5);
        pointsMap.put(Material.PEONY, 0.5);
        pointsMap.put(Material.POPPY, 0.5);
        pointsMap.put(Material.ROSE_BUSH, 0.5);
        pointsMap.put(Material.SUNFLOWER, 0.5);
        pointsMap.put(Material.ORANGE_TULIP, 0.5);
        pointsMap.put(Material.PINK_TULIP, 0.5);
        pointsMap.put(Material.RED_TULIP, 0.5);
        pointsMap.put(Material.WHITE_TULIP, 0.5);
        pointsMap.put(Material.WITHER_ROSE, 150.0);
        pointsMap.put(Material.WHITE_GLAZED_TERRACOTTA, 0.5);
        pointsMap.put(Material.ORANGE_GLAZED_TERRACOTTA, 0.5);
        pointsMap.put(Material.MAGENTA_GLAZED_TERRACOTTA, 0.5);
        pointsMap.put(Material.LIGHT_BLUE_GLAZED_TERRACOTTA, 0.5);
        pointsMap.put(Material.YELLOW_GLAZED_TERRACOTTA, 0.5);
        pointsMap.put(Material.LIME_GLAZED_TERRACOTTA, 0.5);
        pointsMap.put(Material.PINK_GLAZED_TERRACOTTA, 0.5);
        pointsMap.put(Material.GRAY_GLAZED_TERRACOTTA, 0.5);
        pointsMap.put(Material.LIGHT_GRAY_GLAZED_TERRACOTTA, 0.5);
        pointsMap.put(Material.CYAN_GLAZED_TERRACOTTA, 0.5);
        pointsMap.put(Material.PURPLE_GLAZED_TERRACOTTA, 0.5);
        pointsMap.put(Material.BLUE_GLAZED_TERRACOTTA, 0.5);
        pointsMap.put(Material.BROWN_GLAZED_TERRACOTTA, 0.5);
        pointsMap.put(Material.GREEN_GLAZED_TERRACOTTA, 0.5);
        pointsMap.put(Material.RED_GLAZED_TERRACOTTA, 0.5);
        pointsMap.put(Material.BLACK_GLAZED_TERRACOTTA, 0.5);
        pointsMap.put(Material.ACACIA_PLANKS, 0.5);
        pointsMap.put(Material.BIRCH_PLANKS, 0.5);
        pointsMap.put(Material.CRIMSON_PLANKS, 1.0);
        pointsMap.put(Material.JUNGLE_PLANKS, 0.5);
        pointsMap.put(Material.OAK_PLANKS, 0.5);
        pointsMap.put(Material.DARK_OAK_PLANKS, 0.5);
        pointsMap.put(Material.MANGROVE_PLANKS, 0.5);
        pointsMap.put(Material.SPRUCE_PLANKS, 0.5);
        pointsMap.put(Material.WARPED_PLANKS, 1.0);
        pointsMap.put(Material.DARK_OAK_DOOR, 1.0);
        pointsMap.put(Material.ACACIA_DOOR, 1.0);
        pointsMap.put(Material.BIRCH_DOOR, 1.0);
        pointsMap.put(Material.COOKIE, 1.0);
        pointsMap.put(Material.IRON_DOOR, 40.0);
        pointsMap.put(Material.CRIMSON_DOOR, 2.0);
        pointsMap.put(Material.WARPED_DOOR, 2.0);
        pointsMap.put(Material.OAK_DOOR, 1.0);
        pointsMap.put(Material.SPRUCE_DOOR, 1.0);
        pointsMap.put(Material.MANGROVE_DOOR, 1.0);
        pointsMap.put(Material.JUNGLE_DOOR, 1.0);
        pointsMap.put(Material.EGG, 2.0);
        pointsMap.put(Material.POTATO, 2.0);
        pointsMap.put(Material.BEETROOT, 2.0);
        pointsMap.put(Material.WHEAT, 2.0);
        pointsMap.put(Material.APPLE, 2.0);
        pointsMap.put(Material.SHROOMLIGHT, 2.0);
        pointsMap.put(Material.STONE_BRICKS, 2.0);
        pointsMap.put(Material.CHISELED_STONE_BRICKS, 2.0);
        pointsMap.put(Material.CRACKED_STONE_BRICKS, 2.0);
        pointsMap.put(Material.MOSSY_STONE_BRICKS, 2.0);
        pointsMap.put(Material.STRING, 2.0);
        pointsMap.put(Material.COBWEB, 2.0);
        pointsMap.put(Material.SUGAR, 2.0);
        pointsMap.put(Material.SUGAR_CANE, 2.0);
        pointsMap.put(Material.PAPER, 2.0);
        pointsMap.put(Material.WARPED_ROOTS, 2.0);
        pointsMap.put(Material.OAK_LOG, 2.0);
        pointsMap.put(Material.BIRCH_LOG, 2.0);
        pointsMap.put(Material.SPRUCE_LOG, 2.0);
        pointsMap.put(Material.JUNGLE_LOG, 2.0);
        pointsMap.put(Material.ACACIA_LOG, 2.0);
        pointsMap.put(Material.DARK_OAK_LOG, 2.0);
        pointsMap.put(Material.MANGROVE_LOG, 2.0);
        pointsMap.put(Material.CRIMSON_STEM, 4.0);
        pointsMap.put(Material.WARPED_STEM, 4.0);
        pointsMap.put(Material.STRIPPED_OAK_LOG, 2.0);
        pointsMap.put(Material.STRIPPED_BIRCH_LOG, 2.0);
        pointsMap.put(Material.STRIPPED_SPRUCE_LOG, 2.0);
        pointsMap.put(Material.BARREL, 2.0);
        pointsMap.put(Material.STRIPPED_JUNGLE_LOG, 2.0);
        pointsMap.put(Material.STRIPPED_ACACIA_LOG, 2.0);
        pointsMap.put(Material.STRIPPED_DARK_OAK_LOG, 2.0);
        pointsMap.put(Material.STRIPPED_MANGROVE_LOG, 2.0);
        pointsMap.put(Material.NETHER_BRICK, 2.5);
        pointsMap.put(Material.STRIPPED_CRIMSON_STEM, 4.0);
        pointsMap.put(Material.STRIPPED_WARPED_STEM, 4.0);
        pointsMap.put(Material.CHEST, 4.0);
        pointsMap.put(Material.WHITE_WOOL, 3.0);
        pointsMap.put(Material.MAGENTA_WOOL, 3.5);
        pointsMap.put(Material.ORANGE_WOOL, 3.5);
        pointsMap.put(Material.LIGHT_BLUE_WOOL, 3.5);
        pointsMap.put(Material.YELLOW_WOOL, 3.5);
        pointsMap.put(Material.LIME_WOOL, 3.5);
        pointsMap.put(Material.PINK_WOOL, 3.5);
        pointsMap.put(Material.GRAY_WOOL, 3.5);
        pointsMap.put(Material.LIGHT_GRAY_WOOL, 3.5);
        pointsMap.put(Material.CYAN_WOOL, 3.5);
        pointsMap.put(Material.PURPLE_WOOL, 3.5);
        pointsMap.put(Material.BLUE_WOOL, 3.5);
        pointsMap.put(Material.BROWN_WOOL, 3.5);
        pointsMap.put(Material.GREEN_WOOL, 3.5);
        pointsMap.put(Material.RED_WOOL, 3.5);
        pointsMap.put(Material.BLACK_WOOL, 3.5);
        pointsMap.put(Material.IRON_NUGGET, 3.0);
        pointsMap.put(Material.BAKED_POTATO, 3.0);
        pointsMap.put(Material.GLOW_BERRIES, 3.0);
        pointsMap.put(Material.GOLD_NUGGET, 10/3.0);
        pointsMap.put(Material.CHARCOAL, 4.0);
        pointsMap.put(Material.COAL, 4.0);
        pointsMap.put(Material.COCOA_BEANS, 4.0);
        pointsMap.put(Material.FEATHER, 4.0);
        pointsMap.put(Material.FLINT, 4.0);
        pointsMap.put(Material.INK_SAC, 4.0);
        pointsMap.put(Material.MELON_SLICE, 4.0);
        pointsMap.put(Material.GUNPOWDER, 4.0);
        pointsMap.put(Material.RABBIT_HIDE, 4.0);
        pointsMap.put(Material.HONEYCOMB, 4.0);
        pointsMap.put(Material.SWEET_BERRIES, 4.0);
        pointsMap.put(Material.CRIMSON_HYPHAE, 4.0);
        pointsMap.put(Material.WARPED_HYPHAE, 4.0);
        pointsMap.put(Material.AMETHYST_SHARD, 5/4.0);
        pointsMap.put(Material.CLAY_BALL, 5.0);
        pointsMap.put(Material.BRICK, 5.0);
        pointsMap.put(Material.GOAT_HORN, 5.0);
        pointsMap.put(Material.PAINTING, 5.0);
        pointsMap.put(Material.BROWN_MUSHROOM, 6.0);
        pointsMap.put(Material.RED_MUSHROOM, 6.0);
        pointsMap.put(Material.CANDLE, 6.0);
        pointsMap.put(Material.IRON_BARS, 7.5);
        pointsMap.put(Material.RAIL, 7.5);
        pointsMap.put(Material.COOKED_RABBIT, 8.0);
        pointsMap.put(Material.LEAD, 9.0);
        pointsMap.put(Material.NETHER_BRICKS, 10.0);
        pointsMap.put(Material.CHISELED_NETHER_BRICKS, 10.0);
        pointsMap.put(Material.CRACKED_NETHER_BRICKS, 10.0);
        pointsMap.put(Material.DEEPSLATE_BRICKS, 0.04);
        pointsMap.put(Material.CRACKED_DEEPSLATE_BRICKS, 0.04);
        pointsMap.put(Material.DEEPSLATE_TILES, 0.04);
        pointsMap.put(Material.CRACKED_DEEPSLATE_TILES, 0.04);
        pointsMap.put(Material.SLIME_BALL, 10.0);
        pointsMap.put(Material.TRIPWIRE_HOOK, 10.0);
        pointsMap.put(Material.GLOWSTONE_DUST, 10.0);
        pointsMap.put(Material.GLOW_INK_SAC, 12.0);
        pointsMap.put(Material.COOKED_COD, 12.0);
        pointsMap.put(Material.COOKED_SALMON, 12.0);
        pointsMap.put(Material.CRIMSON_FUNGUS, 12.0);
        pointsMap.put(Material.WARPED_FUNGUS, 12.0);
        pointsMap.put(Material.BEETROOT_SOUP, 13.5);
        pointsMap.put(Material.MUSHROOM_STEW, 13.5);
        pointsMap.put(Material.SUSPICIOUS_STEW, 14.0);
        pointsMap.put(Material.REDSTONE, 15.0);
        pointsMap.put(Material.COOKED_BEEF, 15.0);
        pointsMap.put(Material.COOKED_CHICKEN, 15.0);
        pointsMap.put(Material.COOKED_MUTTON, 15.0);
        pointsMap.put(Material.COOKED_PORKCHOP, 15.0);
        pointsMap.put(Material.BEEHIVE, 15.0);
        pointsMap.put(Material.LEATHER, 16.0);
        pointsMap.put(Material.HONEYCOMB_BLOCK, 65.0);
        pointsMap.put(Material.RABBIT_STEW, 17.5);
        pointsMap.put(Material.HAY_BLOCK, 18.0);
        pointsMap.put(Material.ITEM_FRAME, 18.0);
        pointsMap.put(Material.AMETHYST_BLOCK, 20.0);
        pointsMap.put(Material.COPPER_INGOT, 20.0);
        pointsMap.put(Material.IRON_INGOT, 20.0);
        pointsMap.put(Material.PRISMARINE_SHARD, 20.0);
        pointsMap.put(Material.PRISMARINE_CRYSTALS, 20.0);
        pointsMap.put(Material.RABBIT_FOOT, 20.0);
        pointsMap.put(Material.BRICKS, 20.0);
        pointsMap.put(Material.TNT, 25.0);
        pointsMap.put(Material.CHAIN, 24.5);
        pointsMap.put(Material.LAPIS_LAZULI, 25.0);
        pointsMap.put(Material.QUARTZ, 25.0);
        pointsMap.put(Material.GOLDEN_CARROT, 28.5);
        pointsMap.put(Material.GOLD_INGOT, 30.0);
        pointsMap.put(Material.PUFFERFISH, 30.0);
        pointsMap.put(Material.NETHER_WART, 30.0);
        pointsMap.put(Material.GLISTERING_MELON_SLICE, 30.5);
        pointsMap.put(Material.PUMPKIN, 32.0);
        pointsMap.put(Material.COAL_BLOCK, 36.0);
        pointsMap.put(Material.PUMPKIN_PIE, 36.0);
        pointsMap.put(Material.SPONGE, 40.0);
        pointsMap.put(Material.WET_SPONGE, 40.0);
        pointsMap.put(Material.PHANTOM_MEMBRANE, 40.0);
        pointsMap.put(Material.SPYGLASS, 41.25);
        pointsMap.put(Material.BOOK, 50.0);
        pointsMap.put(Material.SCUTE, 50.0);
        pointsMap.put(Material.GHAST_TEAR, 60.0);
        pointsMap.put(Material.WRITABLE_BOOK, 58.0);
        pointsMap.put(Material.WRITTEN_BOOK, 59.0);
        pointsMap.put(Material.SEA_LANTERN, 60.0);
        pointsMap.put(Material.WATER_BUCKET, 62.0);
        pointsMap.put(Material.RED_NETHER_BRICKS, 65.0);
        pointsMap.put(Material.MILK_BUCKET, 70.0);
        pointsMap.put(Material.PRISMARINE, 80.0);
        pointsMap.put(Material.OBSIDIAN, 80.0);
        pointsMap.put(Material.LAVA_BUCKET, 90.0);
        pointsMap.put(Material.SLIME_BLOCK, 90.0);
        pointsMap.put(Material.COMPASS, 95.0);
        pointsMap.put(Material.QUARTZ_BLOCK, 100.0);
        pointsMap.put(Material.CHISELED_QUARTZ_BLOCK, 100.0);
        pointsMap.put(Material.QUARTZ_PILLAR, 100.0);
        pointsMap.put(Material.QUARTZ_BRICKS, 100.0);
        pointsMap.put(Material.ENDER_PEARL, 100.0);
        pointsMap.put(Material.NAUTILUS_SHELL, 100.0);
        pointsMap.put(Material.MAGMA_CREAM, 120.0);
        pointsMap.put(Material.CLOCK, 135.0);
        pointsMap.put(Material.END_ROD, 137.5);
        pointsMap.put(Material.CRYING_OBSIDIAN, 140.0);
        pointsMap.put(Material.BLAZE_POWDER, 150.0);
        pointsMap.put(Material.DARK_PRISMARINE, 160.0);
        pointsMap.put(Material.COPPER_BLOCK, 180.0);
        pointsMap.put(Material.EXPOSED_COPPER, 180.0);
        pointsMap.put(Material.WEATHERED_COPPER, 180.0);
        pointsMap.put(Material.OXIDIZED_COPPER, 180.0);
        pointsMap.put(Material.WAXED_COPPER_BLOCK, 180.0);
        pointsMap.put(Material.WAXED_EXPOSED_COPPER, 180.0);
        pointsMap.put(Material.WAXED_WEATHERED_COPPER, 180.0);
        pointsMap.put(Material.WAXED_OXIDIZED_COPPER, 180.0);
        pointsMap.put(Material.CUT_COPPER, 180.0);
        pointsMap.put(Material.EXPOSED_CUT_COPPER, 180.0);
        pointsMap.put(Material.WEATHERED_CUT_COPPER, 180.0);
        pointsMap.put(Material.OXIDIZED_CUT_COPPER, 180.0);
        pointsMap.put(Material.WAXED_CUT_COPPER, 180.0);
        pointsMap.put(Material.WAXED_EXPOSED_CUT_COPPER, 180.0);
        pointsMap.put(Material.WAXED_WEATHERED_CUT_COPPER, 180.0);
        pointsMap.put(Material.WAXED_OXIDIZED_CUT_COPPER, 180.0);
        pointsMap.put(Material.IRON_BLOCK, 180.0);
        pointsMap.put(Material.PRISMARINE_BRICKS, 180.0);
        pointsMap.put(Material.DIAMOND, 200.0);
        pointsMap.put(Material.SADDLE, 200.0);
        pointsMap.put(Material.LAPIS_BLOCK, 225.0);
        pointsMap.put(Material.GOLDEN_APPLE, 242.0);
        pointsMap.put(Material.BELL, 250.0);
        pointsMap.put(Material.ENDER_EYE, 250.0);
        pointsMap.put(Material.GOLD_BLOCK, 270.0);
        pointsMap.put(Material.EMERALD, 300.0);
        pointsMap.put(Material.MUSIC_DISC_13, 310.0);
        pointsMap.put(Material.MUSIC_DISC_CAT, 300.0);
        pointsMap.put(Material.MUSIC_DISC_BLOCKS, 300.0);
        pointsMap.put(Material.MUSIC_DISC_CHIRP, 300.0);
        pointsMap.put(Material.MUSIC_DISC_FAR, 300.0);
        pointsMap.put(Material.BLAZE_ROD, 300.0);
        pointsMap.put(Material.MUSIC_DISC_MALL, 300.0);
        pointsMap.put(Material.MUSIC_DISC_MELLOHI, 300.0);
        pointsMap.put(Material.MUSIC_DISC_STAL, 300.0);
        pointsMap.put(Material.MUSIC_DISC_STRAD, 300.0);
        pointsMap.put(Material.MUSIC_DISC_WARD, 300.0);
        pointsMap.put(Material.MUSIC_DISC_11, 320.0);
        pointsMap.put(Material.MUSIC_DISC_WAIT, 300.0);
        pointsMap.put(Material.MUSIC_DISC_OTHERSIDE, 300.0);
        pointsMap.put(Material.MUSIC_DISC_5, 300.0);
        pointsMap.put(Material.MUSIC_DISC_PIGSTEP, 320.0);
        pointsMap.put(Material.END_CRYSTAL, 311.0);
        pointsMap.put(Material.NETHERITE_INGOT, 700.0);
        pointsMap.put(Material.ENDER_CHEST, 890.0);
        pointsMap.put(Material.ZOMBIE_HEAD, 900.0);
        pointsMap.put(Material.SKELETON_SKULL, 950.0);
        pointsMap.put(Material.CHORUS_FRUIT, 250.0);
        pointsMap.put(Material.POPPED_CHORUS_FRUIT, 250.0);
        pointsMap.put(Material.CREEPER_HEAD, 1000.0);
        pointsMap.put(Material.EXPERIENCE_BOTTLE, 1000.0);
        pointsMap.put(Material.PURPUR_BLOCK, 1000.0);
        pointsMap.put(Material.DRAGON_BREATH, 1000.0);
        pointsMap.put(Material.DIAMOND_BLOCK, 1800.0);
        pointsMap.put(Material.SHULKER_SHELL, 2000.0);
        pointsMap.put(Material.HEART_OF_THE_SEA, 2200.0);
        pointsMap.put(Material.ENCHANTED_GOLDEN_APPLE, 2500.0);
        pointsMap.put(Material.EMERALD_BLOCK, 2700.0);
        pointsMap.put(Material.TOTEM_OF_UNDYING, 3000.0);
        pointsMap.put(Material.ECHO_SHARD, 5000.0);
        pointsMap.put(Material.WITHER_SKELETON_SKULL, 6000.0);
        pointsMap.put(Material.NETHERITE_BLOCK, 6300.0);
        pointsMap.put(Material.DRAGON_HEAD, 15000.0);
        pointsMap.put(Material.RECOVERY_COMPASS, 40095.0);
        pointsMap.put(Material.ELYTRA, 50000.0);
        pointsMap.put(Material.NETHER_STAR, 300000.0);
        pointsMap.put(Material.DRAGON_EGG, 1500000.0);
    }

    /**
     * Runs when the plugin is disabled
     */
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public static gamePhase getCurrentPhase() {
        return currentPhase;
    }
}