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
		MC.player.networkHandler.sendChatCommand("storage");
		hasOpenedStorage = false;
		clickedSlot27 = false;
		overclicked = false;
		slot = 0;
		start = System.currentTimeMillis();
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		MinecraftClient.getInstance().setScreen(null);
	}
	
	boolean hasOpenedStorage = false;
	boolean clickedSlot27 = false;
	boolean overclicked = false;
	int slot = 0;
	long start = System.currentTimeMillis();
	
	@Override
	public void onUpdate()
	{
		long now = System.currentTimeMillis();
		long elapsed = now - start;
		
		if(!hasOpenedStorage)
		{
			MC.player.networkHandler.sendChatMessage("/storage");
			hasOpenedStorage = true;
			start = now;
			return;
		}
		
		if(elapsed < 500L || MC.player.currentScreenHandler == null)
			return;
		
		int syncId = MC.player.currentScreenHandler.syncId;
		int totalSlots = MC.player.currentScreenHandler.slots.size();
		int chestSlotCount = totalSlots - 36; // chest slots
		int playerInvStart = chestSlotCount;
		int hotbarStart = totalSlots - 9;
		
		if(!clickedSlot27)
		{
			if(chestSlotCount > 27)
			{
				MC.interactionManager.clickSlot(syncId, 27, 0,
					SlotActionType.PICKUP, MC.player);
				clickedSlot27 = true;
				start = now;
			}
			return;
		}
		
		if(!overclicked)
		{
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
