package ch.skyfy.thetrash.mixin;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class ItemStackPickupMixin {

    @Inject(method = "insertStack(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void onItemPickup(ItemStack pickedUpStack, CallbackInfoReturnable<Boolean> cir) {
        var playerInventory = (PlayerInventory) (Object) this;

        playerInventory.main.stream()
                .filter(itemStack -> itemStack.getTranslationKey().contains("shulker_box") && itemStack.getName().asString().contains("TheTrash"))
                .findFirst()
                .ifPresent(shulkerItemStack -> {
                    System.out.println("shulker found ");
                    if (shulkerItemStack.getNbt() != null) {
                        var items = DefaultedList.ofSize(27, ItemStack.EMPTY);
                        var blockEntityTag = shulkerItemStack.getNbt().getCompound("BlockEntityTag");
                        Inventories.readNbt(blockEntityTag, items);

                        // if the shulker contains at least once the item that picked up we insert it, then cancel and remove the item on the ground
                        if (items.stream().anyMatch(itemStack1 -> itemStack1.getTranslationKey().equals(pickedUpStack.getTranslationKey()))) {
                            System.out.println("item found: " + pickedUpStack.getTranslationKey());

                            var shouldBreak = false;
                            for (byte i = 0; i < items.size(); i++) {
                                var s = items.get(i);
                                if(s.getTranslationKey().equalsIgnoreCase(pickedUpStack.getTranslationKey())){
                                    if(pickedUpStack.getCount() + s.getCount() <= 64){
                                        pickedUpStack.setCount(pickedUpStack.getCount() + s.getCount());
                                        items.set(i, pickedUpStack);
                                        shouldBreak = true;
                                    }
                                }else if(s == ItemStack.EMPTY){
                                    items.set(i, pickedUpStack);
                                    shouldBreak = true;
                                }
                                if(shouldBreak){
                                    Inventories.writeNbt(blockEntityTag, items, true);
                                    pickedUpStack.setCount(0);
                                    cir.setReturnValue(false);
                                    cir.cancel();
                                    break;
                                }
                            }
                        }
                    }
                });


    }

}
