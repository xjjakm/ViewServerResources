package de.shiewk.viewserverresources.mixin;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.net.URL;
import java.util.UUID;

@Mixin(targets = "net/minecraft/client/multiplayer/ClientCommonPacketListenerImpl$PackConfirmScreen$PendingRequest")
public interface AccessorConfirmServerResourcePackScreenPack {

    @Accessor(value = "id")
    UUID getId();

    @Accessor(value = "url")
    URL getURL();

    @Accessor(value = "hash")
    String getHash();

}
