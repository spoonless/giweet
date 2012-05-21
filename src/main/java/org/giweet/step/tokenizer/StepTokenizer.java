package org.giweet.step.tokenizer;

import java.io.IOException;
import java.io.Reader;

import org.giweet.step.StepToken;

public class StepTokenizer {
	private static final int DEFAULT_BUFFER_SIZE = 10;
	private final CharacterAnalyzer characterAnalyzer;
	private final TokenizerStrategy strategy;
	private final int bufferSize;

	public StepTokenizer(TokenizerStrategy strategy) {
		this(strategy, new DefaultCharacterAnalyzer(), DEFAULT_BUFFER_SIZE);
	}

	public StepTokenizer(TokenizerStrategy strategy, int bufferSize) {
		this(strategy, new DefaultCharacterAnalyzer(), bufferSize);
	}

	public StepTokenizer(TokenizerStrategy strategy, CharacterAnalyzer characterAnalyzer) {
		this(strategy, characterAnalyzer, DEFAULT_BUFFER_SIZE);
	}

	public StepTokenizer(TokenizerStrategy strategy, CharacterAnalyzer characterAnalyzer, int bufferSize) {
		if (bufferSize == 0) {
			throw new IllegalArgumentException("buffer size must be greater than 0");
		}
		this.strategy = strategy;
		this.characterAnalyzer = characterAnalyzer;
		this.bufferSize = bufferSize;
	}
	
	public StepToken[] tokenize(String value) {
		char[] buffer = value.toCharArray();
		DefaultStepTokenizerListener listener = new DefaultStepTokenizerListener(strategy);
		TokenizerContext ctx = new TokenizerContext();
		ctx.listener = listener;
		ctx.bufferLength = buffer.length;

		tokenize(buffer, ctx);

		ctx.createMeaningfulToken(buffer, false);
		ctx.createMeaninglessToken(buffer);
		return listener.getStepTokens();
	}

	public StepToken[] tokenize(Reader reader) throws IOException {
		DefaultStepTokenizerListener listener = new DefaultStepTokenizerListener(strategy);
		this.tokenize(reader, listener);
		return listener.getStepTokens();
	}

	public void tokenize(Reader reader, StepTokenizerListener listener) throws IOException {
		TokenizerContext ctx = new TokenizerContext();
		ctx.listener = listener;
		char[] buffer = new char[bufferSize];
		
		while ((ctx.bufferLength = reader.read(buffer, ctx.bufferOffset, buffer.length - ctx.bufferOffset)) > 0) {
			tokenize(buffer, ctx);
			buffer = updateBuffer(ctx, buffer);
		}
		
		ctx.createMeaningfulToken(buffer, false);
		ctx.createMeaninglessToken(buffer);
	}

	private char[] updateBuffer(TokenizerContext ctx, char[] buffer) {
		char[] newBuffer = buffer;
		int remainingCharCount = ctx.bufferLength + ctx.bufferOffset - ctx.tokenStartPosition;
		if (remainingCharCount > 0) {
			if (remainingCharCount >= newBuffer.length) {
				newBuffer = new char[newBuffer.length + remainingCharCount];
			}
			System.arraycopy(buffer, ctx.tokenStartPosition, newBuffer, 0, remainingCharCount);
		}
		ctx.bufferOffset = remainingCharCount;
		ctx.tokenStartPosition = 0;
		return newBuffer;
	}
	
	private class TokenizerContext {
		public int bufferOffset;
		public int bufferLength;
		public int tokenStartPosition;
		public int letterCount;
		public int separatorCount;
		public int trailingCharCount;
		public int commentCharCount;
		public char expectedEndQuote;
		public StepTokenizerListener listener;
		
		public boolean isQuote() {
			return this.expectedEndQuote != 0;
		}
		
		public boolean isQuoteEnd(char currentChar) {
			if (currentChar == expectedEndQuote) {
				expectedEndQuote = 0;
				return true;
			}
			return false;
		}
		
		public void createMeaningfulToken(char[] characters, boolean allowEmpty) {
			if (letterCount > 0 || allowEmpty) {
				if (letterCount == 0) {
					createMeaninglessToken(characters);
				}
				letterCount -= trailingCharCount;

				String token = new String (characters, tokenStartPosition, letterCount);
				listener.newToken(token, true);

				tokenStartPosition += letterCount;
				separatorCount += trailingCharCount;
				letterCount = 0;
				trailingCharCount = 0;
			}
		}
		
		public void createMeaninglessToken(char[] characters) {
			if (separatorCount > 0) {
				String token = new String (characters, tokenStartPosition, separatorCount);
				listener.newToken(token, false);
			}
			tokenStartPosition += separatorCount;
			separatorCount = 0;
		}
	}
	
	private void tokenize (char[] characters, TokenizerContext ctx) {
		int lastPosition = ctx.bufferOffset + ctx.bufferLength;
		boolean allowEmptyMeaningfulToken = false;
		for (int i = ctx.bufferOffset ; i < lastPosition ; i++) {
			char c = characters[i];
			int characterType = 0;
			
			if (ctx.isQuote()) {
				if (! ctx.isQuoteEnd(c)) {
					characterType = CharacterAnalyzer.LETTER;
				}
				else {
					characterType = CharacterAnalyzer.SEPARATOR;
					allowEmptyMeaningfulToken = true;
				}
			}
			else {
				characterType = characterAnalyzer.getCharacterType(c);
			}
			
			if (ctx.commentCharCount > 0) {
				switch (characterType) {
				case CharacterAnalyzer.COMMENT_HEAD:
					ctx.commentCharCount++;
					break;
				case CharacterAnalyzer.COMMENT_TAIL:
					ctx.commentCharCount--;
					break;
				}
				characterType = CharacterAnalyzer.SEPARATOR;
			}
			
			if ((characterType & CharacterAnalyzer.SEPARATOR) != 0) {
				ctx.createMeaningfulToken(characters, allowEmptyMeaningfulToken);
				allowEmptyMeaningfulToken = false;
				ctx.separatorCount++;
			}

			switch (characterType) {
			case CharacterAnalyzer.LETTER:
				ctx.createMeaninglessToken(characters);
				ctx.letterCount++;
				ctx.trailingCharCount = 0;
				break;
			case CharacterAnalyzer.SEPARATOR_IF_LEADING:
				if (ctx.letterCount != 0) {
					ctx.letterCount++;
				}
				else {
					ctx.separatorCount++;
				}
				break;
			case CharacterAnalyzer.SEPARATOR_IF_TRAILING:
			case CharacterAnalyzer.SEPARATOR_IF_LEADING_OR_TRAILING:
				if (ctx.letterCount != 0) {
					ctx.letterCount++;
					ctx.trailingCharCount++;
				}
				else {
					// FIXME unappropriate behavior for SEPARATOR_IF_TRAILING
					ctx.separatorCount++;
				}
				break;
			case CharacterAnalyzer.COMMENT_HEAD:
				ctx.commentCharCount++;
				break;
			case CharacterAnalyzer.QUOTE_HEAD:
				// TODO must be refactored because QUOTE_HEAD is nearly identical with SEPARATOR_IF_LEADING
				if (ctx.letterCount == 0) {
					ctx.separatorCount++;
					ctx.expectedEndQuote = characterAnalyzer.getExpectedEndQuote(c);
				}
				else {
					ctx.letterCount++;
				}
				break;
			}
		}
	}
}
