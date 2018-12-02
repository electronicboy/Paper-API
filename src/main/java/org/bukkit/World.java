package org.bukkit;

import java.io.File;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Consumer;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a world, which may contain entities, chunks and blocks
 */
public interface World extends PluginMessageRecipient, Metadatable {

    // Paper start
    /**
     * @return The amount of Entities in this world
     */
    int getEntityCount();

    /**
     * @return The amount of Tile Entities in this world
     */
    int getTileEntityCount();

    /**
     * @return The amount of Tickable Tile Entities in this world
     */
    int getTickableTileEntityCount();

    /**
     * @return The amount of Chunks in this world
     */
    int getChunkCount();

    /**
     * @return The amount of Players in this world
     */
    int getPlayerCount();
    // Paper end

    /**
     * Gets the {@link Block} at the given coordinates
     *
     * @param x X-coordinate of the block
     * @param y Y-coordinate of the block
     * @param z Z-coordinate of the block
     * @return Block at the given coordinates
     */
    @NotNull
    public Block getBlockAt(int x, int y, int z);

    /**
     * Gets the {@link Block} at the given {@link Location}
     *
     * @param location Location of the block
     * @return Block at the given location
     */
    @NotNull
    public Block getBlockAt(@NotNull Location location);

    // Paper start
    /**
     * Gets the {@link Block} at the given block key
     *
     * @param key The block key. See {@link Block#getBlockKey()}
     * @return Block at the key
     * @see Block#getBlockKey(int, int, int)
     */
    @NotNull
    public default Block getBlockAtKey(long key) {
        int x = Block.getBlockKeyX(key);
        int y = Block.getBlockKeyY(key);
        int z = Block.getBlockKeyZ(key);
        return getBlockAt(x, y, z);
    }

    /**
     * Gets the {@link Location} at the given block key
     *
     * @param key The block key. See {@link Location#toBlockKey()}
     * @return Location at the key
     * @see Block#getBlockKey(int, int, int)
     */
    @NotNull
    public default Location getLocationAtKey(long key) {
        int x = Block.getBlockKeyX(key);
        int y = Block.getBlockKeyY(key);
        int z = Block.getBlockKeyZ(key);
        return new Location(this, x, y, z);
    }
    // Paper end

    /**
     * Gets the y coordinate of the lowest block at this position such that the
     * block and all blocks above it are transparent for lighting purposes.
     *
     * @param x X-coordinate of the blocks
     * @param z Z-coordinate of the blocks
     * @return Y-coordinate of the described block
     */
    public int getHighestBlockYAt(int x, int z);

    /**
     * Gets the y coordinate of the lowest block at the given {@link Location}
     * such that the block and all blocks above it are transparent for lighting
     * purposes.
     *
     * @param location Location of the blocks
     * @return Y-coordinate of the highest non-air block
     */
    public int getHighestBlockYAt(@NotNull Location location);

    /**
     * Gets the lowest block at the given coordinates such that the block and
     * all blocks above it are transparent for lighting purposes.
     *
     * @param x X-coordinate of the block
     * @param z Z-coordinate of the block
     * @return Highest non-empty block
     */
    @NotNull
    public Block getHighestBlockAt(int x, int z);

    /**
     * Gets the lowest block at the given {@link Location} such that the block
     * and all blocks above it are transparent for lighting purposes.
     *
     * @param location Coordinates to get the highest block
     * @return Highest non-empty block
     */
    @NotNull
    public Block getHighestBlockAt(@NotNull Location location);

    // Paper start - Add heightmap API
    /**
     * Returns the highest block's y-coordinate at the specified block coordinates that match the specified heightmap's conditions.
     * <p>
     * <b>implNote:</b> Implementations are recommended to use an iterative search as a fallback before resorting to
     * throwing an {@code UnsupportedOperationException}.
     * </p>
     *
     * @param x The block's x-coordinate.
     * @param z The block's z-coordinate.
     * @param heightmap The specified heightmap to use. See {@link com.destroystokyo.paper.HeightmapType}
     * @return The highest block's y-coordinate at (x, z) that matches the specified heightmap's conditions.
     * @throws UnsupportedOperationException If the heightmap type is not supported.
     *
     * @see com.destroystokyo.paper.HeightmapType
     */
    public int getHighestBlockYAt(int x, int z, @NotNull com.destroystokyo.paper.HeightmapType heightmap) throws UnsupportedOperationException;

    /**
     * Returns the highest block's y-coordinate at the specified block coordinates that match the specified heightmap's conditions.
     * Note that the y-coordinate of the specified location is ignored.
     * <p>
     * <b>implNote:</b> Implementations are recommended to use an iterative search as a fallback before resorting to
     * throwing an {@code UnsupportedOperationException}.
     * </p>
     *
     * @param location The specified block coordinates.
     * @param heightmap The specified heightmap to use. See {@link com.destroystokyo.paper.HeightmapType}
     * @return The highest block's y-coordinate at {@code location} that matches the specified heightmap's conditions.
     * @throws UnsupportedOperationException If the heightmap type is not supported.
     * @see com.destroystokyo.paper.HeightmapType
     */
    default int getHighestBlockYAt(@NotNull Location location, @NotNull com.destroystokyo.paper.HeightmapType heightmap) throws UnsupportedOperationException {
        return this.getHighestBlockYAt(location.getBlockX(), location.getBlockZ(), heightmap);
    }

    /**
     * Returns the highest {@link Block} at the specified block coordinates that match the specified heightmap's conditions.
     * <p>
     * <b>implNote:</b> Implementations are recommended to use an iterative search as a fallback before resorting to
     * throwing an {@code UnsupportedOperationException}.
     * </p>
     * @param x The block's x-coordinate.
     * @param z The block's z-coordinate.
     * @param heightmap The specified heightmap to use. See {@link com.destroystokyo.paper.HeightmapType}
     * @return The highest {@link Block} at (x, z) that matches the specified heightmap's conditions.
     * @throws UnsupportedOperationException If the heightmap type is not supported.
     * @see com.destroystokyo.paper.HeightmapType
     */
    @NotNull
    default Block getHighestBlockAt(int x, int z, @NotNull com.destroystokyo.paper.HeightmapType heightmap) throws UnsupportedOperationException {
        return this.getBlockAt(x, this.getHighestBlockYAt(x, z, heightmap), z);
    }

    /**
     * Returns the highest {@link Block} at the specified block coordinates that match the specified heightmap's conditions.
     * Note that the y-coordinate of the specified location is ignored.
     * <p>
     * <b>implNote:</b> Implementations are recommended to use an iterative search as a fallback before resorting to
     * throwing an {@code UnsupportedOperationException}.
     * </p>
     * @param location The specified block coordinates.
     * @param heightmap The specified heightmap to use. See {@link com.destroystokyo.paper.HeightmapType}
     * @return The highest {@link Block} at {@code location} that matches the specified heightmap's conditions.
     * @throws UnsupportedOperationException If the heightmap type is not supported.
     * @see com.destroystokyo.paper.HeightmapType
     */
    @NotNull
    default Block getHighestBlockAt(@NotNull Location location, @NotNull com.destroystokyo.paper.HeightmapType heightmap) throws UnsupportedOperationException {
        return this.getHighestBlockAt(location.getBlockX(), location.getBlockZ(), heightmap);
    }
    // Paper end

    /**
     * Gets the {@link Chunk} at the given coordinates
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return Chunk at the given coordinates
     */
    @NotNull
    public Chunk getChunkAt(int x, int z);

    /**
     * Gets the {@link Chunk} at the given {@link Location}
     *
     * @param location Location of the chunk
     * @return Chunk at the given location
     */
    @NotNull
    public Chunk getChunkAt(@NotNull Location location);

    /**
     * Gets the {@link Chunk} that contains the given {@link Block}
     *
     * @param block Block to get the containing chunk from
     * @return The chunk that contains the given block
     */
    @NotNull
    public Chunk getChunkAt(@NotNull Block block);

    // Paper start
    /**
     * Gets the chunk at the specified chunk key, which is the X and Z packed into a long.
     *
     * See {@link Chunk#getChunkKey()} for easy access to the key, or you may calculate it as:
     * long chunkKey = (long) chunkX &amp; 0xffffffffL | ((long) chunkZ &amp; 0xffffffffL) &gt;&gt; 32;
     *
     * @param chunkKey The Chunk Key to look up the chunk by
     * @return The chunk at the specified key
     */
    @NotNull
    public default Chunk getChunkAt(long chunkKey) {
        return getChunkAt((int) chunkKey, (int) (chunkKey >> 32));
    }

    /**
     * Checks if a {@link Chunk} has been generated at the specified chunk key,
     * which is the X and Z packed into a long.
     *
     * @param chunkKey The Chunk Key to look up the chunk by
     * @return true if the chunk has been generated, otherwise false
     */
    public default boolean isChunkGenerated(long chunkKey) {
        return isChunkGenerated((int) chunkKey, (int) (chunkKey >> 32));
    }

    /**
     * This is the Legacy API before Java 8 was supported. Java 8 Consumer is provided,
     * as well as future support
     *
     * Used by {@link World#getChunkAtAsync(Location,ChunkLoadCallback)} methods
     * to request a {@link Chunk} to be loaded, with this callback receiving
     * the chunk when it is finished.
     *
     * This callback will be executed on synchronously on the main thread.
     *
     * Timing and order this callback is fired is intentionally not defined and
     * and subject to change.
     *
     * @deprecated Use either the Future or the Consumer based methods
     */
    @Deprecated
    public static interface ChunkLoadCallback extends java.util.function.Consumer<Chunk> {
        public void onLoad(@NotNull Chunk chunk);

        // backwards compat to old api
        @Override
        default void accept(@NotNull Chunk chunk) {
            onLoad(chunk);
        }
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The {@link ChunkLoadCallback} will always be executed synchronously
     * on the main Server Thread.
     *
     * @deprecated Use either the Future or the Consumer based methods
     * @param x Chunk X-coordinate of the chunk - (world coordinate / 16)
     * @param z Chunk Z-coordinate of the chunk - (world coordinate / 16)
     * @param cb Callback to receive the chunk when it is loaded.
     *           will be executed synchronously
     */
    @Deprecated
    public default void getChunkAtAsync(int x, int z, @NotNull ChunkLoadCallback cb) {
        getChunkAtAsync(x, z, true).thenAccept(cb::onLoad);
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given {@link Location}
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The {@link ChunkLoadCallback} will always be executed synchronously
     * on the main Server Thread.
     *
     * @deprecated Use either the Future or the Consumer based methods
     * @param loc Location of the chunk
     * @param cb Callback to receive the chunk when it is loaded.
     *           will be executed synchronously
     */
    @Deprecated
    public default void getChunkAtAsync(@NotNull Location loc, @NotNull ChunkLoadCallback cb) {
        getChunkAtAsync(loc, true).thenAccept(cb::onLoad);
    }

    /**
     * Requests {@link Chunk} to be loaded that contains the given {@link Block}
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The {@link ChunkLoadCallback} will always be executed synchronously
     * on the main Server Thread.
     *
     * @deprecated Use either the Future or the Consumer based methods
     * @param block Block to get the containing chunk from
     * @param cb Callback to receive the chunk when it is loaded.
     *           will be executed synchronously
     */
    @Deprecated
    public default void getChunkAtAsync(@NotNull Block block, @NotNull ChunkLoadCallback cb) {
        getChunkAtAsync(block, true).thenAccept(cb::onLoad);
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The {@link java.util.function.Consumer} will always be executed synchronously
     * on the main Server Thread.
     *
     * @param x Chunk X-coordinate of the chunk - (world coordinate / 16)
     * @param z Chunk Z-coordinate of the chunk - (world coordinate / 16)
     * @param cb Callback to receive the chunk when it is loaded.
     *           will be executed synchronously
     */
    public default void getChunkAtAsync(int x, int z, @NotNull java.util.function.Consumer<Chunk> cb) {
        getChunkAtAsync(x, z, true).thenAccept(cb);
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The {@link java.util.function.Consumer} will always be executed synchronously
     * on the main Server Thread.
     *
     * @param x Chunk X-coordinate of the chunk - (world coordinate / 16)
     * @param z Chunk Z-coordinate of the chunk - (world coordinate / 16)
     * @param gen Should we generate a chunk if it doesn't exists or not
     * @param cb Callback to receive the chunk when it is loaded.
     *           will be executed synchronously
     */
    public default void getChunkAtAsync(int x, int z, boolean gen, @NotNull java.util.function.Consumer<Chunk> cb) {
        getChunkAtAsync(x, z, gen).thenAccept(cb);
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given {@link Location}
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The {@link java.util.function.Consumer} will always be executed synchronously
     * on the main Server Thread.
     *
     * @param loc Location of the chunk
     * @param cb Callback to receive the chunk when it is loaded.
     *           will be executed synchronously
     */
    public default void getChunkAtAsync(@NotNull Location loc, @NotNull java.util.function.Consumer<Chunk> cb) {
        getChunkAtAsync((int)Math.floor(loc.getX()) >> 4, (int)Math.floor(loc.getZ()) >> 4, true, cb);
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given {@link Location}
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The {@link java.util.function.Consumer} will always be executed synchronously
     * on the main Server Thread.
     *
     * @param loc Location of the chunk
     * @param gen Should the chunk generate
     * @param cb Callback to receive the chunk when it is loaded.
     *           will be executed synchronously
     */
    public default void getChunkAtAsync(@NotNull Location loc, boolean gen, @NotNull java.util.function.Consumer<Chunk> cb) {
        getChunkAtAsync((int)Math.floor(loc.getX()) >> 4, (int)Math.floor(loc.getZ()) >> 4, gen, cb);
    }

    /**
     * Requests {@link Chunk} to be loaded that contains the given {@link Block}
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The {@link java.util.function.Consumer} will always be executed synchronously
     * on the main Server Thread.
     *
     * @param block Block to get the containing chunk from
     * @param cb Callback to receive the chunk when it is loaded.
     *           will be executed synchronously
     */
    public default void getChunkAtAsync(@NotNull Block block, @NotNull java.util.function.Consumer<Chunk> cb) {
        getChunkAtAsync(block.getX() >> 4, block.getZ() >> 4, true, cb);
    }

    /**
     * Requests {@link Chunk} to be loaded that contains the given {@link Block}
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The {@link java.util.function.Consumer} will always be executed synchronously
     * on the main Server Thread.
     *
     * @param block Block to get the containing chunk from
     * @param gen Should the chunk generate
     * @param cb Callback to receive the chunk when it is loaded.
     *           will be executed synchronously
     */
    public default void getChunkAtAsync(@NotNull Block block, boolean gen, @NotNull java.util.function.Consumer<Chunk> cb) {
        getChunkAtAsync(block.getX() >> 4, block.getZ() >> 4, gen, cb);
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The future will always be executed synchronously
     * on the main Server Thread.
     * @param loc Location to load the corresponding chunk from
     * @return Future that will resolve when the chunk is loaded
     */
    @NotNull
    public default java.util.concurrent.CompletableFuture<Chunk> getChunkAtAsync(@NotNull Location loc) {
        return getChunkAtAsync((int)Math.floor(loc.getX()) >> 4, (int)Math.floor(loc.getZ()) >> 4, true);
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The future will always be executed synchronously
     * on the main Server Thread.
     * @param loc Location to load the corresponding chunk from
     * @param gen Should the chunk generate
     * @return Future that will resolve when the chunk is loaded
     */
    @NotNull
    public default java.util.concurrent.CompletableFuture<Chunk> getChunkAtAsync(@NotNull Location loc, boolean gen) {
        return getChunkAtAsync((int)Math.floor(loc.getX()) >> 4, (int)Math.floor(loc.getZ()) >> 4, gen);
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The future will always be executed synchronously
     * on the main Server Thread.
     * @param block Block to load the corresponding chunk from
     * @return Future that will resolve when the chunk is loaded
     */
    @NotNull
    public default java.util.concurrent.CompletableFuture<Chunk> getChunkAtAsync(@NotNull Block block) {
        return getChunkAtAsync(block.getX() >> 4, block.getZ() >> 4, true);
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The future will always be executed synchronously
     * on the main Server Thread.
     * @param block Block to load the corresponding chunk from
     * @param gen Should the chunk generate
     * @return Future that will resolve when the chunk is loaded
     */
    @NotNull
    public default java.util.concurrent.CompletableFuture<Chunk> getChunkAtAsync(@NotNull Block block, boolean gen) {
        return getChunkAtAsync(block.getX() >> 4, block.getZ() >> 4, gen);
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The future will always be executed synchronously
     * on the main Server Thread.
     *
     * @param x X Coord
     * @param z Z Coord
     * @return Future that will resolve when the chunk is loaded
     */
    @NotNull
    public default java.util.concurrent.CompletableFuture<Chunk> getChunkAtAsync(int x, int z) {
        return getChunkAtAsync(x, z, true);
    }

    /**
     * Requests a {@link Chunk} to be loaded at the given coordinates
     *
     * This method makes no guarantee on how fast the chunk will load,
     * and will return the chunk to the callback at a later time.
     *
     * You should use this method if you need a chunk but do not need it
     * immediately, and you wish to let the server control the speed
     * of chunk loads, keeping performance in mind.
     *
     * The future will always be executed synchronously
     * on the main Server Thread.
     *
     * @param x Chunk X-coordinate of the chunk - (world coordinate / 16)
     * @param z Chunk Z-coordinate of the chunk - (world coordinate / 16)
     * @param gen Should we generate a chunk if it doesn't exists or not
     * @return Future that will resolve when the chunk is loaded
     */
    @NotNull
    public java.util.concurrent.CompletableFuture<Chunk> getChunkAtAsync(int x, int z, boolean gen);
    // Paper end

    /**
     * Checks if the specified {@link Chunk} is loaded
     *
     * @param chunk The chunk to check
     * @return true if the chunk is loaded, otherwise false
     */
    public boolean isChunkLoaded(@NotNull Chunk chunk);

    /**
     * Gets an array of all loaded {@link Chunk}s
     *
     * @return Chunk[] containing all loaded chunks
     */
    @NotNull
    public Chunk[] getLoadedChunks();

    /**
     * Loads the specified {@link Chunk}.
     * <p>
     * <b>This method will keep the specified chunk loaded until one of the
     * unload methods is manually called. Callers are advised to instead use
     * getChunkAt which will only temporarily load the requested chunk.</b>
     *
     * @param chunk The chunk to load
     */
    public void loadChunk(@NotNull Chunk chunk);

    /**
     * Checks if the {@link Chunk} at the specified coordinates is loaded
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return true if the chunk is loaded, otherwise false
     */
    public boolean isChunkLoaded(int x, int z);

    /**
     * Checks if the {@link Chunk} at the specified coordinates is generated
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return true if the chunk is generated, otherwise false
     */
    public boolean isChunkGenerated(int x, int z);

    /**
     * Checks if the {@link Chunk} at the specified coordinates is loaded and
     * in use by one or more players
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return true if the chunk is loaded and in use by one or more players,
     *     otherwise false
     * @deprecated This method was added to facilitate chunk garbage collection.
     *     As of the current Minecraft version chunks are now strictly managed and
     *     will not be loaded for more than 1 tick unless they are in use.
     */
    @Deprecated
    public boolean isChunkInUse(int x, int z);

    /**
     * Loads the {@link Chunk} at the specified coordinates.
     * <p>
     * <b>This method will keep the specified chunk loaded until one of the
     * unload methods is manually called. Callers are advised to instead use
     * getChunkAt which will only temporarily load the requested chunk.</b>
     * <p>
     * If the chunk does not exist, it will be generated.
     * <p>
     * This method is analogous to {@link #loadChunk(int, int, boolean)} where
     * generate is true.
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     */
    public void loadChunk(int x, int z);

    /**
     * Loads the {@link Chunk} at the specified coordinates.
     * <p>
     * <b>This method will keep the specified chunk loaded until one of the
     * unload methods is manually called. Callers are advised to instead use
     * getChunkAt which will only temporarily load the requested chunk.</b>
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @param generate Whether or not to generate a chunk if it doesn't
     *     already exist
     * @return true if the chunk has loaded successfully, otherwise false
     */
    public boolean loadChunk(int x, int z, boolean generate);

    /**
     * Safely unloads and saves the {@link Chunk} at the specified coordinates
     * <p>
     * This method is analogous to {@link #unloadChunk(int, int, boolean)}
     * where save is true.
     *
     * @param chunk the chunk to unload
     * @return true if the chunk has unloaded successfully, otherwise false
     */
    public boolean unloadChunk(@NotNull Chunk chunk);

    /**
     * Safely unloads and saves the {@link Chunk} at the specified coordinates
     * <p>
     * This method is analogous to {@link #unloadChunk(int, int, boolean)}
     * where save is true.
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return true if the chunk has unloaded successfully, otherwise false
     */
    public boolean unloadChunk(int x, int z);

    /**
     * Safely unloads and optionally saves the {@link Chunk} at the specified
     * coordinates.
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @param save Whether or not to save the chunk
     * @return true if the chunk has unloaded successfully, otherwise false
     */
    public boolean unloadChunk(int x, int z, boolean save);

    /**
     * Safely queues the {@link Chunk} at the specified coordinates for
     * unloading.
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return true is the queue attempt was successful, otherwise false
     */
    public boolean unloadChunkRequest(int x, int z);

    /**
     * Regenerates the {@link Chunk} at the specified coordinates
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return Whether the chunk was actually regenerated
     *
     * @deprecated regenerating a single chunk is not likely to produce the same
     * chunk as before as terrain decoration may be spread across chunks. Use of
     * this method should be avoided as it is known to produce buggy results.
     */
    @Deprecated
    public boolean regenerateChunk(int x, int z);

    /**
     * Resends the {@link Chunk} to all clients
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return Whether the chunk was actually refreshed
     *
     * @deprecated This method is not guaranteed to work suitably across all client implementations.
     */
    @Deprecated
    public boolean refreshChunk(int x, int z);

    /**
     * Gets whether the chunk at the specified chunk coordinates is force
     * loaded.
     * <p>
     * A force loaded chunk will not be unloaded due to lack of player activity.
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @return force load status
     */
    public boolean isChunkForceLoaded(int x, int z);

    /**
     * Sets whether the chunk at the specified chunk coordinates is force
     * loaded.
     * <p>
     * A force loaded chunk will not be unloaded due to lack of player activity.
     *
     * @param x X-coordinate of the chunk
     * @param z Z-coordinate of the chunk
     * @param forced force load status
     */
    public void setChunkForceLoaded(int x, int z, boolean forced);

    /**
     * Returns all force loaded chunks in this world.
     * <p>
     * A force loaded chunk will not be unloaded due to lack of player activity.
     *
     * @return unmodifiable collection of force loaded chunks
     */
    @NotNull
    public Collection<Chunk> getForceLoadedChunks();

    /**
     * Drops an item at the specified {@link Location}
     *
     * @param location Location to drop the item
     * @param item ItemStack to drop
     * @return ItemDrop entity created as a result of this method
     */
    @NotNull
    public Item dropItem(@NotNull Location location, @NotNull ItemStack item);

    /**
     * Drops an item at the specified {@link Location} with a random offset
     *
     * @param location Location to drop the item
     * @param item ItemStack to drop
     * @return ItemDrop entity created as a result of this method
     */
    @NotNull
    public Item dropItemNaturally(@NotNull Location location, @NotNull ItemStack item);

    /**
     * Creates an {@link Arrow} entity at the given {@link Location}
     *
     * @param location Location to spawn the arrow
     * @param direction Direction to shoot the arrow in
     * @param speed Speed of the arrow. A recommend speed is 0.6
     * @param spread Spread of the arrow. A recommend spread is 12
     * @return Arrow entity spawned as a result of this method
     */
    @NotNull
    public Arrow spawnArrow(@NotNull Location location, @NotNull Vector direction, float speed, float spread);

    /**
     * Creates an arrow entity of the given class at the given {@link Location}
     *
     * @param <T> type of arrow to spawn
     * @param location Location to spawn the arrow
     * @param direction Direction to shoot the arrow in
     * @param speed Speed of the arrow. A recommend speed is 0.6
     * @param spread Spread of the arrow. A recommend spread is 12
     * @param clazz the Entity class for the arrow
     * {@link org.bukkit.entity.SpectralArrow},{@link org.bukkit.entity.Arrow},{@link org.bukkit.entity.TippedArrow}
     * @return Arrow entity spawned as a result of this method
     */
    @NotNull
    public <T extends AbstractArrow> T spawnArrow(@NotNull Location location, @NotNull Vector direction, float speed, float spread, @NotNull Class<T> clazz);

    /**
     * Creates a tree at the given {@link Location}
     *
     * @param location Location to spawn the tree
     * @param type Type of the tree to create
     * @return true if the tree was created successfully, otherwise false
     */
    public boolean generateTree(@NotNull Location location, @NotNull TreeType type);

    /**
     * Creates a tree at the given {@link Location}
     *
     * @param loc Location to spawn the tree
     * @param type Type of the tree to create
     * @param delegate A class to call for each block changed as a result of
     *     this method
     * @return true if the tree was created successfully, otherwise false
     */
    public boolean generateTree(@NotNull Location loc, @NotNull TreeType type, @NotNull BlockChangeDelegate delegate);

    /**
     * Creates a entity at the given {@link Location}
     *
     * @param loc The location to spawn the entity
     * @param type The entity to spawn
     * @return Resulting Entity of this method, or null if it was unsuccessful
     */
    @NotNull
    public Entity spawnEntity(@NotNull Location loc, @NotNull EntityType type);

    /**
     * Strikes lightning at the given {@link Location}
     *
     * @param loc The location to strike lightning
     * @return The lightning entity.
     */
    @NotNull
    public LightningStrike strikeLightning(@NotNull Location loc);

    /**
     * Strikes lightning at the given {@link Location} without doing damage
     *
     * @param loc The location to strike lightning
     * @return The lightning entity.
     */
    @NotNull
    public LightningStrike strikeLightningEffect(@NotNull Location loc);

    /**
     * Get a list of all entities in this World
     *
     * @return A List of all Entities currently residing in this world
     */
    @NotNull
    public List<Entity> getEntities();

    /**
     * Get a list of all living entities in this World
     *
     * @return A List of all LivingEntities currently residing in this world
     */
    @NotNull
    public List<LivingEntity> getLivingEntities();

    /**
     * Get a collection of all entities in this World matching the given
     * class/interface
     *
     * @param <T> an entity subclass
     * @param classes The classes representing the types of entity to match
     * @return A List of all Entities currently residing in this world that
     *     match the given class/interface
     */
    @Deprecated
    @NotNull
    public <T extends Entity> Collection<T> getEntitiesByClass(@NotNull Class<T>... classes);

    /**
     * Get a collection of all entities in this World matching the given
     * class/interface
     *
     * @param <T> an entity subclass
     * @param cls The class representing the type of entity to match
     * @return A List of all Entities currently residing in this world that
     *     match the given class/interface
     */
    @NotNull
    public <T extends Entity> Collection<T> getEntitiesByClass(@NotNull Class<T> cls);

    /**
     * Get a collection of all entities in this World matching any of the
     * given classes/interfaces
     *
     * @param classes The classes representing the types of entity to match
     * @return A List of all Entities currently residing in this world that
     *     match one or more of the given classes/interfaces
     */
    @NotNull
    public Collection<Entity> getEntitiesByClasses(@NotNull Class<?>... classes);

    // Paper start
    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param radius Radius
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    @NotNull
    public default Collection<LivingEntity> getNearbyLivingEntities(@NotNull Location loc, double radius) {
        return getNearbyEntitiesByType(org.bukkit.entity.LivingEntity.class, loc, radius, radius, radius);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param xzRadius X/Z Radius
     * @param yRadius Y Radius
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    @NotNull
    public default Collection<LivingEntity> getNearbyLivingEntities(@NotNull Location loc, double xzRadius, double yRadius) {
        return getNearbyEntitiesByType(org.bukkit.entity.LivingEntity.class, loc, xzRadius, yRadius, xzRadius);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param xRadius X Radius
     * @param yRadius Y Radius
     * @param zRadius Z radius
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    @NotNull
    public default Collection<LivingEntity> getNearbyLivingEntities(@NotNull Location loc, double xRadius, double yRadius, double zRadius) {
        return getNearbyEntitiesByType(org.bukkit.entity.LivingEntity.class, loc, xRadius, yRadius, zRadius);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param radius X Radius
     * @param predicate a predicate used to filter results
     * @return the collection of living entities near location. This will always be a non-null collection
     */
    @NotNull
    public default Collection<LivingEntity> getNearbyLivingEntities(@NotNull Location loc, double radius, @Nullable Predicate<LivingEntity> predicate) {
        return getNearbyEntitiesByType(org.bukkit.entity.LivingEntity.class, loc, radius, radius, radius, predicate);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param xzRadius X/Z Radius
     * @param yRadius Y Radius
     * @param predicate a predicate used to filter results
     * @return the collection of living entities near location. This will always be a non-null collection
     */
    @NotNull
    public default Collection<LivingEntity> getNearbyLivingEntities(@NotNull Location loc, double xzRadius, double yRadius, @Nullable Predicate<LivingEntity> predicate) {
        return getNearbyEntitiesByType(org.bukkit.entity.LivingEntity.class, loc, xzRadius, yRadius, xzRadius, predicate);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param xRadius X Radius
     * @param yRadius Y Radius
     * @param zRadius Z radius
     * @param predicate a predicate used to filter results
     * @return the collection of living entities near location. This will always be a non-null collection.
     */
    @NotNull
    public default Collection<LivingEntity> getNearbyLivingEntities(@NotNull Location loc, double xRadius, double yRadius, double zRadius, @Nullable Predicate<LivingEntity> predicate) {
        return getNearbyEntitiesByType(org.bukkit.entity.LivingEntity.class, loc, xRadius, yRadius, zRadius, predicate);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param radius X/Y/Z Radius
     * @return the collection of living entities near location. This will always be a non-null collection.
     */
    @NotNull
    public default Collection<Player> getNearbyPlayers(@NotNull Location loc, double radius) {
        return getNearbyEntitiesByType(org.bukkit.entity.Player.class, loc, radius, radius, radius);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param xzRadius X/Z Radius
     * @param yRadius Y Radius
     * @return the collection of living entities near location. This will always be a non-null collection.
     */
    @NotNull
    public default Collection<Player> getNearbyPlayers(@NotNull Location loc, double xzRadius, double yRadius) {
        return getNearbyEntitiesByType(org.bukkit.entity.Player.class, loc, xzRadius, yRadius, xzRadius);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param xRadius X Radius
     * @param yRadius Y Radius
     * @param zRadius Z Radius
     * @return the collection of players near location. This will always be a non-null collection.
     */
    @NotNull
    public default Collection<Player> getNearbyPlayers(@NotNull Location loc, double xRadius, double yRadius, double zRadius) {
        return getNearbyEntitiesByType(org.bukkit.entity.Player.class, loc, xRadius, yRadius, zRadius);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param radius X/Y/Z Radius
     * @param predicate a predicate used to filter results
     * @return the collection of players near location. This will always be a non-null collection.
     */
    @NotNull
    public default Collection<Player> getNearbyPlayers(@NotNull Location loc, double radius, @Nullable Predicate<Player> predicate) {
        return getNearbyEntitiesByType(org.bukkit.entity.Player.class, loc, radius, radius, radius, predicate);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param xzRadius X/Z Radius
     * @param yRadius Y Radius
     * @param predicate a predicate used to filter results
     * @return the collection of players near location. This will always be a non-null collection.
     */
    @NotNull
    public default Collection<Player> getNearbyPlayers(@NotNull Location loc, double xzRadius, double yRadius, @Nullable Predicate<Player> predicate) {
        return getNearbyEntitiesByType(org.bukkit.entity.Player.class, loc, xzRadius, yRadius, xzRadius, predicate);
    }

    /**
     * Gets nearby players within the specified radius (bounding box)
     * @param loc Center location
     * @param xRadius X Radius
     * @param yRadius Y Radius
     * @param zRadius Z Radius
     * @param predicate a predicate used to filter results
     * @return the collection of players near location. This will always be a non-null collection.
     */
    @NotNull
    public default Collection<Player> getNearbyPlayers(@NotNull Location loc, double xRadius, double yRadius, double zRadius, @Nullable Predicate<Player> predicate) {
        return getNearbyEntitiesByType(org.bukkit.entity.Player.class, loc, xRadius, yRadius, zRadius, predicate);
    }

    /**
     * Gets all nearby entities of the specified type, within the specified radius (bounding box)
     * @param clazz Type to filter by
     * @param loc Center location
     * @param radius X/Y/Z radius to search within
     * @param <T> the entity type
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    @NotNull
    public default <T extends Entity> Collection<T> getNearbyEntitiesByType(@Nullable Class<? extends T> clazz, @NotNull Location loc, double radius) {
        return getNearbyEntitiesByType(clazz, loc, radius, radius, radius, null);
    }

    /**
     * Gets all nearby entities of the specified type, within the specified radius, with x and x radius matching (bounding box)
     * @param clazz Type to filter by
     * @param loc Center location
     * @param xzRadius X/Z radius to search within
     * @param yRadius Y radius to search within
     * @param <T> the entity type
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    @NotNull
    public default <T extends Entity> Collection<T> getNearbyEntitiesByType(@Nullable Class<? extends T> clazz, @NotNull Location loc, double xzRadius, double yRadius) {
        return getNearbyEntitiesByType(clazz, loc, xzRadius, yRadius, xzRadius, null);
    }

    /**
     * Gets all nearby entities of the specified type, within the specified radius (bounding box)
     * @param clazz Type to filter by
     * @param loc Center location
     * @param xRadius X Radius
     * @param yRadius Y Radius
     * @param zRadius Z Radius
     * @param <T> the entity type
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    @NotNull
    public default <T extends Entity> Collection<T> getNearbyEntitiesByType(@Nullable Class<? extends T> clazz, @NotNull Location loc, double xRadius, double yRadius, double zRadius) {
        return getNearbyEntitiesByType(clazz, loc, xRadius, yRadius, zRadius, null);
    }

    /**
     * Gets all nearby entities of the specified type, within the specified radius (bounding box)
     * @param clazz Type to filter by
     * @param loc Center location
     * @param radius X/Y/Z radius to search within
     * @param predicate a predicate used to filter results
     * @param <T> the entity type
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    @NotNull
    public default <T extends Entity> Collection<T> getNearbyEntitiesByType(@Nullable Class<? extends T> clazz, @NotNull Location loc, double radius, @Nullable Predicate<T> predicate) {
        return getNearbyEntitiesByType(clazz, loc, radius, radius, radius, predicate);
    }

    /**
     * Gets all nearby entities of the specified type, within the specified radius, with x and x radius matching (bounding box)
     * @param clazz Type to filter by
     * @param loc Center location
     * @param xzRadius X/Z radius to search within
     * @param yRadius Y radius to search within
     * @param predicate a predicate used to filter results
     * @param <T> the entity type
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    @NotNull
    public default <T extends Entity> Collection<T> getNearbyEntitiesByType(@Nullable Class<? extends T> clazz, @NotNull Location loc, double xzRadius, double yRadius, @Nullable Predicate<T> predicate) {
        return getNearbyEntitiesByType(clazz, loc, xzRadius, yRadius, xzRadius, predicate);
    }

     /**
      * Gets all nearby entities of the specified type, within the specified radius (bounding box)
      * @param clazz Type to filter by
      * @param loc Center location
      * @param xRadius X Radius
      * @param yRadius Y Radius
      * @param zRadius Z Radius
      * @param predicate a predicate used to filter results
      * @param <T> the entity type
      * @return the collection of entities near location. This will always be a non-null collection.
      */
     @NotNull
    public default <T extends Entity> Collection<T> getNearbyEntitiesByType(@Nullable Class<? extends Entity> clazz, @NotNull Location loc, double xRadius, double yRadius, double zRadius, @Nullable Predicate<T> predicate) {
        if (clazz == null) {
            clazz = Entity.class;
        }
        List<T> nearby = new ArrayList<>();
        for (Entity bukkitEntity : getNearbyEntities(loc, xRadius, yRadius, zRadius)) {
            //noinspection unchecked
            if (clazz.isAssignableFrom(bukkitEntity.getClass()) && (predicate == null || predicate.test((T) bukkitEntity))) {
                //noinspection unchecked
                nearby.add((T) bukkitEntity);
            }
        }
        return nearby;
    }
    // Paper end

    /**
     * Get a list of all players in this World
     *
     * @return A list of all Players currently residing in this world
     */
    @NotNull
    public List<Player> getPlayers();

    /**
     * Returns a list of entities within a bounding box centered around a
     * Location.
     * <p>
     * This may not consider entities in currently unloaded chunks. Some
     * implementations may impose artificial restrictions on the size of the
     * search bounding box.
     *
     * @param location The center of the bounding box
     * @param x 1/2 the size of the box along x axis
     * @param y 1/2 the size of the box along y axis
     * @param z 1/2 the size of the box along z axis
     * @return the collection of entities near location. This will always be a
     *      non-null collection.
     */
    @NotNull
    public Collection<Entity> getNearbyEntities(@NotNull Location location, double x, double y, double z);

    // Paper start - getEntity by UUID API
    /**
     * Gets an entity in this world by its UUID
     *
     * @param uuid the UUID of the entity
     * @return the entity with the given UUID, or null if it isn't found
     */
    @Nullable
    public Entity getEntity(@NotNull UUID uuid);
    // Paper end

    /**
     * Returns a list of entities within a bounding box centered around a
     * Location.
     * <p>
     * This may not consider entities in currently unloaded chunks. Some
     * implementations may impose artificial restrictions on the size of the
     * search bounding box.
     *
     * @param location The center of the bounding box
     * @param x 1/2 the size of the box along x axis
     * @param y 1/2 the size of the box along y axis
     * @param z 1/2 the size of the box along z axis
     * @param filter only entities that fulfill this predicate are considered,
     *     or <code>null</code> to consider all entities
     * @return the collection of entities near location. This will always be a
     *     non-null collection.
     */
    @NotNull
    public Collection<Entity> getNearbyEntities(@NotNull Location location, double x, double y, double z, @Nullable Predicate<Entity> filter);

    /**
     * Returns a list of entities within the given bounding box.
     * <p>
     * This may not consider entities in currently unloaded chunks. Some
     * implementations may impose artificial restrictions on the size of the
     * search bounding box.
     *
     * @param boundingBox the bounding box
     * @return the collection of entities within the bounding box, will always
     *     be a non-null collection
     */
    @NotNull
    public Collection<Entity> getNearbyEntities(@NotNull BoundingBox boundingBox);

    /**
     * Returns a list of entities within the given bounding box.
     * <p>
     * This may not consider entities in currently unloaded chunks. Some
     * implementations may impose artificial restrictions on the size of the
     * search bounding box.
     *
     * @param boundingBox the bounding box
     * @param filter only entities that fulfill this predicate are considered,
     *     or <code>null</code> to consider all entities
     * @return the collection of entities within the bounding box, will always
     *     be a non-null collection
     */
    @NotNull
    public Collection<Entity> getNearbyEntities(@NotNull BoundingBox boundingBox, @Nullable Predicate<Entity> filter);

    /**
     * Performs a ray trace that checks for entity collisions.
     * <p>
     * This may not consider entities in currently unloaded chunks. Some
     * implementations may impose artificial restrictions on the maximum
     * distance.
     *
     * @param start the start position
     * @param direction the ray direction
     * @param maxDistance the maximum distance
     * @return the closest ray trace hit result, or <code>null</code> if there
     *     is no hit
     * @see #rayTraceEntities(Location, Vector, double, double, Predicate)
     */
    @Nullable
    public RayTraceResult rayTraceEntities(@NotNull Location start, @NotNull Vector direction, double maxDistance);

    /**
     * Performs a ray trace that checks for entity collisions.
     * <p>
     * This may not consider entities in currently unloaded chunks. Some
     * implementations may impose artificial restrictions on the maximum
     * distance.
     *
     * @param start the start position
     * @param direction the ray direction
     * @param maxDistance the maximum distance
     * @param raySize entity bounding boxes will be uniformly expanded (or
     *     shrinked) by this value before doing collision checks
     * @return the closest ray trace hit result, or <code>null</code> if there
     *     is no hit
     * @see #rayTraceEntities(Location, Vector, double, double, Predicate)
     */
    @Nullable
    public RayTraceResult rayTraceEntities(@NotNull Location start, @NotNull Vector direction, double maxDistance, double raySize);

    /**
     * Performs a ray trace that checks for entity collisions.
     * <p>
     * This may not consider entities in currently unloaded chunks. Some
     * implementations may impose artificial restrictions on the maximum
     * distance.
     *
     * @param start the start position
     * @param direction the ray direction
     * @param maxDistance the maximum distance
     * @param filter only entities that fulfill this predicate are considered,
     *     or <code>null</code> to consider all entities
     * @return the closest ray trace hit result, or <code>null</code> if there
     *     is no hit
     * @see #rayTraceEntities(Location, Vector, double, double, Predicate)
     */
    @Nullable
    public RayTraceResult rayTraceEntities(@NotNull Location start, @NotNull Vector direction, double maxDistance, @Nullable Predicate<Entity> filter);

    /**
     * Performs a ray trace that checks for entity collisions.
     * <p>
     * This may not consider entities in currently unloaded chunks. Some
     * implementations may impose artificial restrictions on the maximum
     * distance.
     *
     * @param start the start position
     * @param direction the ray direction
     * @param maxDistance the maximum distance
     * @param raySize entity bounding boxes will be uniformly expanded (or
     *     shrinked) by this value before doing collision checks
     * @param filter only entities that fulfill this predicate are considered,
     *     or <code>null</code> to consider all entities
     * @return the closest ray trace hit result, or <code>null</code> if there
     *     is no hit
     */
    @Nullable
    public RayTraceResult rayTraceEntities(@NotNull Location start, @NotNull Vector direction, double maxDistance, double raySize, @Nullable Predicate<Entity> filter);

    /**
     * Performs a ray trace that checks for block collisions using the blocks'
     * precise collision shapes.
     * <p>
     * This takes collisions with passable blocks into account, but ignores
     * fluids.
     * <p>
     * This may cause loading of chunks! Some implementations may impose
     * artificial restrictions on the maximum distance.
     *
     * @param start the start location
     * @param direction the ray direction
     * @param maxDistance the maximum distance
     * @return the ray trace hit result, or <code>null</code> if there is no hit
     * @see #rayTraceBlocks(Location, Vector, double, FluidCollisionMode, boolean)
     */
    @Nullable
    public RayTraceResult rayTraceBlocks(@NotNull Location start, @NotNull Vector direction, double maxDistance);

    /**
     * Performs a ray trace that checks for block collisions using the blocks'
     * precise collision shapes.
     * <p>
     * This takes collisions with passable blocks into account.
     * <p>
     * This may cause loading of chunks! Some implementations may impose
     * artificial restrictions on the maximum distance.
     *
     * @param start the start location
     * @param direction the ray direction
     * @param maxDistance the maximum distance
     * @param fluidCollisionMode the fluid collision mode
     * @return the ray trace hit result, or <code>null</code> if there is no hit
     * @see #rayTraceBlocks(Location, Vector, double, FluidCollisionMode, boolean)
     */
    @Nullable
    public RayTraceResult rayTraceBlocks(@NotNull Location start, @NotNull Vector direction, double maxDistance, @NotNull FluidCollisionMode fluidCollisionMode);

    /**
     * Performs a ray trace that checks for block collisions using the blocks'
     * precise collision shapes.
     * <p>
     * If collisions with passable blocks are ignored, fluid collisions are
     * ignored as well regardless of the fluid collision mode.
     * <p>
     * Portal blocks are only considered passable if the ray starts within
     * them. Apart from that collisions with portal blocks will be considered
     * even if collisions with passable blocks are otherwise ignored.
     * <p>
     * This may cause loading of chunks! Some implementations may impose
     * artificial restrictions on the maximum distance.
     *
     * @param start the start location
     * @param direction the ray direction
     * @param maxDistance the maximum distance
     * @param fluidCollisionMode the fluid collision mode
     * @param ignorePassableBlocks whether to ignore passable but collidable
     *     blocks (ex. tall grass, signs, fluids, ..)
     * @return the ray trace hit result, or <code>null</code> if there is no hit
     */
    @Nullable
    public RayTraceResult rayTraceBlocks(@NotNull Location start, @NotNull Vector direction, double maxDistance, @NotNull FluidCollisionMode fluidCollisionMode, boolean ignorePassableBlocks);

    /**
     * Performs a ray trace that checks for both block and entity collisions.
     * <p>
     * Block collisions use the blocks' precise collision shapes. The
     * <code>raySize</code> parameter is only taken into account for entity
     * collision checks.
     * <p>
     * If collisions with passable blocks are ignored, fluid collisions are
     * ignored as well regardless of the fluid collision mode.
     * <p>
     * Portal blocks are only considered passable if the ray starts within them.
     * Apart from that collisions with portal blocks will be considered even if
     * collisions with passable blocks are otherwise ignored.
     * <p>
     * This may cause loading of chunks! Some implementations may impose
     * artificial restrictions on the maximum distance.
     *
     * @param start the start location
     * @param direction the ray direction
     * @param maxDistance the maximum distance
     * @param fluidCollisionMode the fluid collision mode
     * @param ignorePassableBlocks whether to ignore passable but collidable
     *     blocks (ex. tall grass, signs, fluids, ..)
     * @param raySize entity bounding boxes will be uniformly expanded (or
     *     shrinked) by this value before doing collision checks
     * @param filter only entities that fulfill this predicate are considered,
     *     or <code>null</code> to consider all entities
     * @return the closest ray trace hit result with either a block or an
     *     entity, or <code>null</code> if there is no hit
     */
    @Nullable
    public RayTraceResult rayTrace(@NotNull Location start, @NotNull Vector direction, double maxDistance, @NotNull FluidCollisionMode fluidCollisionMode, boolean ignorePassableBlocks, double raySize, @Nullable Predicate<Entity> filter);

    /**
     * Gets the unique name of this world
     *
     * @return Name of this world
     */
    @NotNull
    public String getName();

    /**
     * Gets the Unique ID of this world
     *
     * @return Unique ID of this world.
     */
    @NotNull
    public UUID getUID();

    /**
     * Gets the default spawn {@link Location} of this world
     *
     * @return The spawn location of this world
     */
    @NotNull
    public Location getSpawnLocation();

    /**
     * Sets the spawn location of the world.
     * <br>
     * The location provided must be equal to this world.
     *
     * @param location The {@link Location} to set the spawn for this world at.
     * @return True if it was successfully set.
     */
    @NotNull
    public boolean setSpawnLocation(@NotNull Location location);

    /**
     * Sets the spawn location of the world
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return True if it was successfully set.
     */
    public boolean setSpawnLocation(int x, int y, int z);

    /**
     * Gets the relative in-game time of this world.
     * <p>
     * The relative time is analogous to hours * 1000
     *
     * @return The current relative time
     * @see #getFullTime() Returns an absolute time of this world
     */
    public long getTime();

    /**
     * Sets the relative in-game time on the server.
     * <p>
     * The relative time is analogous to hours * 1000
     * <p>
     * Note that setting the relative time below the current relative time
     * will actually move the clock forward a day. If you require to rewind
     * time, please see {@link #setFullTime(long)}
     *
     * @param time The new relative time to set the in-game time to (in
     *     hours*1000)
     * @see #setFullTime(long) Sets the absolute time of this world
     */
    public void setTime(long time);

    /**
     * Gets the full in-game time on this world
     *
     * @return The current absolute time
     * @see #getTime() Returns a relative time of this world
     */
    public long getFullTime();

    /**
     * Sets the in-game time on the server
     * <p>
     * Note that this sets the full time of the world, which may cause adverse
     * effects such as breaking redstone clocks and any scheduled events
     *
     * @param time The new absolute time to set this world to
     * @see #setTime(long) Sets the relative time of this world
     */
    public void setFullTime(long time);

    // Paper start

    /**
     * Check if it is currently daytime in this world
     *
     * @return True if it is daytime
     */
    public boolean isDayTime();
    // Paper end

    /**
     * Returns whether the world has an ongoing storm.
     *
     * @return Whether there is an ongoing storm
     */
    public boolean hasStorm();

    /**
     * Set whether there is a storm. A duration will be set for the new
     * current conditions.
     *
     * @param hasStorm Whether there is rain and snow
     */
    public void setStorm(boolean hasStorm);

    /**
     * Get the remaining time in ticks of the current conditions.
     *
     * @return Time in ticks
     */
    public int getWeatherDuration();

    /**
     * Set the remaining time in ticks of the current conditions.
     *
     * @param duration Time in ticks
     */
    public void setWeatherDuration(int duration);

    /**
     * Returns whether there is thunder.
     *
     * @return Whether there is thunder
     */
    public boolean isThundering();

    /**
     * Set whether it is thundering.
     *
     * @param thundering Whether it is thundering
     */
    public void setThundering(boolean thundering);

    /**
     * Get the thundering duration.
     *
     * @return Duration in ticks
     */
    public int getThunderDuration();

    /**
     * Set the thundering duration.
     *
     * @param duration Duration in ticks
     */
    public void setThunderDuration(int duration);

    /**
     * Creates explosion at given coordinates with given power
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param power The power of explosion, where 4F is TNT
     * @return false if explosion was canceled, otherwise true
     */
    public boolean createExplosion(double x, double y, double z, float power);

    /**
     * Creates explosion at given coordinates with given power and optionally
     * setting blocks on fire.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @return false if explosion was canceled, otherwise true
     */
    public boolean createExplosion(double x, double y, double z, float power, boolean setFire);

    /**
     * Creates explosion at given coordinates with given power and optionally
     * setting blocks on fire or breaking blocks.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @param breakBlocks Whether or not to have blocks be destroyed
     * @return false if explosion was canceled, otherwise true
     */
    public boolean createExplosion(double x, double y, double z, float power, boolean setFire, boolean breakBlocks);

    /**
     * Creates explosion at given coordinates with given power
     *
     * @param loc Location to blow up
     * @param power The power of explosion, where 4F is TNT
     * @return false if explosion was canceled, otherwise true
     */
    public boolean createExplosion(@NotNull Location loc, float power);

    /**
     * Creates explosion at given coordinates with given power and optionally
     * setting blocks on fire.
     *
     * @param loc Location to blow up
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @return false if explosion was canceled, otherwise true
     */
    public boolean createExplosion(@NotNull Location loc, float power, boolean setFire);

    // Paper start
    /**
     * Creates explosion at given location with given power and optionally
     * setting blocks on fire, with the specified entity as the source.
     *
     * @param source The source entity of the explosion
     * @param loc Location to blow up
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @param breakBlocks Whether or not to have blocks be destroyed
     * @return false if explosion was canceled, otherwise true
     */
    public boolean createExplosion(@Nullable Entity source, @NotNull Location loc, float power, boolean setFire, boolean breakBlocks);

    /**
     * Creates explosion at given location with given power and optionally
     * setting blocks on fire, with the specified entity as the source.
     *
     * Will destroy other blocks
     *
     * @param source The source entity of the explosion
     * @param loc Location to blow up
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @return false if explosion was canceled, otherwise true
     */
    public default boolean createExplosion(@Nullable Entity source, @NotNull Location loc, float power, boolean setFire) {
        return createExplosion(source, loc, power, setFire, true);
    }
    /**
     * Creates explosion at given location with given power, with the specified entity as the source.
     * Will set blocks on fire and destroy blocks.
     *
     * @param source The source entity of the explosion
     * @param loc Location to blow up
     * @param power The power of explosion, where 4F is TNT
     * @return false if explosion was canceled, otherwise true
     */
    public default boolean createExplosion(@Nullable Entity source, @NotNull Location loc, float power) {
        return createExplosion(source, loc, power, true, true);
    }
    /**
     * Creates explosion at given entities location with given power and optionally
     * setting blocks on fire, with the specified entity as the source.
     *
     * @param source The source entity of the explosion
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @param breakBlocks Whether or not to have blocks be destroyed
     * @return false if explosion was canceled, otherwise true
     */
    public default boolean createExplosion(@NotNull Entity source, float power, boolean setFire, boolean breakBlocks) {
        return createExplosion(source, source.getLocation(), power, setFire, breakBlocks);
    }
    /**
     * Creates explosion at given entities location with given power and optionally
     * setting blocks on fire, with the specified entity as the source.
     *
     * Will destroy blocks.
     *
     * @param source The source entity of the explosion
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @return false if explosion was canceled, otherwise true
     */
    public default boolean createExplosion(@NotNull Entity source, float power, boolean setFire) {
        return createExplosion(source, source.getLocation(), power, setFire, true);
    }

    /**
     * Creates explosion at given entities location with given power and optionally
     * setting blocks on fire, with the specified entity as the source.
     *
     * @param source The source entity of the explosion
     * @param power The power of explosion, where 4F is TNT
     * @return false if explosion was canceled, otherwise true
     */
    public default boolean createExplosion(@NotNull Entity source, float power) {
        return createExplosion(source, source.getLocation(), power, true, true);
    }

    /**
     * Creates explosion at given location with given power and optionally
     * setting blocks on fire or breaking blocks.
     *
     * @param loc Location to blow up
     * @param power The power of explosion, where 4F is TNT
     * @param setFire Whether or not to set blocks on fire
     * @param breakBlocks Whether or not to have blocks be destroyed
     * @return false if explosion was canceled, otherwise true
     */
    public default boolean createExplosion(@NotNull Location loc, float power, boolean setFire, boolean breakBlocks) {
        return createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, setFire, breakBlocks);
    }
    // Paper end

    /**
     * Gets the {@link Environment} type of this world
     *
     * @return This worlds Environment type
     */
    @NotNull
    public Environment getEnvironment();

    /**
     * Gets the Seed for this world.
     *
     * @return This worlds Seed
     */
    public long getSeed();

    /**
     * Gets the current PVP setting for this world.
     *
     * @return True if PVP is enabled
     */
    public boolean getPVP();

    /**
     * Sets the PVP setting for this world.
     *
     * @param pvp True/False whether PVP should be Enabled.
     */
    public void setPVP(boolean pvp);

    /**
     * Gets the chunk generator for this world
     *
     * @return ChunkGenerator associated with this world
     */
    @Nullable
    public ChunkGenerator getGenerator();

    /**
     * Saves world to disk
     */
    public void save();

    /**
     * Gets a list of all applied {@link BlockPopulator}s for this World
     *
     * @return List containing any or none BlockPopulators
     */
    @NotNull
    public List<BlockPopulator> getPopulators();

    /**
     * Spawn an entity of a specific class at the given {@link Location}
     *
     * @param location the {@link Location} to spawn the entity at
     * @param clazz the class of the {@link Entity} to spawn
     * @param <T> the class of the {@link Entity} to spawn
     * @return an instance of the spawned {@link Entity}
     * @throws IllegalArgumentException if either parameter is null or the
     *     {@link Entity} requested cannot be spawned
     */
    @NotNull
    public <T extends Entity> T spawn(@NotNull Location location, @NotNull Class<T> clazz) throws IllegalArgumentException;

    /**
     * Spawn an entity of a specific class at the given {@link Location}, with
     * the supplied function run before the entity is added to the world.
     * <br>
     * Note that when the function is run, the entity will not be actually in
     * the world. Any operation involving such as teleporting the entity is undefined
     * until after this function returns.
     *
     * @param location the {@link Location} to spawn the entity at
     * @param clazz the class of the {@link Entity} to spawn
     * @param function the function to be run before the entity is spawned.
     * @param <T> the class of the {@link Entity} to spawn
     * @return an instance of the spawned {@link Entity}
     * @throws IllegalArgumentException if either parameter is null or the
     *     {@link Entity} requested cannot be spawned
     */
    @NotNull
    public <T extends Entity> T spawn(@NotNull Location location, @NotNull Class<T> clazz, @Nullable Consumer<T> function) throws IllegalArgumentException;

    /**
     * Spawn a {@link FallingBlock} entity at the given {@link Location} of
     * the specified {@link Material}. The material dictates what is falling.
     * When the FallingBlock hits the ground, it will place that block.
     * <p>
     * The Material must be a block type, check with {@link Material#isBlock()
     * material.isBlock()}. The Material may not be air.
     *
     * @param location The {@link Location} to spawn the FallingBlock
     * @param data The block data
     * @return The spawned {@link FallingBlock} instance
     * @throws IllegalArgumentException if {@link Location} or {@link
     *     MaterialData} are null or {@link Material} of the {@link MaterialData} is not a block
     */
    @NotNull
    public FallingBlock spawnFallingBlock(@NotNull Location location, @NotNull MaterialData data) throws IllegalArgumentException;

    /**
     * Spawn a {@link FallingBlock} entity at the given {@link Location} of
     * the specified {@link Material}. The material dictates what is falling.
     * When the FallingBlock hits the ground, it will place that block.
     * <p>
     * The Material must be a block type, check with {@link Material#isBlock()
     * material.isBlock()}. The Material may not be air.
     *
     * @param location The {@link Location} to spawn the FallingBlock
     * @param data The block data
     * @return The spawned {@link FallingBlock} instance
     * @throws IllegalArgumentException if {@link Location} or {@link
     *     BlockData} are null
     */
    @NotNull
    public FallingBlock spawnFallingBlock(@NotNull Location location, @NotNull BlockData data) throws IllegalArgumentException;

    /**
     * Spawn a {@link FallingBlock} entity at the given {@link Location} of the
     * specified {@link Material}. The material dictates what is falling.
     * When the FallingBlock hits the ground, it will place that block.
     * <p>
     * The Material must be a block type, check with {@link Material#isBlock()
     * material.isBlock()}. The Material may not be air.
     *
     * @param location The {@link Location} to spawn the FallingBlock
     * @param material The block {@link Material} type
     * @param data The block data
     * @return The spawned {@link FallingBlock} instance
     * @throws IllegalArgumentException if {@link Location} or {@link
     *     Material} are null or {@link Material} is not a block
     * @deprecated Magic value
     */
    @Deprecated
    @NotNull
    public FallingBlock spawnFallingBlock(@NotNull Location location, @NotNull Material material, byte data) throws IllegalArgumentException;

    /**
     * Plays an effect to all players within a default radius around a given
     * location.
     *
     * @param location the {@link Location} around which players must be to
     *     hear the sound
     * @param effect the {@link Effect}
     * @param data a data bit needed for some effects
     */
    public void playEffect(@NotNull Location location, @NotNull Effect effect, int data);

    /**
     * Plays an effect to all players within a given radius around a location.
     *
     * @param location the {@link Location} around which players must be to
     *     hear the effect
     * @param effect the {@link Effect}
     * @param data a data bit needed for some effects
     * @param radius the radius around the location
     */
    public void playEffect(@NotNull Location location, @NotNull Effect effect, int data, int radius);

    /**
     * Plays an effect to all players within a default radius around a given
     * location.
     *
     * @param <T> data dependant on the type of effect
     * @param location the {@link Location} around which players must be to
     *     hear the sound
     * @param effect the {@link Effect}
     * @param data a data bit needed for some effects
     */
    public <T> void playEffect(@NotNull Location location, @NotNull Effect effect, @Nullable T data);

    /**
     * Plays an effect to all players within a given radius around a location.
     *
     * @param <T> data dependant on the type of effect
     * @param location the {@link Location} around which players must be to
     *     hear the effect
     * @param effect the {@link Effect}
     * @param data a data bit needed for some effects
     * @param radius the radius around the location
     */
    public <T> void playEffect(@NotNull Location location, @NotNull Effect effect, @Nullable T data, int radius);

    /**
     * Get empty chunk snapshot (equivalent to all air blocks), optionally
     * including valid biome data. Used for representing an ungenerated chunk,
     * or for fetching only biome data without loading a chunk.
     *
     * @param x - chunk x coordinate
     * @param z - chunk z coordinate
     * @param includeBiome - if true, snapshot includes per-coordinate biome
     *     type
     * @param includeBiomeTemp - if true, snapshot includes per-coordinate
     *     raw biome temperature
     * @return The empty snapshot.
     */
    @NotNull
    public ChunkSnapshot getEmptyChunkSnapshot(int x, int z, boolean includeBiome, boolean includeBiomeTemp);

    /**
     * Sets the spawn flags for this.
     *
     * @param allowMonsters - if true, monsters are allowed to spawn in this
     *     world.
     * @param allowAnimals - if true, animals are allowed to spawn in this
     *     world.
     */
    public void setSpawnFlags(boolean allowMonsters, boolean allowAnimals);

    /**
     * Gets whether animals can spawn in this world.
     *
     * @return whether animals can spawn in this world.
     */
    public boolean getAllowAnimals();

    /**
     * Gets whether monsters can spawn in this world.
     *
     * @return whether monsters can spawn in this world.
     */
    public boolean getAllowMonsters();

    /**
     * Gets the biome for the given block coordinates.
     *
     * @param x X coordinate of the block
     * @param z Z coordinate of the block
     * @return Biome of the requested block
     */
    @NotNull
    Biome getBiome(int x, int z);

    /**
     * Sets the biome for the given block coordinates
     *
     * @param x X coordinate of the block
     * @param z Z coordinate of the block
     * @param bio new Biome type for this block
     */
    void setBiome(int x, int z, @NotNull Biome bio);

    /**
     * Gets the temperature for the given block coordinates.
     * <p>
     * It is safe to run this method when the block does not exist, it will
     * not create the block.
     * <p>
     * This method will return the raw temperature without adjusting for block
     * height effects.
     *
     * @param x X coordinate of the block
     * @param z Z coordinate of the block
     * @return Temperature of the requested block
     */
    public double getTemperature(int x, int z);

    /**
     * Gets the humidity for the given block coordinates.
     * <p>
     * It is safe to run this method when the block does not exist, it will
     * not create the block.
     *
     * @param x X coordinate of the block
     * @param z Z coordinate of the block
     * @return Humidity of the requested block
     */
    public double getHumidity(int x, int z);

    /**
     * Gets the maximum height of this world.
     * <p>
     * If the max height is 100, there are only blocks from y=0 to y=99.
     *
     * @return Maximum height of the world
     */
    public int getMaxHeight();

    /**
     * Gets the sea level for this world.
     * <p>
     * This is often half of {@link #getMaxHeight()}
     *
     * @return Sea level
     */
    public int getSeaLevel();

    /**
     * Gets whether the world's spawn area should be kept loaded into memory
     * or not.
     *
     * @return true if the world's spawn area will be kept loaded into memory.
     */
    public boolean getKeepSpawnInMemory();

    /**
     * Sets whether the world's spawn area should be kept loaded into memory
     * or not.
     *
     * @param keepLoaded if true then the world's spawn area will be kept
     *     loaded into memory.
     */
    public void setKeepSpawnInMemory(boolean keepLoaded);

    /**
     * Gets whether or not the world will automatically save
     *
     * @return true if the world will automatically save, otherwise false
     */
    public boolean isAutoSave();

    /**
     * Sets whether or not the world will automatically save
     *
     * @param value true if the world should automatically save, otherwise
     *     false
     */
    public void setAutoSave(boolean value);

    /**
     * Sets the Difficulty of the world.
     *
     * @param difficulty the new difficulty you want to set the world to
     */
    public void setDifficulty(@NotNull Difficulty difficulty);

    /**
     * Gets the Difficulty of the world.
     *
     * @return The difficulty of the world.
     */
    @NotNull
    public Difficulty getDifficulty();

    /**
     * Gets the folder of this world on disk.
     *
     * @return The folder of this world.
     */
    @NotNull
    public File getWorldFolder();

    /**
     * Gets the type of this world.
     *
     * @return Type of this world.
     */
    @Nullable
    public WorldType getWorldType();

    /**
     * Gets whether or not structures are being generated.
     *
     * @return True if structures are being generated.
     */
    public boolean canGenerateStructures();

    /**
     * Gets the world's ticks per animal spawns value
     * <p>
     * This value determines how many ticks there are between attempts to
     * spawn animals.
     * <p>
     * <b>Example Usage:</b>
     * <ul>
     * <li>A value of 1 will mean the server will attempt to spawn animals in
     *     this world every tick.
     * <li>A value of 400 will mean the server will attempt to spawn animals
     *     in this world every 400th tick.
     * <li>A value below 0 will be reset back to Minecraft's default.
     * </ul>
     * <p>
     * <b>Note:</b>
     * If set to 0, animal spawning will be disabled for this world. We
     * recommend using {@link #setSpawnFlags(boolean, boolean)} to control
     * this instead.
     * <p>
     * Minecraft default: 400.
     *
     * @return The world's ticks per animal spawns value
     */
    public long getTicksPerAnimalSpawns();

    /**
     * Sets the world's ticks per animal spawns value
     * <p>
     * This value determines how many ticks there are between attempts to
     * spawn animals.
     * <p>
     * <b>Example Usage:</b>
     * <ul>
     * <li>A value of 1 will mean the server will attempt to spawn animals in
     *     this world every tick.
     * <li>A value of 400 will mean the server will attempt to spawn animals
     *     in this world every 400th tick.
     * <li>A value below 0 will be reset back to Minecraft's default.
     * </ul>
     * <p>
     * <b>Note:</b>
     * If set to 0, animal spawning will be disabled for this world. We
     * recommend using {@link #setSpawnFlags(boolean, boolean)} to control
     * this instead.
     * <p>
     * Minecraft default: 400.
     *
     * @param ticksPerAnimalSpawns the ticks per animal spawns value you want
     *     to set the world to
     */
    public void setTicksPerAnimalSpawns(int ticksPerAnimalSpawns);

    /**
     * Gets the world's ticks per monster spawns value
     * <p>
     * This value determines how many ticks there are between attempts to
     * spawn monsters.
     * <p>
     * <b>Example Usage:</b>
     * <ul>
     * <li>A value of 1 will mean the server will attempt to spawn monsters in
     *     this world every tick.
     * <li>A value of 400 will mean the server will attempt to spawn monsters
     *     in this world every 400th tick.
     * <li>A value below 0 will be reset back to Minecraft's default.
     * </ul>
     * <p>
     * <b>Note:</b>
     * If set to 0, monsters spawning will be disabled for this world. We
     * recommend using {@link #setSpawnFlags(boolean, boolean)} to control
     * this instead.
     * <p>
     * Minecraft default: 1.
     *
     * @return The world's ticks per monster spawns value
     */
    public long getTicksPerMonsterSpawns();

    /**
     * Sets the world's ticks per monster spawns value
     * <p>
     * This value determines how many ticks there are between attempts to
     * spawn monsters.
     * <p>
     * <b>Example Usage:</b>
     * <ul>
     * <li>A value of 1 will mean the server will attempt to spawn monsters in
     *     this world on every tick.
     * <li>A value of 400 will mean the server will attempt to spawn monsters
     *     in this world every 400th tick.
     * <li>A value below 0 will be reset back to Minecraft's default.
     * </ul>
     * <p>
     * <b>Note:</b>
     * If set to 0, monsters spawning will be disabled for this world. We
     * recommend using {@link #setSpawnFlags(boolean, boolean)} to control
     * this instead.
     * <p>
     * Minecraft default: 1.
     *
     * @param ticksPerMonsterSpawns the ticks per monster spawns value you
     *     want to set the world to
     */
    public void setTicksPerMonsterSpawns(int ticksPerMonsterSpawns);

    /**
     * Gets limit for number of monsters that can spawn in a chunk in this
     * world
     *
     * @return The monster spawn limit
     */
    int getMonsterSpawnLimit();

    /**
     * Sets the limit for number of monsters that can spawn in a chunk in this
     * world
     * <p>
     * <b>Note:</b> If set to a negative number the world will use the
     * server-wide spawn limit instead.
     *
     * @param limit the new mob limit
     */
    void setMonsterSpawnLimit(int limit);

    /**
     * Gets the limit for number of animals that can spawn in a chunk in this
     * world
     *
     * @return The animal spawn limit
     */
    int getAnimalSpawnLimit();

    /**
     * Sets the limit for number of animals that can spawn in a chunk in this
     * world
     * <p>
     * <b>Note:</b> If set to a negative number the world will use the
     * server-wide spawn limit instead.
     *
     * @param limit the new mob limit
     */
    void setAnimalSpawnLimit(int limit);

    /**
     * Gets the limit for number of water animals that can spawn in a chunk in
     * this world
     *
     * @return The water animal spawn limit
     */
    int getWaterAnimalSpawnLimit();

    /**
     * Sets the limit for number of water animals that can spawn in a chunk in
     * this world
     * <p>
     * <b>Note:</b> If set to a negative number the world will use the
     * server-wide spawn limit instead.
     *
     * @param limit the new mob limit
     */
    void setWaterAnimalSpawnLimit(int limit);

    /**
     * Gets the limit for number of ambient mobs that can spawn in a chunk in
     * this world
     *
     * @return The ambient spawn limit
     */
    int getAmbientSpawnLimit();

    /**
     * Sets the limit for number of ambient mobs that can spawn in a chunk in
     * this world
     * <p>
     * <b>Note:</b> If set to a negative number the world will use the
     * server-wide spawn limit instead.
     *
     * @param limit the new mob limit
     */
    void setAmbientSpawnLimit(int limit);

    /**
     * Play a Sound at the provided Location in the World
     * <p>
     * This function will fail silently if Location or Sound are null.
     *
     * @param location The location to play the sound
     * @param sound The sound to play
     * @param volume The volume of the sound
     * @param pitch The pitch of the sound
     */
    void playSound(@NotNull Location location, @NotNull Sound sound, float volume, float pitch);

    /**
     * Play a Sound at the provided Location in the World.
     * <p>
     * This function will fail silently if Location or Sound are null. No
     * sound will be heard by the players if their clients do not have the
     * respective sound for the value passed.
     *
     * @param location the location to play the sound
     * @param sound the internal sound name to play
     * @param volume the volume of the sound
     * @param pitch the pitch of the sound
     */
    void playSound(@NotNull Location location, @NotNull String sound, float volume, float pitch);

    /**
     * Play a Sound at the provided Location in the World.
     * <p>
     * This function will fail silently if Location or Sound are null.
     *
     * @param location The location to play the sound
     * @param sound The sound to play
     * @param category the category of the sound
     * @param volume The volume of the sound
     * @param pitch The pitch of the sound
     */
    void playSound(@NotNull Location location, @NotNull Sound sound, @NotNull SoundCategory category, float volume, float pitch);

    /**
     * Play a Sound at the provided Location in the World.
     * <p>
     * This function will fail silently if Location or Sound are null. No sound
     * will be heard by the players if their clients do not have the respective
     * sound for the value passed.
     *
     * @param location the location to play the sound
     * @param sound the internal sound name to play
     * @param category the category of the sound
     * @param volume the volume of the sound
     * @param pitch the pitch of the sound
     */
    void playSound(@NotNull Location location, @NotNull String sound, @NotNull SoundCategory category, float volume, float pitch);

    /**
     * Get an array containing the names of all the {@link GameRule}s.
     *
     * @return An array of {@link GameRule} names.
     */
    @NotNull
    public String[] getGameRules();

    /**
     * Gets the current state of the specified rule
     * <p>
     * Will return null if rule passed is null
     *
     * @param rule Rule to look up value of
     * @return String value of rule
     * @deprecated use {@link #getGameRuleValue(GameRule)} instead
     */
    @Deprecated
    @Contract("null -> null; !null -> !null")
    @Nullable
    public String getGameRuleValue(@Nullable String rule);

    /**
     * Set the specified gamerule to specified value.
     * <p>
     * The rule may attempt to validate the value passed, will return true if
     * value was set.
     * <p>
     * If rule is null, the function will return false.
     *
     * @param rule Rule to set
     * @param value Value to set rule to
     * @return True if rule was set
     * @deprecated use {@link #setGameRule(GameRule, Object)} instead.
     */
    @Deprecated
    public boolean setGameRuleValue(@NotNull String rule, @NotNull String value);

    /**
     * Checks if string is a valid game rule
     *
     * @param rule Rule to check
     * @return True if rule exists
     */
    public boolean isGameRule(@NotNull String rule);

    /**
     * Get the current value for a given {@link GameRule}.
     *
     * @param rule the GameRule to check
     * @param <T> the GameRule's type
     * @return the current value
     */
    @Nullable
    public <T> T getGameRuleValue(@NotNull GameRule<T> rule);

    /**
     * Get the default value for a given {@link GameRule}. This value is not
     * guaranteed to match the current value.
     *
     * @param rule the rule to return a default value for
     * @param <T> the type of GameRule
     * @return the default value
     */
    @Nullable
    public <T> T getGameRuleDefault(@NotNull GameRule<T> rule);

    /**
     * Set the given {@link GameRule}'s new value.
     *
     * @param rule the GameRule to update
     * @param newValue the new value
     * @param <T> the value type of the GameRule
     * @return true if the value was successfully set
     */
    public <T> boolean setGameRule(@NotNull GameRule<T> rule, @NotNull T newValue);

    /**
     * Gets the world border for this world.
     *
     * @return The world border for this world.
     */
    @NotNull
    public WorldBorder getWorldBorder();

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location.
     *
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count the number of particles
     */
    public void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location.
     *
     * @param particle the particle to spawn
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     */
    public void spawnParticle(@NotNull Particle particle, double x, double y, double z, int count);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location.
     *
     * @param <T> type of particle data (see {@link Particle#getDataType()}
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count the number of particles
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     * @param <T> Type
     */
    public <T> void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count, @Nullable T data);


    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location.
     *
     * @param <T> type of particle data (see {@link Particle#getDataType()}
     * @param particle the particle to spawn
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     * @param <T> Type
     */
    public <T> void spawnParticle(@NotNull Particle particle, double x, double y, double z, int count, @Nullable T data);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     */
    public void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count, double offsetX, double offsetY, double offsetZ);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     */
    public void spawnParticle(@NotNull Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param <T> type of particle data (see {@link Particle#getDataType()}
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     * @param <T> Type
     */
    public <T> void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count, double offsetX, double offsetY, double offsetZ, @Nullable T data);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param <T> type of particle data (see {@link Particle#getDataType()}
     * @param particle the particle to spawn
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     * @param <T> Type
     */
    public <T> void spawnParticle(@NotNull Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, @Nullable T data);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param extra the extra data for this particle, depends on the
     *              particle used (normally speed)
     */
    public void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count, double offsetX, double offsetY, double offsetZ, double extra);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param extra the extra data for this particle, depends on the
     *              particle used (normally speed)
     */
    public void spawnParticle(@NotNull Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param <T> type of particle data (see {@link Particle#getDataType()}
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param extra the extra data for this particle, depends on the
     *              particle used (normally speed)
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     * @param <T> Type
     */
    public <T> void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable T data);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param <T> type of particle data (see {@link Particle#getDataType()}
     * @param particle the particle to spawn
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param extra the extra data for this particle, depends on the
     *              particle used (normally speed)
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     * @param <T> Type
     */
    public default <T> void spawnParticle(@NotNull Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable T data) { spawnParticle(particle, null, null, x, y, z, count, offsetX, offsetY, offsetZ, extra, data, true); }// Paper start - Expand Particle API
    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param receivers List of players to receive the particles, or null for all in world
     * @param source Source of the particles to be used in visibility checks, or null if no player source
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param extra the extra data for this particle, depends on the
     *              particle used (normally speed)
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     * @param <T> Type
     */
    public default <T> void spawnParticle(@NotNull Particle particle, @Nullable List<Player> receivers, @NotNull Player source, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable T data) { spawnParticle(particle, receivers, source, x, y, z, count, offsetX, offsetY, offsetZ, extra, data, true); }
    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param particle the particle to spawn
     * @param receivers List of players to receive the particles, or null for all in world
     * @param source Source of the particles to be used in visibility checks, or null if no player source
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param extra the extra data for this particle, depends on the
     *              particle used (normally speed)
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     * @param <T> Type
     * @param force allows the particle to be seen further away from the player
     *              and shows to players using any vanilla client particle settings
     */
    public <T> void spawnParticle(@NotNull Particle particle, @Nullable List<Player> receivers, @Nullable Player source, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable T data, boolean force);
    // Paper end


    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param <T> type of particle data (see {@link Particle#getDataType()}
     * @param particle the particle to spawn
     * @param location the location to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param extra the extra data for this particle, depends on the
     *              particle used (normally speed)
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     * @param force whether to send the particle to players within an extended
     *              range and encourage their client to render it regardless of
     *              settings
     * @param <T> Particle data type
     */
    public <T> void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable T data, boolean force);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param <T> type of particle data (see {@link Particle#getDataType()}
     * @param particle the particle to spawn
     * @param x the position on the x axis to spawn at
     * @param y the position on the y axis to spawn at
     * @param z the position on the z axis to spawn at
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param extra the extra data for this particle, depends on the
     *              particle used (normally speed)
     * @param data the data to use for the particle or null,
     *             the type of this depends on {@link Particle#getDataType()}
     * @param force whether to send the particle to players within an extended
     *              range and encourage their client to render it regardless of
     *              settings
     * @param <T> Particle data type
     */
    public <T> void spawnParticle(@NotNull Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable T data, boolean force);

    /**
     * Find the closest nearby structure of a given {@link StructureType}.
     * Finding unexplored structures can, and will, block if the world is
     * looking in chunks that gave not generated yet. This can lead to the world
     * temporarily freezing while locating an unexplored structure.
     * <p>
     * The {@code radius} is not a rigid square radius. Each structure may alter
     * how many chunks to check for each iteration. Do not assume that only a
     * radius x radius chunk area will be checked. For example,
     * {@link StructureType#WOODLAND_MANSION} can potentially check up to 20,000
     * blocks away (or more) regardless of the radius used.
     * <p>
     * This will <i>not</i> load or generate chunks. This can also lead to
     * instances where the server can hang if you are only looking for
     * unexplored structures. This is because it will keep looking further and
     * further out in order to find the structure.
     *
     * @param origin where to start looking for a structure
     * @param structureType the type of structure to find
     * @param radius the radius, in chunks, around which to search
     * @param findUnexplored true to only find unexplored structures
     * @return the closest {@link Location}, or null if no structure of the
     * specified type exists.
     */
    @Nullable
    public Location locateNearestStructure(@NotNull Location origin, @NotNull StructureType structureType, int radius, boolean findUnexplored);

    // Spigot start
    public class Spigot
    {

        /**
         * Strikes lightning at the given {@link Location} and possibly without sound
         *
         * @param loc The location to strike lightning
         * @param isSilent Whether this strike makes no sound
         * @return The lightning entity.
         */
        @NotNull
        public LightningStrike strikeLightning(@NotNull Location loc, boolean isSilent)
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        /**
         * Strikes lightning at the given {@link Location} without doing damage and possibly without sound
         *
         * @param loc The location to strike lightning
         * @param isSilent Whether this strike makes no sound
         * @return The lightning entity.
         */
        @NotNull
        public LightningStrike strikeLightningEffect(@NotNull Location loc, boolean isSilent)
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }
    }

    @NotNull
    Spigot spigot();
    // Spigot end

    /**
     * Represents various map environment types that a world may be
     */
    public enum Environment {

        /**
         * Represents the "normal"/"surface world" map
         */
        NORMAL(0),
        /**
         * Represents a nether based map ("hell")
         */
        NETHER(-1),
        /**
         * Represents the "end" map
         */
        THE_END(1);

        private final int id;
        private static final Map<Integer, Environment> lookup = new HashMap<Integer, Environment>();

        private Environment(int id) {
            this.id = id;
        }

        /**
         * Gets the dimension ID of this environment
         *
         * @return dimension ID
         * @deprecated Magic value
         */
        @Deprecated
        public int getId() {
            return id;
        }

        /**
         * Get an environment by ID
         *
         * @param id The ID of the environment
         * @return The environment
         * @deprecated Magic value
         */
        @Deprecated
        @Nullable
        public static Environment getEnvironment(int id) {
            return lookup.get(id);
        }

        static {
            for (Environment env : values()) {
                lookup.put(env.getId(), env);
            }
        }
    }
}
