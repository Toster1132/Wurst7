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

@SearchTags({"pest"})
public final class PestHack extends Hack implements UpdateListener
{
	
	private void swapHotbarSlots(int slot1, int slot2)
	{
		if(MC.player == null || MC.interactionManager == null)
			return;
		
		int invSlot1 = 36 + slot1;
		int invSlot2 = 36 + slot2;
		int syncId = MC.player.currentScreenHandler.syncId;
		
		MC.interactionManager.clickSlot(syncId, invSlot1, 0,
			SlotActionType.PICKUP, MC.player);
		MC.interactionManager.clickSlot(syncId, invSlot2, 0,
			SlotActionType.PICKUP, MC.player);
		MC.interactionManager.clickSlot(syncId, invSlot1, 0,
			SlotActionType.PICKUP, MC.player);
	}
	
	public PestHack()
	{
		super("PestHack");
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
		swapHotbarSlots(0, 1);
		setEnabled(false);
		
		// Send a command and press backKey
		MC.player.networkHandler.sendChatCommand("warp garden");
		MC.options.backKey.setPressed(true);
		// backKey was pressed
	}
	
	@Override
	protected void onDisable()
	{
		MC.options.backKey.setPressed(false);
		MC.options.forwardKey.setPressed(false);
		EVENTS.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{}
}
