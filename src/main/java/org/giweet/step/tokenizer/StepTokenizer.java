package org.giweet.step.tokenizer;

import java.io.IOException;
import java.io.Reader;

import org.giweet.step.StepToken;

public class StepTokenizer {
	private static final int DEFAULT_BUFFER_SIZE = 255;
	private final CharacterAnalyzer characterAnalyzer;
	private final TokenizerStrategy strategy;
	private final int bufferInitialCapacity;

	public StepTokenizer(TokenizerStrategy strategy) {
		this(strategy, new DefaultCharacterAnalyzer(), DEFAULT_BUFFER_SIZE);
	}

	public StepTokenizer(TokenizerStrategy strategy, int bufferInitialCapacity) {
		this(strategy, new DefaultCharacterAnalyzer(), bufferInitialCapacity);
	}

	public StepTokenizer(TokenizerStrategy strategy, CharacterAnalyzer characterAnalyzer) {
		this(strategy, characterAnalyzer, DEFAULT_BUFFER_SIZE);
	}

	public StepTokenizer(TokenizerStrategy strategy, CharacterAnalyzer characterAnalyzer, int bufferInitialCapacity) {
		if (bufferInitialCapacity == 0) {
			throw new IllegalArgumentException("buffer size must be greater than 0");
		}
		this.strategy = strategy;
		this.characterAnalyzer = characterAnalyzer;
		this.bufferInitialCapacity = bufferInitialCapacity;
	}
	
	public StepToken[] tokenize(String value) {
		DefaultStepTokenizerListener listener = new DefaultStepTokenizerListener(strategy);
		tokenize(value, listener);
		return listener.getStepTokens();
	}

	public void tokenize(String value, StepTokenizerListener listener) {
		TokenizerContext ctx = new TokenizerContext();
		ctx.listener = listener;
		ctx.tokenize(value, true);
	}

	public StepToken[] tokenize(Reader reader) throws IOException {
		DefaultStepTokenizerListener listener = new DefaultStepTokenizerListener(strategy);
		this.tokenize(reader, listener);
		return listener.getStepTokens();
	}

	public void tokenize(Reader reader, StepTokenizerListener listener) throws IOException {
		TokenizerContext ctx = new TokenizerContext();
		ctx.listener = listener;
		CharSequenceBuffer charSequenceBuffer = new CharSequenceBuffer(bufferInitialCapacity);
		
		while (charSequenceBuffer.load(reader)) {
			int remainingCharCount = ctx.tokenize(charSequenceBuffer, false);
			charSequenceBuffer.updateBuffer(remainingCharCount);
		}
		ctx.tokenize(charSequenceBuffer, true);
	}

	private static class CharSequenceBuffer implements CharSequence {
		
		private char [] buffer;
		private int length;
		
		public CharSequenceBuffer(int initialCapacity) {
			buffer = new char[initialCapacity];
		}
		
		public int length() {
			return length;
		}

		public char charAt(int index) {
			return buffer[index];
		}

		public CharSequence subSequence(int start, int end) {
			return new String(buffer, start, end - start);
		}
		
		public boolean load(Reader reader) throws IOException {
			int nbRead = reader.read(buffer, length, buffer.length - length);
			if (nbRead > 0) {
				length += nbRead;
			}
			return nbRead > -1;
		}
		
		public void updateBuffer(int nbReadable) {
			if (nbReadable > 0) {
				char[] newBuffer = buffer;
				if (nbReadable == buffer.length) {
					newBuffer = new char[buffer.length + nbReadable];
				}
				System.arraycopy(buffer, length - nbReadable, newBuffer, 0, nbReadable);
				buffer = newBuffer;
			}
			length = nbReadable;
		}
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
		
		private void createToken(CharSequence charSequence) {
			if (nextTokenIsMeaningful) {
				createMeaningfulToken(charSequence);
			}
			else {
				createMeaninglessToken(charSequence);
			}
			nextTokenIsMeaningful = ! nextTokenIsMeaningful;
		}
		
		private void createMeaningfulToken(CharSequence charSequence) {
			int meaningfulTokenLength = tokenLength - trailingCharCount;
			String token = charSequence.subSequence(tokenStartPosition, tokenStartPosition + meaningfulTokenLength).toString();
			listener.newToken(token, true);

			tokenStartPosition += meaningfulTokenLength;
			tokenLength = trailingCharCount;
			trailingCharCount = 0;
		}
		
		private void createMeaninglessToken(CharSequence charSequence) {
			if (tokenLength > 0) {
				String token = charSequence.subSequence(tokenStartPosition, tokenStartPosition + tokenLength).toString();
				listener.newToken(token, false);
				tokenStartPosition += tokenLength;
				tokenLength = 0;
			}
		}

		public int tokenize (CharSequence charSequence, boolean isLastChunk) {
			tokenStartPosition = 0;
			
			for (int i = tokenLength ; i < charSequence.length() ; i++) {
				int characterType = getCharacterType(charSequence, i);
				
				if (this.isNextCharTokenDelimiter(characterType, charSequence.charAt(i))) {
					this.createToken(charSequence);
				}
				tokenLength++;
			}
			if (isLastChunk) {
				while (tokenLength > 0) {
					this.createToken(charSequence);
				}
			}
			return tokenLength;
		}

		private int getCharacterType(CharSequence charSequence, int i) {
			char c = charSequence.charAt(i);
			int characterType = characterAnalyzer.getCharacterType(c);
			
			if (expectedQuoteTail != 0) {
				if (c == expectedQuoteTail) {
					characterType = CharacterAnalyzer.SEPARATOR;
					if (! nextTokenIsMeaningful) {
						this.createToken(charSequence);
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
