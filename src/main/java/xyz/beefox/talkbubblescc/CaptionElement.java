package xyz.beefox.talkbubblescc;

import java.util.UUID;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.elements.MarkerElement;
import eu.pb4.polymer.virtualentity.api.elements.TextDisplayElement;
import net.minecraft.entity.decoration.DisplayEntity.BillboardMode;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class CaptionElement extends ElementHolder {

    private TextDisplayElement caption = new TextDisplayElement();
    private MarkerElement marker = new MarkerElement();
    private Integer age = 0;
    private Integer maxAge = 300;

    public UUID playerID;

    public CaptionElement(ServerPlayerEntity player, String message){

        this.playerID = player.getUuid();

		caption.setText(Text.literal(message));
		caption.setOffset(new Vec3d(0f, 2.35f, 0f));
		caption.setBillboardMode(BillboardMode.CENTER);
        caption.setViewRange(40);
		this.addElement(caption);
		
        VirtualEntityUtils.createRidePacket(marker.getEntityId(), caption.getEntityIds());
		EntityAttachment.ofTicking(this, player);
    }	

    @Override
    public void onTick(){
        super.onTick();
        if(this.age > this.maxAge){
            TalkBubblesCC.removeCaption(playerID);
        }
        age++;
    }
    
}

