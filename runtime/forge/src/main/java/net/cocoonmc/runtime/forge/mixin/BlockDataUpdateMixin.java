package net.cocoonmc.runtime.forge.mixin;

import com.mojang.datafixers.util.Pair;
import net.cocoonmc.runtime.forge.annotation.Available;
import net.cocoonmc.runtime.forge.api.IOriginalLevelChunk;
import net.cocoonmc.runtime.forge.helper.BlockHelper;
import net.cocoonmc.runtime.forge.helper.GameProfileHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Available("[1.18, )")
@Mixin(ClientboundBlockEntityDataPacket.class)
public abstract class BlockDataUpdateMixin {

    private BlockEntityType<?> aw$originalType;

    @Shadow
    @Final
    private BlockPos pos;

    @Shadow
    @Final
    @Mutable
    private BlockEntityType<?> type;

    @Shadow
    @Final
    @Nullable
    @Mutable
    private CompoundTag tag;

    @Inject(method = "handle(Lnet/minecraft/network/protocol/game/ClientGamePacketListener;)V", at = @At("HEAD"))
    private void aw$handleBlockEntityData(ClientGamePacketListener listener, CallbackInfo ci) {
        if (aw$originalType != null) {
            return;
        }
        aw$originalType = type;
        ClientLevel level = ((ClientPacketListener) listener).getLevel();
        ChunkAccess chunk = level.getChunk(pos);
        if (!(chunk instanceof IOriginalLevelChunk)) {
            return;
        }
        IOriginalLevelChunk originalChunk = (IOriginalLevelChunk) chunk;
        BlockState originalState = originalChunk.getOriginalBlockState(pos);
        Pair<BlockState, CompoundTag> pair = BlockHelper.getBlockFromTexture(GameProfileHelper.getTextureFromTag(tag));
        if (pair == null) {
            return;
        }
        BlockState newState = pair.getFirst();
        BlockState oldState = chunk.getBlockState(pos);
        // in the first call, we need save the origin block state.
        if (originalState == null) {
            originalChunk.setOriginalBlockState(pos, Blocks.PLAYER_HEAD.defaultBlockState());
        }
        // if set a new block, change to new block type.
        if (!oldState.equals(newState)) {
            chunk.setBlockState(pos, newState, false);
        }
        // change to last block entity type.
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null) {
            type = blockEntity.getType();
        }
        tag = pair.getSecond();
    }
}
