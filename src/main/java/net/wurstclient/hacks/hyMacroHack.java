/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.wurstclient.WurstClient;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;

@SearchTags({"macro"})
public final class hyMacroHack extends Hack implements UpdateListener
{
	private enum State
	{
		PEST,
		PESTHACK_PHASE,
		FARMING
	}
	
	private static final long PEST_DURATION = 10_000L; // 10s
	private static final long PESTHACK_DURATION = 2 * 60_000L; // 2m
	private static final long FARMING_DURATION = 15 * 60_000L; // 15m
	
	private State currentState;
	private long stateStart;
	
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
		pestHack = WurstClient.INSTANCE.getHax().pestHack;
		przejscieHack = WurstClient.INSTANCE.getHax().przejscieHack;
		fightBotHack = WurstClient.INSTANCE.getHax().fightBotHack;
		gardenHack = WurstClient.INSTANCE.getHax().gardenHack;
		farmingSimHack = WurstClient.INSTANCE.getHax().farmingSimHack;
		autoReconnectHack = WurstClient.INSTANCE.getHax().autoReconnectHack;
		autoSprintHack = WurstClient.INSTANCE.getHax().autoSprintHack;
		
		currentState = State.PEST;
		stateStart = System.currentTimeMillis();
		
		EVENTS.add(UpdateListener.class, this);
		pestHack.setEnabled(true);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		pestHack.setEnabled(false);
		przejscieHack.setEnabled(false);
		fightBotHack.setEnabled(false);
		gardenHack.setEnabled(false);
		farmingSimHack.setEnabled(false);
		autoSprintHack.setEnabled(false);
		autoReconnectHack.setEnabled(false);
	}
	
	@Override
	public void onUpdate()
	{
		long now = System.currentTimeMillis();
		long elapsed = now - stateStart;
		
		switch(currentState)
		{
			case PEST:
			if(elapsed >= PEST_DURATION)
			{
				pestHack.setEnabled(false);
				przejscieHack.setEnabled(true);
				fightBotHack.setEnabled(true);
				currentState = State.PESTHACK_PHASE;
				stateStart = now;
			}
			break;
			
			case PESTHACK_PHASE:
			if(elapsed >= PESTHACK_DURATION)
			{
				przejscieHack.setEnabled(false);
				fightBotHack.setEnabled(false);
				gardenHack.setEnabled(true);
				gardenHack.setEnabled(false);
				farmingSimHack.setEnabled(true);
				currentState = State.FARMING;
				stateStart = now;
			}
			break;
			
			case FARMING:
			if(elapsed >= FARMING_DURATION)
			{
				farmingSimHack.setEnabled(false);
				pestHack.setEnabled(true);
				currentState = State.PEST;
				stateStart = now;
			}
			break;
		}
	}
}
