/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.util.math.BlockPos;
import net.minecraft.text.Text;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.mixinterface.IKeyBinding;

@SearchTags({"przejscie"})
public final class PrzejscieHack extends Hack implements UpdateListener
{
	private static final int AFK_BUFFER_SIZE = 25;
	private final BlockPos[] lastPositions = new BlockPos[AFK_BUFFER_SIZE];
	private final float[] lastYaws = new float[AFK_BUFFER_SIZE];
	private final float[] lastPitches = new float[AFK_BUFFER_SIZE];
	private int afkIndex = 0;
	private boolean afkDetected = false;
	private boolean isMoving = false;
	private long movementStartTime;
	private static final int MOVEMENT_DURATION = 3_000;
	private Runnable movementAction = null;
	
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
		movementAction = decideMovementAction();
	}
	
	private void stopMovementMacro()
	{
		IKeyBinding.get(MC.options.forwardKey).resetPressedState();
		isMoving = false;
		afkDetected = false;
		movementAction = null;
	}
	
	private void pitchYawForward(int yaw, int pitch)
	{
		MC.player.setYaw(yaw);
		MC.player.setPitch(pitch);
		MC.options.forwardKey.setPressed(true);
	}
	
	private void performMovement()
	{
		if(movementAction != null)
			movementAction.run();
	}
	
	private Runnable decideMovementAction()
	{
		int x = MC.player.getBlockX();
		int z = MC.player.getBlockZ();
		
		if(x <= 135 && x >= 97)
		{
			if(z >= -52 && z <= 0)
				return () -> pitchYawForward(90, 0); // zone 1
			if(z >= 1 && z <= 48)
				return () -> pitchYawForward(-180, 0); // zone 3
			if(z >= 49 && z <= 96)
				return () -> pitchYawForward(-180, 0); // zone 5
			if(z >= 97 && z <= 138)
				return () -> pitchYawForward(-180, 0); // zone 7
		}else if(x <= 96 && x >= 52)
		{
			if(z >= -52 && z <= 0)
				return () -> pitchYawForward(0, 0); // zone 2
			if(z >= 1 && z <= 48)
				return () -> pitchYawForward(0, 0); // zone 4
			if(z >= 49 && z <= 96)
				return () -> pitchYawForward(0, 0); // zone 6
			if(z >= 97 && z <= 138)
				return () -> pitchYawForward(-90, 0); // zone 8
		}else if(x > 135 && x <= 140)
			return () -> pitchYawForward(90, 0);
		
		else if(x >= 47 && x < 52)
			return () -> pitchYawForward(-90, 0);
		
		return () -> {}; // default: do nothing
	}
}
