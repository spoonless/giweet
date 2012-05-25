package org.giweet.step.tokenizer;

import java.io.IOException;
import java.io.Reader;

import org.giweet.step.StepToken;

public class StepTokenizer {
	private static final int DEFAULT_BUFFER_SIZE = 255;
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

		ctx.tokenize(buffer, 0, buffer.length, true);

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
		int bufferOffset = 0;
		int bufferLength = 0;
		
		while ((bufferLength = reader.read(buffer, bufferOffset, buffer.length - bufferOffset)) > 0) {
			int remainingCharCount = ctx.tokenize(buffer, bufferOffset, bufferLength, false);
			bufferOffset = bufferOffset + bufferLength - remainingCharCount;
			buffer = updateBuffer(buffer, bufferOffset, remainingCharCount);
			bufferOffset = remainingCharCount;
		}
		ctx.tokenize(buffer, bufferOffset, 0, true);
	}

	private char[] updateBuffer(char[] buffer, int offset, int nbReadable) {
		char[] newBuffer = buffer;
		if (nbReadable > 0) {
			if (nbReadable >= newBuffer.length) {
				newBuffer = new char[newBuffer.length + nbReadable];
			}
			System.arraycopy(buffer, offset, newBuffer, 0, nbReadable);
		}
		return newBuffer;
	}
	
	private class TokenizerContext {
		private int tokenStartPosition;
		private int tokenLength;
		private int trailingCharCount;
		private boolean nextTokenIsMeaningful = false;
		private StepTokenizerListener listener;
		private char expectedQuoteTail;
		private char expectedCommentTail;
		
		private boolean isNextCharTokenDelimiter (int characterType, char character) {
			boolean isTokenDelimiter = false;
			
			if (nextTokenIsMeaningful) {
				if ((CharacterAnalyzer.SEPARATOR & characterType) > 0) {
					isTokenDelimiter = true;
				}
				else if ((CharacterAnalyzer.SEPARATOR_IF_TRAILING & characterType) > 0) {
					trailingCharCount++;
				}
				else {
					trailingCharCount = 0;
				}
			}
			else if ((CharacterAnalyzer.LETTER & characterType) > 0) {
				isTokenDelimiter = true;
			}
			else if (CharacterAnalyzer.QUOTE_HEAD == characterType) {
				expectedQuoteTail = characterAnalyzer.getExpectedQuoteTail(character);
			}
			
			if (CharacterAnalyzer.COMMENT_HEAD == characterType) {
				expectedCommentTail = characterAnalyzer.getExpectedCommentTail(character);
			}
			return isTokenDelimiter;
		}
		
		private void createToken(char[] characters) {
			if (nextTokenIsMeaningful) {
				createMeaningfulToken(characters);
			}
			else {
				createMeaninglessToken(characters);
			}
			nextTokenIsMeaningful = ! nextTokenIsMeaningful;
		}
		
		private void createMeaningfulToken(char[] characters) {
			int meaningfulTokenLength = tokenLength - trailingCharCount;
			String token = new String (characters, tokenStartPosition, meaningfulTokenLength);
			listener.newToken(token, true);

			tokenStartPosition += meaningfulTokenLength;
			tokenLength = trailingCharCount;
			trailingCharCount = 0;
		}
		
		private void createMeaninglessToken(char[] characters) {
			if (tokenLength > 0) {
				String token = new String (characters, tokenStartPosition, tokenLength);
				listener.newToken(token, false);
				tokenStartPosition += tokenLength;
				tokenLength = 0;
			}
		}

		public int tokenize (char[] characters, int offset, int nbReadable, boolean isLastChunk) {
			int lastPosition = offset + nbReadable;
			tokenStartPosition = offset - tokenLength;
			
			for (int i = offset ; i < lastPosition ; i++) {
				int characterType = getCharacterType(characters, i);
				
				if (this.isNextCharTokenDelimiter(characterType, characters[i])) {
					this.createToken(characters);
				}
				tokenLength++;
			}
			if (isLastChunk) {
				while (tokenLength > 0) {
					this.createToken(characters);
				}
			}
			return tokenLength;
		}

		private int getCharacterType(char[] characters, int i) {
			char c = characters[i];
			int characterType = characterAnalyzer.getCharacterType(c);
			
			if (expectedQuoteTail != 0) {
				if (c == expectedQuoteTail) {
					characterType = CharacterAnalyzer.SEPARATOR;
					if (! nextTokenIsMeaningful) {
						this.createToken(characters);
					}
					expectedQuoteTail = 0;
				}
				else {
					characterType = CharacterAnalyzer.LETTER;
				}
			}
			else if (expectedCommentTail != 0) {
				if (c != expectedCommentTail) {
					characterType = CharacterAnalyzer.SEPARATOR;
				}
				else {
					expectedCommentTail = 0;
				}
			}
			return characterType;
		}
	}
}
