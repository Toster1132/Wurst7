/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.util.math.BlockPos;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.mixinterface.IKeyBinding;
import net.minecraft.text.Text;

@SearchTags({"przejscie"})
public final class PrzejscieHack extends Hack implements UpdateListener
{
	public PrzejscieHack()
	{
		super("PrzejscieHack");
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
	}
	
	private static final int AFK_BUFFER_SIZE = 150;
	private final BlockPos[] lastPositions = new BlockPos[AFK_BUFFER_SIZE];
	private final float[] lastYaws = new float[AFK_BUFFER_SIZE];
	private final float[] lastPitches = new float[AFK_BUFFER_SIZE];
	private int afkIndex = 0;
	private boolean afkDetected = false;
	private boolean isMoving = false;
	private long movementStartTime;
	private static final int MOVEMENT_DURATION = 5_000;
	
	@Override
	public void onUpdate()
	{
		if(MC.player == null)
			return;
		
		long now = System.currentTimeMillis();
		
		if(isMoving)
		{
			if(now - movementStartTime < MOVEMENT_DURATION)
			{
				performMovement();
			}else
			{
				stopMovementMacro();
			}
			return;
		}
		
		BlockPos pos = MC.player.getBlockPos();
		float yaw = MC.player.getYaw();
		float pitch = MC.player.getPitch();
		
		lastPositions[afkIndex] = pos;
		lastYaws[afkIndex] = yaw;
		lastPitches[afkIndex] = pitch;
		afkIndex = (afkIndex + 1) % AFK_BUFFER_SIZE;
		
		boolean allSame = true;
		
		if(lastPositions[AFK_BUFFER_SIZE - 1] != null)
		{
			BlockPos firstPos = lastPositions[0];
			float firstYaw = lastYaws[0];
			float firstPitch = lastPitches[0];
			
			for(int i = 1; i < AFK_BUFFER_SIZE; i++)
			{
				if(!lastPositions[i].equals(firstPos) || lastYaws[i] != firstYaw
					|| lastPitches[i] != firstPitch)
				{
					allSame = false;
					break;
				}
			}
		}else
		{
			allSame = false;
		}
		
		if(allSame && !afkDetected)
		{
			afkDetected = true;
			MC.inGameHud.getChatHud()
				.addMessage(Text.of("AFK detected—starting movement macro..."));
			startMovementMacro();
		}else if(!allSame && afkDetected)
		{
			MC.inGameHud.getChatHud().addMessage(Text.of("AFK cancelled"));
			afkDetected = false;
		}
	}
	
	private void startMovementMacro()
	{
		isMoving = true;
		movementStartTime = System.currentTimeMillis();
	}
	
	private void stopMovementMacro()
	{
		isMoving = false;
		IKeyBinding.get(MC.options.forwardKey).resetPressedState();
		IKeyBinding.get(MC.options.rightKey).resetPressedState();
		IKeyBinding.get(MC.options.leftKey).resetPressedState();
	}
	
	private void performMovement()
	{
		// TUTAJ KURWA MACRO
        MC.options.forwardKey.setPressed(true);
		MC.options.rightKey.setPressed(true);
	}
	
}
