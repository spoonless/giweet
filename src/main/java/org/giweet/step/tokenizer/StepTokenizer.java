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
		TokenizerContext ctx = new TokenizerContext(characterAnalyzer);
		ctx.listener = listener;
		ctx.tokenize(value, true);
	}

	public StepToken[] tokenize(Reader reader) throws IOException {
		DefaultStepTokenizerListener listener = new DefaultStepTokenizerListener(strategy);
		this.tokenize(reader, listener);
		return listener.getStepTokens();
	}

	public void tokenize(Reader reader, StepTokenizerListener listener) throws IOException {
		TokenizerContext ctx = new TokenizerContext(characterAnalyzer);
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
	
	private static class QuotationBoundary {
		private char quoteHead;
		private char quoteTail;
		private int quoteTailCount;
		
		public QuotationBoundary(char quoteHead, char quoteTail) {
			this.quoteHead = quoteHead;
			this.quoteTail = quoteTail;
			this.quoteTailCount = 1;
		}
		
		public boolean isQuoteTail(char character) {
			if (character == quoteTail) {
				quoteTailCount--;
			}
			else if (character == quoteHead) {
				quoteTailCount++;
			}
			return quoteTailCount == 0;
		}
	}
	
	private static class TokenizerContext {
		private final CharacterAnalyzer characterAnalyzer;
		private int tokenStartPosition;
		private int tokenLength;
		private int trailingSeparatorCount;
		private boolean nextTokenIsMeaningful = false;
		private StepTokenizerListener listener;
		private QuotationBoundary quotationBoundary;
		
		public TokenizerContext (CharacterAnalyzer characterAnalyzer) {
			this.characterAnalyzer = characterAnalyzer;
		}
		
		public int tokenize(CharSequence charSequence, boolean isLastChunk) {
			tokenStartPosition = 0;
			
			for (int i = tokenLength ; i < charSequence.length() ; i++) {
				char character = charSequence.charAt(i);
				int characterType = getCharacterType(charSequence, character);
				if (this.isNextCharTokenDelimiter(characterType, character)) {
					this.createToken(charSequence);
				}
				tokenLength++;
			}
			if (isLastChunk) {
				if (quotationBoundary != null) {
					// TODO What are we suppose to do here? 
					// It means that the sequence is completely parsed but no
					// quote tail found
				}
				while (tokenLength > 0) {
					this.createToken(charSequence);
				}
			}
			return tokenLength;
		}

		private int getCharacterType(CharSequence charSequence, char currentChar) {
			int characterType = characterAnalyzer.getCharacterType(currentChar);
			
			if (quotationBoundary != null) {
				if (quotationBoundary.isQuoteTail(currentChar)) {
					characterType = CharacterAnalyzer.SEPARATOR;
					if (! nextTokenIsMeaningful) {
						this.createToken(charSequence);
					}
					quotationBoundary = null;
				}
				else {
					characterType = CharacterAnalyzer.LETTER;
				}
			}
			return characterType;
		}

		private boolean isNextCharTokenDelimiter (int characterType, char character) {
			boolean isTokenDelimiter = false;
			
			if (nextTokenIsMeaningful) {
				if ((CharacterAnalyzer.SEPARATOR & characterType) > 0) {
					isTokenDelimiter = true;
				}
				else if ((CharacterAnalyzer.SEPARATOR_IF_TRAILING & characterType) > 0) {
					trailingSeparatorCount++;
				}
				else {
					trailingSeparatorCount = 0;
				}
			}
			else if ((CharacterAnalyzer.LETTER & characterType) > 0) {
				isTokenDelimiter = true;
			}
			else if (CharacterAnalyzer.QUOTE_HEAD == characterType) {
				char expectedQuoteTail = characterAnalyzer.getExpectedQuoteTail(character);
				if (expectedQuoteTail != 0) {
					quotationBoundary = new QuotationBoundary(character, expectedQuoteTail);
				}
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
			int meaningfulTokenLength = tokenLength - trailingSeparatorCount;
			String token = charSequence.subSequence(tokenStartPosition, tokenStartPosition + meaningfulTokenLength).toString();
			listener.newToken(token, true);

			tokenStartPosition += meaningfulTokenLength;
			tokenLength = trailingSeparatorCount;
			trailingSeparatorCount = 0;
		}
		
		private void createMeaninglessToken(CharSequence charSequence) {
			if (tokenLength > 0) {
				String token = charSequence.subSequence(tokenStartPosition, tokenStartPosition + tokenLength).toString();
				listener.newToken(token, false);
				tokenStartPosition += tokenLength;
				tokenLength = 0;
			}
		}
	}
}
