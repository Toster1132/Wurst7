/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.screen.slot.SlotActionType;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.mixinterface.IKeyBinding;
import net.minecraft.client.MinecraftClient;

@SearchTags({"stash"})
public final class stashHack extends Hack implements UpdateListener
{
	
	public stashHack()
	{
		super("StashHack");
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	protected void onEnable()
	{
		hasOpenedStorage = false;
		overclicked = false;
		hasWarped = false;
		slot = 0;
		MC.player.networkHandler.sendChatCommand("hub");
		start = System.currentTimeMillis();
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		MinecraftClient.getInstance().setScreen(null);
		EVENTS.remove(UpdateListener.class, this);
	}
	
	boolean hasOpenedStorage = false;
	boolean overclicked = false;
	int slot = 0;
	long start = System.currentTimeMillis();
	long PODEJSCIE_DO_BABKI = 2_000L;
	boolean hasWarped = false;
	
	@Override
	public void onUpdate()
	{
		long now = System.currentTimeMillis();
		long elapsed = now - start;
		
		if(!hasWarped)
		{
			MC.player.setYaw(263);
			MC.player.setPitch(0);
			
			if(elapsed >= 500L)
			{
				MC.options.forwardKey.setPressed(true);
				hasWarped = true;
				start = now;
			}
			
			return;
		}
		
		if(!hasOpenedStorage)
		{
			if(elapsed >= PODEJSCIE_DO_BABKI)
			{
				MC.options.forwardKey.setPressed(false);
				IKeyBinding.get(MC.options.useKey).setPressed(true);
				hasOpenedStorage = true;
				start = now;
			}
			
			return;
		}
		IKeyBinding.get(MC.options.useKey).resetPressedState();
		if(elapsed < 500L || MC.player.currentScreenHandler == null)
			return;
		
		int syncId = MC.player.currentScreenHandler.syncId;
		int totalSlots = MC.player.currentScreenHandler.slots.size();
		int chestSlotCount = totalSlots - 36;
		int playerInvStart = chestSlotCount;
		int hotbarStart = totalSlots - 9;
		
		if(!overclicked)
		{
			// MC.options.attackKey.setPressed(false);
			int currentSlot = playerInvStart + slot;
			if(currentSlot < hotbarStart)
			{
				MC.interactionManager.clickSlot(syncId, currentSlot, 0,
					SlotActionType.QUICK_MOVE, MC.player);
				slot++;
				start = now;
			}else
			{
				overclicked = true;
			}
		}
	}
	
}
