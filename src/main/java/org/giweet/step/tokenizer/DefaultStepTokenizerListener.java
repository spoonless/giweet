package org.giweet.step.tokenizer;

import java.util.ArrayList;
import java.util.List;

import org.giweet.step.ParameterStepToken;
import org.giweet.step.StaticStepToken;
import org.giweet.step.StepToken;

public class DefaultStepTokenizerListener implements StepTokenizerListener {
	
	private List<StepToken> stepTokens = new ArrayList<StepToken>();
	private final TokenizerStrategy strategy;
	
	public DefaultStepTokenizerListener(TokenizerStrategy strategy) {
		this.strategy = strategy;
	}

	public void newToken(String token, boolean isMeaningful) {
		if (isMeaningful) {
			if (token.length() > 1  && token.charAt(0) == '$' && strategy.isParameterStepTokenAllowed()) {
				stepTokens.add(new ParameterStepToken(token.substring(1)));
			}
			else {
				stepTokens.add(new StaticStepToken(token, true));
			}
		}
		else if (strategy.isMeaninglessStepTokenAllowed()) {
			stepTokens.add(new StaticStepToken(token, false));
		}
	}

	public StepToken[] getStepTokens() {
		return stepTokens.toArray(new StepToken[stepTokens.size()]);
	}
}
