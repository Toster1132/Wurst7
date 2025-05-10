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
	private boolean farmingActive = false;
	
	public FarmingSimHack()
	{
		super("FarmingSim");
		setCategory(Category.MOVEMENT);
	}
	
	@Override
	protected void onEnable()
	{
		farmingActive = true;
		EVENTS.add(UpdateListener.class, this);
		startFarming();
	}
	
	@Override
	protected void onDisable()
	{
		farmingActive = false;
		IKeyBinding.get(MC.options.attackKey).resetPressedState();
		IKeyBinding.get(MC.options.forwardKey).resetPressedState();
		IKeyBinding.get(MC.options.backKey).resetPressedState();
		IKeyBinding.get(MC.options.leftKey).resetPressedState();
		IKeyBinding.get(MC.options.rightKey).resetPressedState();
		EVENTS.remove(UpdateListener.class, this);
	}
	
	public void startFarming()
	{
		this.setEnabled(true);
	}
	
	public void stopFarming()
	{
		IKeyBinding.get(MC.options.attackKey).resetPressedState();
		IKeyBinding.get(MC.options.forwardKey).resetPressedState();
		IKeyBinding.get(MC.options.backKey).resetPressedState();
		IKeyBinding.get(MC.options.leftKey).resetPressedState();
		IKeyBinding.get(MC.options.rightKey).resetPressedState();
		this.setEnabled(false);
	}
	
	@Override
	public void onUpdate()
	{
		if(!farmingActive || MC.player == null)
			return;
		
		int x = MC.player.getBlockX();
		int y = MC.player.getBlockY();
		int z = MC.player.getBlockZ();
		
		IKeyBinding.get(MC.options.attackKey).setPressed(true);
		
		if(x < 47 || x > 143 || z < -48 || z > 143)
		{
			MC.inGameHud.getChatHud()
				.addMessage(Text.of("[FarmingSim] X or Z are Bad!"));
			return;
		}
		if(y > 69 || y < 68)
		{
			MC.inGameHud.getChatHud()
				.addMessage(Text.of("[FarmingSim] Y is Bad!"));
			return;
		}
		
		if(x == 140 && z == -48)
		{
			MC.player.networkHandler.sendChatCommand("warp garden");
			return;
		}
		
		if((z >= -48 && z <= 142) && (x == 49 || x == 63 || x == 77 || x == 91
			|| x == 105 || x == 119 || x == 133))
		{
			IKeyBinding.get(MC.options.rightKey).setPressed(true);
			IKeyBinding.get(MC.options.forwardKey).setPressed(true);
			IKeyBinding.get(MC.options.leftKey).resetPressedState();
		}else if((z >= -48 && z <= 143) && (x == 56 || x == 70 || x == 84
			|| x == 98 || x == 112 || x == 126 || x == 140))
		{
			IKeyBinding.get(MC.options.leftKey).setPressed(true);
			IKeyBinding.get(MC.options.forwardKey).setPressed(true);
			IKeyBinding.get(MC.options.rightKey).resetPressedState();
		}else
		{
			IKeyBinding.get(MC.options.forwardKey).setPressed(true);
			IKeyBinding.get(MC.options.leftKey).resetPressedState();
			IKeyBinding.get(MC.options.rightKey).resetPressedState();
		}
	}
}
