package net.cocoonmc.runtime.forge.mixin;

import net.cocoonmc.runtime.forge.helper.ItemHelper;
import net.cocoonmc.runtime.forge.helper.ItemMixinHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FriendlyByteBuf.class)
public abstract class FriendlyByteBufMixin {

    @Inject(method = "readItem", at = @At("RETURN"), cancellable = true)
    public void cocoon$readItem(CallbackInfoReturnable<ItemStack> cir) {
        if (ItemHelper.isEnableRedirect()) {
            ItemMixinHelper.readItem(cir);
        }
    }

    @ModifyVariable(method = "writeItem", at = @At("HEAD"), argsOnly = true)
    public ItemStack cocoon$writeItem(ItemStack itemStack) {
        if (ItemHelper.isEnableRedirect()) {
            return ItemMixinHelper.writeItem(itemStack);
        }
        return itemStack;
    }

    @ModifyVariable(method = "writeItemStack", at = @At("HEAD"), argsOnly = true, remap = false)
    public ItemStack cocoon$writeItemStack(ItemStack originItemStack, ItemStack itemStack, boolean flag) {
        if (ItemHelper.isEnableRedirect()) {
            return ItemMixinHelper.writeItem(itemStack);
        }
        return itemStack;
    }
}
