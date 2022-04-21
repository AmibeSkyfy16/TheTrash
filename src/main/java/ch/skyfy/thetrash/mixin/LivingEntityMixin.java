package ch.skyfy.thetrash.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.EntityPredicates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "sendPickup", at = @At("HEAD"))
    public void sendPickup(Entity item, int count, CallbackInfo info){

        if(0 == 0)return;

        var player = (((LivingEntity) (Object) this));

        if(!(player instanceof PlayerEntity)){
            System.out.println("not a player entity");
            return;
        }

        System.out.println("count: " + count);
        System.out.println("item.getDisplayName(): " + item.getDisplayName());
        System.out.println("item.getDisplayName(): " + item.getDisplayName());
        System.out.println("item.getName().asString(): " + item.getName().asString());
        System.out.println("item.getClass().getName(): " + item.getClass().getName());
        System.out.println("item.getClass().getCanonicalName(): " + item.getClass().getCanonicalName());

        if(item instanceof ItemEntity itemEntity){
            if(itemEntity.getPickBlockStack() == null){
                System.out.println("null");
            }else{
                System.out.println("getPickBlockStack().getTranslationKey(): " + itemEntity.getPickBlockStack().getTranslationKey());
                System.out.println("itemEntity.getPickBlockStack().getName().asString(): " + itemEntity.getPickBlockStack().getName().asString());
            }
            var stack = itemEntity.getStack();
            System.out.println("stack.getName().asString(): " + stack.getName().asString());
            System.out.println("stack.getTranslationKey(): " + stack.getTranslationKey());
        }

    }




}
