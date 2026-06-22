package de.shiewk.viewserverresources.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(targets = "net/minecraft/client/multiplayer/ClientCommonPacketListenerImpl$ConfirmServerResourcePackScreen")
public interface AccessorConfirmServerResourcePackScreen {

    @Accessor(value = "packs")
    List<?> getPacks();
    // ? type because the class is private

}
