package de.shiewk.viewserverresources.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(targets = "net/minecraft/client/multiplayer/ClientCommonPacketListenerImpl$PackConfirmScreen")
public interface AccessorConfirmServerResourcePackScreen {

    @Accessor(value = "requests")
    List<?> getRequests();
}
