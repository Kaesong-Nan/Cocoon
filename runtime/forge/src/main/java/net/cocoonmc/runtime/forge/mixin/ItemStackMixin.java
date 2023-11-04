package net.cocoonmc.runtime.forge.mixin;

import net.cocoonmc.runtime.forge.helper.ItemHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @ModifyVariable(method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("HEAD"), argsOnly = true)
    private static CompoundTag cocoon$read(CompoundTag tag) {
        if (ItemHelper.isEnableRedirect()) {
            String targetId = ItemHelper.getId(tag, 0);
            if (targetId != null) {
                CompoundTag newTag = tag.copy();
                newTag.putString("id", targetId);
                return newTag;
            }
        }
        return tag;
    }

    @Inject(method = "save", at = @At("RETURN"))
    public void cocoon$save(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        if (ItemHelper.isEnableRedirect()) {
            CompoundTag itemTag = cir.getReturnValue();
            String targetId = itemTag.getString("id");
            if (ItemHelper.shouldRedirectId(targetId)) {
                String sourceId = ItemHelper.getId(tag, 1);
                if (sourceId == null) {
                    sourceId = ItemHelper.getMatchIdBySize(targetId);
                }
                itemTag.putString("id", sourceId);
                ItemHelper.setId(tag, sourceId, targetId);
            }
        }
    }
}
