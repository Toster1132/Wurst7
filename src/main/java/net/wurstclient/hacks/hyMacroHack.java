/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.wurstclient.WurstClient;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;

@SearchTags({"macro", "garden", "skyblock"})
public final class hyMacroHack extends Hack implements UpdateListener
{
	private enum State
	{
		PEST,
		PESTHACK_PHASE,
		FARMING
	}
	
	private static final long PEST_DURATION = 4_000L;
	private static final long PESTHACK_DURATION = 60_000L;
	private static final long FARMING_DURATION = 13 * 60_000L + 40_000L;
	
	private State currentState;
	private long stateStart;
	
	private final int cooldown = 5000;
	private long skyblockCooldownStart = 0;
	private long gardenCooldownStart = 0;
	private boolean skyblockOnCooldown = false;
	private boolean gardenOnCooldown = false;
	
	private PestHack pestHack;
	private PrzejscieHack przejscieHack;
	private FightBotHack fightBotHack;
	private GardenHack gardenHack;
	private FarmingSimHack farmingSimHack;
	private AutoSprintHack autoSprintHack;
	private AutoReconnectHack autoReconnectHack;
	
	public hyMacroHack()
	{
		super("hyMacroHack");
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	protected void onEnable()
	{
		resetKeys();
		
		pestHack = WurstClient.INSTANCE.getHax().pestHack;
		przejscieHack = WurstClient.INSTANCE.getHax().przejscieHack;
		fightBotHack = WurstClient.INSTANCE.getHax().fightBotHack;
		gardenHack = WurstClient.INSTANCE.getHax().gardenHack;
		farmingSimHack = WurstClient.INSTANCE.getHax().farmingSimHack;
		autoReconnectHack = WurstClient.INSTANCE.getHax().autoReconnectHack;
		autoSprintHack = WurstClient.INSTANCE.getHax().autoSprintHack;
		
		autoReconnectHack.setEnabled(true);
		autoSprintHack.setEnabled(true);
		
		currentState = State.PEST;
		stateStart = System.currentTimeMillis();
		pestHack.setEnabled(true);
		
		EVENTS.add(UpdateListener.class, this);
		notify("Started in PEST state.");
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		resetKeys();
		
		pestHack.setEnabled(false);
		przejscieHack.setEnabled(false);
		fightBotHack.setEnabled(false);
		gardenHack.setEnabled(false);
		farmingSimHack.stopFarming();
		autoSprintHack.setEnabled(false);
		autoReconnectHack.setEnabled(false);
		notify("Disabled hyMacroHack.");
	}
	
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
	
	@Override
	public void onUpdate()
	{
		if(MC.player == null)
			return;
		
		handleTeleportLogic();
		
		long now = System.currentTimeMillis();
		long elapsed = now - stateStart;
		
		switch(currentState)
		{
			case PEST:
			if(elapsed >= PEST_DURATION)
			{
				resetKeys();
				pestHack.setEnabled(false);
				fightBotHack.setEnabled(true);
				przejscieHack.setEnabled(true);
				swapHotbarSlots(0, 1);
				currentState = State.PESTHACK_PHASE;
				stateStart = now;
				notify("Switched to PESTHACK_PHASE.");
			}
			break;
			
			case PESTHACK_PHASE:
			if(elapsed >= PESTHACK_DURATION)
			{
				przejscieHack.setEnabled(false);
				fightBotHack.setEnabled(false);
				
				// Restart garden hack cleanly
				gardenHack.setEnabled(false);
				gardenHack.setEnabled(true);
				swapHotbarSlots(0, 1);
				farmingSimHack.startFarming();
				
				currentState = State.FARMING;
				stateStart = now;
				notify("Switched to FARMING.");
			}
			break;
			
			case FARMING:
			if(elapsed >= FARMING_DURATION)
			{
				resetKeys();
				farmingSimHack.stopFarming();
				pestHack.setEnabled(true);
				currentState = State.PEST;
				stateStart = now;
				notify("Switched to PEST.");
			}
			break;
		}
	}
	
	private void resetKeys()
	{
		MC.options.attackKey.setPressed(false);
		MC.options.forwardKey.setPressed(false);
		MC.options.backKey.setPressed(false);
		MC.options.leftKey.setPressed(false);
		MC.options.rightKey.setPressed(false);
	}
	
	private void handleTeleportLogic()
	{
		int x = MC.player.getBlockX();
		int y = MC.player.getBlockY();
		int z = MC.player.getBlockZ();
		long now = System.currentTimeMillis();
		
		int randSkyExtra = (int)(Math.random() * 901) + 100;
		if(skyblockOnCooldown
			&& now - skyblockCooldownStart >= cooldown + randSkyExtra)
			skyblockOnCooldown = false;
		
		int randGardenExtra = (int)(Math.random() * 901) + 100;
		if(gardenOnCooldown
			&& now - gardenCooldownStart >= cooldown + randGardenExtra)
			gardenOnCooldown = false;
		
		if(y == 75 && !skyblockOnCooldown && x != 48 && z != -47)
		{
			MC.player.networkHandler.sendChatCommand("skyblock");
			skyblockOnCooldown = true;
			skyblockCooldownStart = now;
		}else if(y == 70 && !gardenOnCooldown && x != 48 && z != -47)
		{
			MC.player.networkHandler.sendChatCommand("warp garden");
			gardenOnCooldown = true;
			gardenCooldownStart = now;
		}
	}
	
	private void notify(String message)
	{
		MC.inGameHud.getChatHud()
			.addMessage(Text.of("[hyMacroHack] " + message));
	}
}
