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
		IKeyBinding.get(MC.options.sneakKey).resetPressedState();
	}
	
	private int timer = 2;
	
	@Override
	public void onUpdate()
	{
		if(MC.player != null)
		{
			int x = MC.player.getBlockX();
			int y = MC.player.getBlockY();
			int z = MC.player.getBlockZ();
			if(timer == 2)
			{
				// pestXterm
				timer = 0;
			}else if(y == 75)
			{
				MC.player.networkHandler.sendChatCommand("skyblock");
				MC.player.networkHandler.sendChatCommand("warp garden");
			}else if(y == 70)
			{
				MC.player.networkHandler.sendChatCommand("warp garden");
			}else
			{
				MC.options.attackKey.setPressed(true);
				if(x < 47 || x > 143 || z < -48 || z > 143)
				{
					MC.inGameHud.getChatHud()
						.addMessage(Text.of("X or Y are Bad!!!"));
				}else if(y > 69 || y < 68)
				{
					MC.inGameHud.getChatHud()
						.addMessage(Text.of("Y is Bad!!!"));
				}
				
				if(x == 140 && z == -48)
				{
					MC.player.networkHandler.sendChatCommand("warp garden");
					timer++;
				}else if((z >= -48 && 142 >= z) && (x == 49 || x == 63
					|| x == 77 || x == 91 || x == 105 || x == 119 || x == 133))
				{
					MC.options.rightKey.setPressed(true);
					MC.options.forwardKey.setPressed(true);
					IKeyBinding.get(MC.options.leftKey).resetPressedState();
					IKeyBinding.get(MC.options.sneakKey).resetPressedState();
				}else if((z >= -47 && 143 <= z) && (x == 56 || x == 70
					|| x == 84 || x == 98 || x == 112 || x == 126 || x == 140))
				{
					MC.options.leftKey.setPressed(true);
					MC.options.forwardKey.setPressed(true);
					IKeyBinding.get(MC.options.rightKey).resetPressedState();
					IKeyBinding.get(MC.options.sneakKey).resetPressedState();
				}
			}
		}
	}
}
