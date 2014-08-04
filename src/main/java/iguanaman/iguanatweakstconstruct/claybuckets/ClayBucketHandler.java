package iguanaman.iguanatweakstconstruct.claybuckets;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mantle.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.smeltery.blocks.LiquidMetalFinite;

public class ClayBucketHandler {
    // milking cows
    @SubscribeEvent
    public void EntityInteract(EntityInteractEvent event)
    {
        if (event != null && event.target != null && event.target instanceof EntityCow)
        {
            ItemStack equipped = event.entityPlayer.getCurrentEquippedItem();
            if(equipped.getItem() != IguanaItems.clayBucketFired)
                return;

            EntityPlayer player = event.entityPlayer;

            if (equipped.stackSize-- == 1)
            {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(IguanaItems.clayBucketMilk));
            }
            else if (!player.inventory.addItemStackToInventory(new ItemStack(IguanaItems.clayBucketMilk)))
            {
                player.dropPlayerItemWithRandomChoice(new ItemStack(IguanaItems.clayBucketMilk, 1, 0), false);
            }
        }
    }

    // filling buckets with molten metal, same behaviour as regular buckets from TConstruct, but with clay buckets
    @SubscribeEvent
    public void bucketFill (FillBucketEvent event)
    {
        if (event.current.getItem() == IguanaItems.clayBucketFired && event.target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
        {
            int hitX = event.target.blockX;
            int hitY = event.target.blockY;
            int hitZ = event.target.blockZ;

            if (event.entityPlayer != null && !event.entityPlayer.canPlayerEdit(hitX, hitY, hitZ, event.target.sideHit, event.current))
            {
                return;
            }

            Block bID = event.world.getBlock(hitX, hitY, hitZ);

            // tinkers fluids
            for (int id = 0; id < TinkerSmeltery.fluidBlocks.length; id++)
            {
                if (bID == TinkerSmeltery.fluidBlocks[id])
                {
                    event.world.setBlockToAir(hitX, hitY, hitZ);
                    if (!event.entityPlayer.capabilities.isCreativeMode)
                    {
                        event.world.setBlockToAir(hitX, hitY, hitZ);

                        event.setResult(Event.Result.ALLOW);
                        event.result = new ItemStack(IguanaItems.clayBucketsTinkers, 1, id);
                        return;
                    }
                }
            }

            // water and lava
            if(bID.getMaterial() == Material.water)
            {
                event.setResult(Event.Result.ALLOW);
                event.result = new ItemStack(IguanaItems.clayBucketWater);
                event.world.setBlockToAir(hitX, hitY, hitZ);

                return;
            }
            if(bID.getMaterial() == Material.lava)
            {
                event.setResult(Event.Result.ALLOW);
                event.result = new ItemStack(IguanaItems.clayBucketLava);
                event.world.setBlockToAir(hitX, hitY, hitZ);

                return;
            }
        }
    }
}
