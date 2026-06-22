package de.shiewk.viewserverresources.event;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ChatAnnouncer implements ClientTickEvents.EndTick {

    private static final ObjectArrayList<Component> queue = new ObjectArrayList<>();
    @Override
    public void onEndTick(Minecraft client) {
        if (client.player != null && !queue.isEmpty()){
            client.player.sendSystemMessage(queue.removeFirst());
        }
    }

    public static void announce(Component text){
        queue.add(text);
    }
}
