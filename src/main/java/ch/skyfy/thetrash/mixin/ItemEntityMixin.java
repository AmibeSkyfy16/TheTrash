package ch.skyfy.thetrash.mixin;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.nbt.visitor.NbtElementVisitor;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    @Shadow
    private int itemAge;
    @Shadow
    private int pickupDelay;
    @Shadow
    @Nullable
    private UUID owner;
    @Shadow
    @Final
    private static TrackedData<ItemStack> STACK;

    @Shadow
    public abstract ItemStack getStack();

    @Shadow
    protected abstract void initDataTracker();

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "onPlayerCollision(Lnet/minecraft/entity/player/PlayerEntity;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;getStack()Lnet/minecraft/item/ItemStack;", shift = At.Shift.AFTER),
            cancellable = true
    )
    public void onPlayerCollision(PlayerEntity player, CallbackInfo callbackInfo) {


        if(0 == 0)return;

        var itemEntity = (ItemEntity) (Object) this;
        var stack = itemEntity.getStack();

        for (int slot = 0; slot < player.getInventory().size(); slot++) {
            var slotItemStack = player.getInventory().getStack(slot);
            if (slotItemStack.getTranslationKey().contains("shulker")) {
                if (slotItemStack.getItem() instanceof BlockItem blockItem) {
                    if (blockItem.getBlock() instanceof ShulkerBoxBlock shulkerBoxBlock) {
                        if (slotItemStack.getNbt() != null) {
                            var items = DefaultedList.ofSize(27, ItemStack.EMPTY);
                            var blockEntityTag = slotItemStack.getNbt().getCompound("BlockEntityTag");
                            Inventories.readNbt(blockEntityTag, items);
                            // if the shulker contains at least once the item that picked up we insert it, then cancel and remove the item on the ground
                            if(items.stream().anyMatch(itemStack1 -> itemStack1.getTranslationKey().equalsIgnoreCase(stack.getTranslationKey()))){
                                var shouldBreak = false;
                                for (int i = 0; i < items.size(); i++) {
                                    var s = items.get(i);
                                    if(s.getTranslationKey().equalsIgnoreCase(stack.getTranslationKey())){
                                        if(stack.getCount() + s.getCount() <= 64){
                                            stack.setCount(stack.getCount() + s.getCount());
                                            items.set(i, stack);
                                            shouldBreak = true;
                                        }
                                    }else if(s == ItemStack.EMPTY){
                                        items.set(i, stack);
                                        shouldBreak = true;
                                    }
                                    if(shouldBreak){
                                        Inventories.writeNbt(blockEntityTag, items, true);
                                        break;
                                    }
                                }
                                itemEntity.remove(RemovalReason.DISCARDED);
                                callbackInfo.cancel();
                            }
                        }
                    }
                }
            }
        }

//        var result = player.getInventory().insertStack(stack);
//        if(result){
//            player.getInventory().removeOne(stack);
//        }else{
//            System.out.println("inventory is full");
//        }

//        callbackInfo.cancel();

    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (0 == 0) return super.interact(player, hand);
        var itemEntity = (ItemEntity) (Object) this;
        if (!this.world.isClient) {
            ItemStack itemStack = this.getStack();
            Item item = itemStack.getItem();
            int i = itemStack.getCount();
            System.out.println("pickup: " + itemStack.getTranslationKey());
            if (this.pickupDelay == 0 && (this.owner == null || this.owner.equals(player.getUuid())) && player.getInventory().insertStack(itemStack)) {
                player.sendPickup(this, i);
                if (itemStack.isEmpty()) {
                    this.discard();
                    itemStack.setCount(i);
                }

                player.increaseStat(Stats.PICKED_UP.getOrCreateStat(item), i);
                player.triggerItemPickedUpByEntityCriteria(itemEntity);
            }

        }
        return ActionResult.SUCCESS;
    }
}
