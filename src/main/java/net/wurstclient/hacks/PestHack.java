/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;

@SearchTags({"pest"})
public final class PestHack extends Hack implements UpdateListener
{
	
	public PestHack()
	{
		super("PestHack");
		setCategory(Category.MOVEMENT);
	}
	@Override
	protected void onEnable()
	{
		// setEnabled(false);
		
		// Send a command and press backKey
		MC.player.networkHandler.sendChatCommand("warp garden");
		EVENTS.add(UpdateListener.class, this);
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
	{
	}
}
