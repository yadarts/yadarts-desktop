package spare.n52.yadarts.layout;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import spare.n52.yadarts.entity.Player;
import spare.n52.yadarts.entity.impl.PlayerImpl;
import spare.n52.yadarts.games.GameStatusUpdateListener;
import spare.n52.yadarts.layout.GameParameter.Bounds;

public abstract class AbstractGameView implements GameView, GameStatusUpdateListener{
	
	public static final String PLAYERS_PARAMETER = "playersInput";
	
	protected static final String FONT = "Arial";
	protected static final int NORMAL_FONT = 16;
	protected static final int LARGE_FONT = 24;
	protected static final String MISSED = "X";
	protected static final String BOUNCE_OUT = "B";
	
	@Override
	public List<GameParameter<?>> getInputParameters() {
		List<GameParameter<?>> result = new ArrayList<>();
		result.add(new GameParameter<String>(String.class, PLAYERS_PARAMETER, Bounds.unbound(2)));
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Player> resolvePlayers(List<GameParameter<?>> inputValues) {
		for (GameParameter<?> gameParameter : inputValues) {
			switch (gameParameter.getName()) {
			case PLAYERS_PARAMETER:
				List<Player> result = new ArrayList<>();
				
				Object value = gameParameter.getValue();

				for (String player : (List<String>) value) {
					result.add(new PlayerImpl(player));
				}
				
				return result;
			default:
				break;
			}
		}
		return null;
	}
	
	protected void updateLabel(Composite wrapper, final Label theLabel, final String value) {
		wrapper.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				theLabel.setText(value);
				theLabel.pack();
			}
		});
	}
	
}
