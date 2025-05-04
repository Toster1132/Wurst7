/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.text.Text;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.mixinterface.IKeyBinding;

@SearchTags({"farming"})
public final class FarmingSimHack extends Hack implements UpdateListener
{
	public FarmingSimHack()
	{
		super("FarmingSim");
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		IKeyBinding.get(MC.options.forwardKey).resetPressedState();
		IKeyBinding.get(MC.options.leftKey).resetPressedState();
		IKeyBinding.get(MC.options.rightKey).resetPressedState();
		IKeyBinding.get(MC.options.backKey).resetPressedState();
		IKeyBinding.get(MC.options.attackKey).resetPressedState();
	}
	
	private long skyblockCooldownStart = 0;
	private long gardenCooldownStart = 0;
	private final int cooldown = 5000;
	private boolean skyblockOnCooldown = false;
	private boolean gardenOnCooldown = false;
	
	@Override
	public void onUpdate()
	{
		if(MC.player == null)
			return;
		
		int x = MC.player.getBlockX();
		int y = MC.player.getBlockY();
		int z = MC.player.getBlockZ();
		
		long now = System.currentTimeMillis();
		
		int randomExtra = (int)(Math.random() * 901) + 100;
		if(skyblockOnCooldown
			&& now - skyblockCooldownStart >= cooldown + randomExtra)
			skyblockOnCooldown = false;
		
		int randomExtra2 = (int)(Math.random() * 901) + 100;
		if(gardenOnCooldown
			&& now - gardenCooldownStart >= cooldown + randomExtra2)
			gardenOnCooldown = false;
		
		if(y == 75 && !skyblockOnCooldown) // magic const; hub y lvl
		{
			MC.player.networkHandler.sendChatCommand("skyblock");
			skyblockOnCooldown = true;
			skyblockCooldownStart = now;
		}else if(y == 70 && !gardenOnCooldown) // magic const; skyblock hub y
												// lvl
		{
			MC.player.networkHandler.sendChatCommand("warp garden");
			gardenOnCooldown = true;
			gardenCooldownStart = now;
		}else if(y == 94 && !skyblockOnCooldown) // magic const; hub y lvl
		{
			MC.player.networkHandler.sendChatCommand("skyblock");
			skyblockOnCooldown = true;
			skyblockCooldownStart = now;
		}else
		{
			MC.options.attackKey.setPressed(true);
			
			if(x < 47 || x > 143 || z < -48 || z > 143)
			{
				MC.inGameHud.getChatHud()
					.addMessage(Text.of("X or Z are Bad!!!"));
			}else if(y > 69 || y < 68)
			{
				MC.inGameHud.getChatHud().addMessage(Text.of("Y is Bad!!!"));
			}
			
			if(x == 140 && z == -48)
			{
				MC.player.networkHandler.sendChatCommand("warp garden");
			}else if((z >= -48 && z <= 142) && (x == 49 || x == 63 || x == 77
				|| x == 91 || x == 105 || x == 119 || x == 133))
			{
				MC.options.rightKey.setPressed(true);
				MC.options.forwardKey.setPressed(true);
				IKeyBinding.get(MC.options.leftKey).resetPressedState();
			}else if((z >= -48 && z <= 143) && (x == 56 || x == 70 || x == 84
				|| x == 98 || x == 112 || x == 126 || x == 140))
			{
				MC.options.leftKey.setPressed(true);
				MC.options.forwardKey.setPressed(true);
				IKeyBinding.get(MC.options.rightKey).resetPressedState();
			}
		}
	}
}
