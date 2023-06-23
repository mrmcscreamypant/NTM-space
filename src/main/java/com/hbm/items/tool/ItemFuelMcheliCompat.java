package com.hbm.items.tool;

import java.util.List;

import com.hbm.handler.pollution.PollutionHandler;
import com.hbm.handler.pollution.PollutionHandler.PollutionType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;
import com.hbm.lib.Library;
import com.hbm.main.MainRegistry;
import cpw.mods.fml.common.Loader;
import mcheli.aircraft.MCH_ItemFuel;
import mcheli.wrapper.W_Item;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCoal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.event.ForgeEventFactory;

public class ItemFuelMcheliCompat extends MCH_ItemFuel {
    public ItemFuelMcheliCompat(final int Durability) {
        super(Durability);
        this.setMaxDamage(Durability);
        this.setMaxStackSize(1);
        this.setNoRepair();
        this.setFull3D();
    }
    
    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (stack.getItem() == ModItems.mchtier1 && stack.isItemDamaged() && stack.getItemDamage() >= stack.getMaxDamage()) {
            stack.stackSize--; 

            if (stack.stackSize <= 0) {
                stack = null;
            } else {
                stack.setItemDamage(0);
            }

        }
        
        // Call the superclass onUpdate method to ensure normal item behavior
        super.onUpdate(stack, world, entity, itemSlot, isSelected);
    }
    public ItemStack onItemRightClick(final ItemStack stack, final World world, final EntityPlayer player) {
        final int damage = stack.getItemDamage();
        if (!world.isRemote && stack.isItemDamaged() && !player.capabilities.isCreativeMode) {
            this.refuel(stack, player, 1);
            this.refuel(stack, player, 0);
        }
        return stack;
    }
    
    
    private void refuel(final ItemStack stack, final EntityPlayer player, final int coalType) {
        final ItemStack[] list = player.inventory.mainInventory;
        for (int i = 0; i < list.length; ++i) {
            final ItemStack is = list[i];
            if(stack.getItem() == ModItems.mchelidebug) {
                if (is != null && is.getItem() == ModItems.canister_full && is.getItemDamage() == Fluids.COALOIL.getID()) {
                    for (int j = 0; is.stackSize > 0 && stack.isItemDamaged() && j < 64; ++j) {
                        int damage = stack.getItemDamage() - ((coalType == 1) ? 75 : 100);
                        if (damage < 0) {
                            damage = 0;
                        }
                        stack.setItemDamage(damage);
                        final ItemStack itemStack = is;
                        --itemStack.stackSize;
                    }
                    if (is.stackSize <= 0) {
                        list[i] = null;
                    }
                }
            }
            if(stack.getItem() == ModItems.mchtier1) {
                if (is != null && is.getItem() == ModItems.canister_full && is.getItemDamage() == Fluids.LIGHTOIL.getID()) {
                    for (int j = 0; is.stackSize > 0 && stack.isItemDamaged() && j < 64; ++j) {
                        int damage = stack.getItemDamage() - ((coalType == 1) ? 75 : 100);
                        if (damage < 0) {
                            damage = 0;
                        }
                        stack.setItemDamage(damage);
                        final ItemStack itemStack = is;
                        --itemStack.stackSize;
                    }
                    if (is.stackSize <= 0) {
                        list[i] = null;
                    }
                }
            }
            if(stack.getItem() == ModItems.mchtier2) {
                if (is != null && is.getItem() == ModItems.canister_full && is.getItemDamage() == Fluids.GASOLINE.getID()) {
                    for (int j = 0; is.stackSize > 0 && stack.isItemDamaged() && j < 64; ++j) {
                        int damage = stack.getItemDamage() - ((coalType == 1) ? 75 : 100);
                        if (damage < 0) {
                            damage = 0;
                        }
                        stack.setItemDamage(damage);
                        final ItemStack itemStack = is;
                        --itemStack.stackSize;
                    }
                    if (is.stackSize <= 0) {
                        list[i] = null;
                    }
                }
            }
            if(stack.getItem() == ModItems.mchtier3){
                if (is != null && is.getItem() == ModItems.canister_full && is.getItemDamage() == Fluids.KEROSENE_REFORM.getID()) {
                    for (int j = 0; is.stackSize > 0 && stack.isItemDamaged() && j < 64; ++j) {
                        int damage = stack.getItemDamage() - ((coalType == 1) ? 75 : 100);
                        if (damage < 0) {
                            damage = 0;
                        }
                        stack.setItemDamage(damage);
                        final ItemStack itemStack = is;
                        --itemStack.stackSize;
                    }
                    if (is.stackSize <= 0) {
                        list[i] = null;
                    }
                }
            }
            if(stack.getItem() == ModItems.mchtier4) {
                if (is != null && is.getItem() == ModItems.canister_full && is.getItemDamage() == Fluids.NITAN.getID()) {
                    for (int j = 0; is.stackSize > 0 && stack.isItemDamaged() && j < 64; ++j) {
                        int damage = stack.getItemDamage() - ((coalType == 1) ? 75 : 100);
                        if (damage < 0) {
                            damage = 0;
                        }
                        stack.setItemDamage(damage);
                        final ItemStack itemStack = is;
                        --itemStack.stackSize;
                    }
                    if (is.stackSize <= 0) {
                        list[i] = null;
                    }
                }
            }
        }
    }
  }


