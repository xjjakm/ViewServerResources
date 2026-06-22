package de.shiewk.viewserverresources.mixin;

import de.shiewk.viewserverresources.client.ViewServerResourcesClient;
import de.shiewk.viewserverresources.event.ChatAnnouncer;
import net.minecraft.client.resources.server.DownloadedPackSource;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.net.URL;
import java.util.UUID;

@Mixin(DownloadedPackSource.class)
public class MixinServerResourcePackLoader {

    @Inject(at = @At("HEAD"), method = "pushPack(Ljava/util/UUID;Ljava/net/URL;Ljava/lang/String;)V")
    public void onResourcePackPush(UUID id, URL url, String hash, CallbackInfo ci){
        if (ViewServerResourcesClient.isBroadcastDownloads()){
            ChatAnnouncer.announce(Component.translatable("gui.viewserverresources.downloading",
                            Component.literal(url.toString()))
                    .withColor(Color.ORANGE.getRGB())
            );
        }
    }
}
