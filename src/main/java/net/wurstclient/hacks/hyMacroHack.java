/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.WurstClient;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.minecraft.client.MinecraftClient;

@SearchTags({"macro", "garden", "skyblock"})
public final class hyMacroHack extends Hack implements UpdateListener
{
	private enum State
	{
		PRZEJSCIE,
		PEST_KILL,
		WARP_GARDEN,
		FARM,
		STASH
	}
	
	private static final long PRZEJSCIE_SEC = 4_000L;
	private static final long PEST_KILL_SEC = 60_000L;
	private static final long FARM_SEC = 13 * 60_000L + 40_000L;
	private static final long STASH_SEC = 60_000L * 120;
	private static final long STASH_DURATION_SEC = 30_000L;
	
	private State currentState;
	private long stateStart;
	private long LONG_ELAPSED;
	
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
	private stashHack stashHack;
	
	public hyMacroHack()
	{
		super("hyMacroHack");
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	protected void onEnable()
	{
		hyEnable();
	}
	
	@Override
	protected void onDisable()
	{
		hyDisable();
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
			case PEST_KILL:
			if(elapsed >= PRZEJSCIE_SEC)
			{
				if(LONG_ELAPSED >= STASH_SEC)
				{
					currentState = State.STASH;
					return;
				}
				resetKeys();
				pestHack.setEnabled(false);
				fightBotHack.setEnabled(true);
				przejscieHack.setEnabled(true);
				swapHotbarSlots(0, 1);
				
				currentState = State.WARP_GARDEN;
				stateStart = now;
				notify("Set to WARP_GARDEN state.");
			}
			break;
			case WARP_GARDEN:
			if(elapsed >= PEST_KILL_SEC)
			{
				if(LONG_ELAPSED >= STASH_SEC)
				{
					currentState = State.STASH;
					return;
				}
				przejscieHack.setEnabled(false);
				fightBotHack.setEnabled(false);
				
				gardenHack.setEnabled(true);
				
				currentState = State.FARM;
				stateStart = now;
				notify("Set to FARM state.");
			}
			break;
			case FARM:
			
			gardenHack.setEnabled(false);
			swapHotbarSlots(0, 1);
			
			farmingSimHack.startFarming();
			
			currentState = State.PRZEJSCIE;
			stateStart = now;
			notify("Set to FARM state");
			
			break;
			case PRZEJSCIE:
			if(elapsed >= FARM_SEC)
			{
				if(LONG_ELAPSED >= STASH_SEC)
				{
					currentState = State.STASH;
					return;
				}
				resetKeys();
				farmingSimHack.stopFarming();
				pestHack.setEnabled(true);
				currentState = State.PEST_KILL;
				stateStart = now;
				notify("Set to PRZEJSCIE state");
				
			}
			break;
			case STASH:
			notify("Set to STASH state.");
			resetKeys();
			stashHack.setEnabled(true);
			
			pestHack.setEnabled(false);
			przejscieHack.setEnabled(false);
			fightBotHack.setEnabled(false);
			gardenHack.setEnabled(false);
			farmingSimHack.stopFarming();
			autoSprintHack.setEnabled(false);
			autoReconnectHack.setEnabled(false);
			
			if(elapsed >= STASH_DURATION_SEC)
			{
				resetKeys();
				stashHack.setEnabled(false);
				przejscieHack.setEnabled(false);
				fightBotHack.setEnabled(false);
				
				gardenHack.setEnabled(true);
				
				currentState = State.FARM;
				stateStart = now;
				notify("Set to FARM state.");
			}
			break;
		}
		
	}
	
	private void hyDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		resetKeys();
		stashHack.setEnabled(false);
		pestHack.setEnabled(false);
		przejscieHack.setEnabled(false);
		fightBotHack.setEnabled(false);
		gardenHack.setEnabled(false);
		farmingSimHack.stopFarming();
		autoSprintHack.setEnabled(false);
		autoReconnectHack.setEnabled(false);
		notify("Disabled hyMacro.");
	}
	
	private void hyEnable()
	{
		resetKeys();
		
		pestHack = WurstClient.INSTANCE.getHax().pestHack;
		przejscieHack = WurstClient.INSTANCE.getHax().przejscieHack;
		fightBotHack = WurstClient.INSTANCE.getHax().fightBotHack;
		gardenHack = WurstClient.INSTANCE.getHax().gardenHack;
		farmingSimHack = WurstClient.INSTANCE.getHax().farmingSimHack;
		autoReconnectHack = WurstClient.INSTANCE.getHax().autoReconnectHack;
		autoSprintHack = WurstClient.INSTANCE.getHax().autoSprintHack;
		stashHack = WurstClient.INSTANCE.getHax().stashHack;
		
		autoReconnectHack.setEnabled(true);
		autoSprintHack.setEnabled(true);
		
		currentState = State.PEST_KILL;
		stateStart = System.currentTimeMillis();
		pestHack.setEnabled(true);
		
		EVENTS.add(UpdateListener.class, this);
		notify("Set to PRZEJSCIE state.");
	}
	
	private boolean isLookingAtOakSign()
	{
		MinecraftClient client = MinecraftClient.getInstance();
		
		if(client.player == null || client.world == null
			|| client.crosshairTarget == null)
			return false;
		
		if(client.crosshairTarget.getType() != HitResult.Type.BLOCK)
			return false;
		
		BlockPos pos = ((BlockHitResult)client.crosshairTarget).getBlockPos();
		BlockState state = client.world.getBlockState(pos);
		
		return state.isOf(Blocks.OAK_SIGN) || state.isOf(Blocks.OAK_WALL_SIGN);
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
		
		// to not trigger limbo
		int randSkyExtra = (int)(Math.random() * 901) + 100;
		if(skyblockOnCooldown
			&& now - skyblockCooldownStart >= cooldown + randSkyExtra)
			skyblockOnCooldown = false;
		int randGardenExtra = (int)(Math.random() * 901) + 100;
		if(gardenOnCooldown
			&& now - gardenCooldownStart >= cooldown + randGardenExtra)
			gardenOnCooldown = false;
		
		// statements
		if((y == 75 || y == 94) && !skyblockOnCooldown && (z <= 5 && z >= -5)
			&& ((x <= 15 && x >= 5) || (x >= -58 && x <= -45)))
		{ // lobby hypixel
			MC.player.networkHandler.sendChatCommand("skyblock");
			skyblockOnCooldown = true;
			skyblockCooldownStart = now;
			// -3 -70
		}else if(y == 70 && !gardenOnCooldown && (x <= 5 && x >= -7)
			&& (z >= -76 && z <= -62))
		{ // hub
			MC.player.networkHandler.sendChatCommand("warp garden");
			gardenOnCooldown = true;
			gardenCooldownStart = now;
			hyDisable();
			hyEnable();
			currentState = State.FARM;
		}else if(y == 31 && isLookingAtOakSign() && !skyblockOnCooldown)
		{ // limbo
			MC.player.networkHandler.sendChatCommand("l skyblock");
			skyblockOnCooldown = true;
			skyblockCooldownStart = now;
		}
	}
	
	private void notify(String message)
	{
		MC.inGameHud.getChatHud().addMessage(Text.of("[hyMacro] " + message));
	}
}
