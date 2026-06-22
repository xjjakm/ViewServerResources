package de.shiewk.viewserverresources.mixin;

import de.shiewk.viewserverresources.client.ViewServerResourcesClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URL;
import java.util.UUID;

import static de.shiewk.viewserverresources.ViewServerResourcesMod.LOGGER;

@Mixin(value = ClientCommonPacketListenerImpl.class, priority = 1000)
public abstract class MixinClientCommonNetworkHandler {

    @Shadow @Final protected Minecraft minecraft;

    @Inject(at = @At("HEAD"), method = "handleResourcePackPush", cancellable = true, require = 0)
    public void onResourcePackPush(ClientboundResourcePackPushPacket packet, CallbackInfo ci){
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Received resource pack push: {}", packet.url());
        
        URL url = parseResourcePackUrl(packet);
        if (url == null) {
            return;
        }
        
        if (ViewServerResourcesClient.allowedURL(url)){
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("URL is whitelisted, auto-downloading");
            UUID packId = packet.id();
            String hash = packet.hash();
            this.minecraft.getDownloadedPackSource().pushPack(packId, url, hash);
            ci.cancel();
        }
    }
    
    private URL parseResourcePackUrl(ClientboundResourcePackPushPacket packet) {
        try {
            return new URL(packet.url());
        } catch (java.net.MalformedURLException e) {
            LOGGER.warn("Invalid resource pack URL: {}", packet.url());
            return null;
        }
    }
}
