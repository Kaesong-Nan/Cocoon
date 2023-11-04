package net.cocoonmc.runtime.forge.mixin;

import net.cocoonmc.runtime.forge.api.Available;
import net.cocoonmc.runtime.forge.helper.ItemHelper;
import net.cocoonmc.runtime.forge.helper.PacketHelper;
import net.minecraft.network.Connection;
import net.minecraftforge.network.ICustomPacket;
import net.minecraftforge.network.NetworkHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Available("[1.18, )")
@Mixin(NetworkHooks.class)
public class NetworkMixin {

    @Inject(method = "handleClientLoginSuccess", at = @At("HEAD"), remap = false)
    private static void cocoon$updateConnectType(Connection manager, CallbackInfo cir) {
        ItemHelper.setEnableRedirect(NetworkHooks.isVanillaConnection(manager));
    }

    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true, remap = false)
    private static void cocoon$onCustomPayload(ICustomPacket<?> packet, Connection manager, CallbackInfoReturnable<Boolean> cir) {
        if (PacketHelper.test(packet.getName()) && packet.getInternalData() != null) {
            PacketHelper.handle(manager, packet.getInternalData());
            cir.setReturnValue(true);
        }
    }
}
