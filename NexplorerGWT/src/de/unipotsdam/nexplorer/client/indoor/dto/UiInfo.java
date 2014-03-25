package de.unipotsdam.nexplorer.client.indoor.dto;

import de.unipotsdam.nexplorer.shared.PlayerInfo;
import de.unipotsdam.nexplorer.shared.GameStatus;

public class UiInfo {

	private UiPlayer player;
	private UiDataPacket dataPacket;
	private final String hint;
	private final String bonusGoal;
	private final Long remainingTime;
	private final GameStatus gameState;

	public UiInfo(PlayerInfo result) {
		this.hint = result.getHint();
		this.bonusGoal = result.getBonusGoal();
		this.remainingTime = result.getRemainingTime();
		this.gameState = result.getGameState();


		if (result.getPlayer() != null) {
			this.player = new UiPlayer(result.getPlayer());
		} else {
			this.player = null;
		}

		if (result.getDataPacketSend() != null) {
			this.dataPacket = new UiDataPacket(result.getDataPacketSend());
		} else {
			this.dataPacket = null;
		}
	}

	public UiPlayer getPlayer() {
		return this.player;
	}

	public UiDataPacket getDataPacketSend() {
		return this.dataPacket;
	}

	public String getHint() {
		return this.hint;
	}

	public String getBonusGoal() {
		return this.bonusGoal;
	}

	public Long getRemainingTime() {
		return this.remainingTime;
	}
	
	public GameStatus getGameState() {
		return this.gameState;
	}
}
