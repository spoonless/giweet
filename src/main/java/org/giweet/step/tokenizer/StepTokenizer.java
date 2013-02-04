package org.giweet.step.tokenizer;

import java.io.IOException;
import java.io.Reader;

import org.giweet.step.StaticStepToken;
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
		CharSequenceIterator charSequenceIterator = new CharSequenceIterator(value, characterAnalyzer);
		
		StepTokenParser stepTokenParser = new MeaninglessStepTokenParser(charSequenceIterator, 0);
		
		do {
			StepTokenParser nextStepTokenParser = stepTokenParser.parse();
			StepToken stepToken = stepTokenParser.getStepToken();
			if (stepToken.isMeaningful() || stepToken.toString().length() > 0) {
				listener.newToken(stepToken.toString(), stepToken.isMeaningful());
			}
			stepTokenParser = nextStepTokenParser;
		} while (stepTokenParser != null);
		
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
	
	
	private static interface StepTokenParser {
		
		StepTokenParser parse();
		
		StepToken getStepToken();
	}
	
	private static class MeaninglessStepTokenParser implements StepTokenParser {
		
		private final CharSequenceIterator charSequenceIterator;
		private int firstCharsToIgnore;
		
		public MeaninglessStepTokenParser(CharSequenceIterator charSequenceIterator, int firstCharsToIgnore) {
			this.charSequenceIterator = charSequenceIterator;
			this.firstCharsToIgnore = firstCharsToIgnore;
		}

		public StepTokenParser parse() {
			charSequenceIterator.popNext(firstCharsToIgnore);
			while (charSequenceIterator.hasNext()) {
				int type = charSequenceIterator.getNextType();
				if ((type & CharacterAnalyzer.QUOTE_HEAD) == CharacterAnalyzer.QUOTE_HEAD) {
					char quoteHead = charSequenceIterator.getNext();
					charSequenceIterator.popNext();
					return new QuotationStepTokenParser(charSequenceIterator, quoteHead);
				}
				else if ((type & CharacterAnalyzer.SEPARATOR) > 0 || (type & CharacterAnalyzer.SEPARATOR_IF_LEADING_OR_TRAILING) > 0) {
					charSequenceIterator.popNext();
				}
				else {
					return new MeaningFulStepTokenParser(charSequenceIterator);
				}
			}
			return null;
		}
		
		public StepToken getStepToken() {
			return new StaticStepToken(charSequenceIterator.subSequence().toString(), false);
		}
	}
	
	private static class QuotationStepTokenParser implements StepTokenParser {
		
		private final CharSequenceIterator charSequenceIterator;
		private final char quoteHead;
		private int quoteHeadCount = 1;
		
		public QuotationStepTokenParser(CharSequenceIterator charSequenceIterator, char quoteHead) {
			this.charSequenceIterator = charSequenceIterator;
			this.quoteHead = quoteHead;
		}

		public StepTokenParser parse() {
			char expectedQuoteTail = charSequenceIterator.getExpectedQuoteTail(quoteHead);
			while (charSequenceIterator.hasNext()) {
				char c = charSequenceIterator.getNext();
				if (c == expectedQuoteTail) {
					--quoteHeadCount;
					if (quoteHeadCount == 0) {
						return new MeaninglessStepTokenParser(charSequenceIterator, 1);
					}
				}
				else if (c == quoteHead) {
					++quoteHeadCount;
				}
				charSequenceIterator.popNext();				
			}
			// FIXME something to throw here! No quote tail found
			return null;
		}
		
		public StepToken getStepToken() {
			return new StaticStepToken(charSequenceIterator.subSequence().toString(), true);
		}
		
	}
	
	private static class MeaningFulStepTokenParser implements StepTokenParser {
		
		private final CharSequenceIterator charSequenceIterator;
		private int trailingSeparators;

		public MeaningFulStepTokenParser(CharSequenceIterator charSequenceIterator) {
			this.charSequenceIterator = charSequenceIterator;
		}
		
		public StepTokenParser parse() {
			while (charSequenceIterator.hasNext()) {
				int type = charSequenceIterator.getNextType();
				if ((type & CharacterAnalyzer.SEPARATOR) > 0) {
					charSequenceIterator.pushBack(trailingSeparators);
					return new MeaninglessStepTokenParser(charSequenceIterator, trailingSeparators);
				}
				else if ((type & CharacterAnalyzer.SEPARATOR_IF_TRAILING) > 0) {
					trailingSeparators++;
				}
				else {
					trailingSeparators = 0;
				}
				charSequenceIterator.popNext();
			}
			// FIXME error prone for reentrant call. The end of the char sequence does not mean
			// it is the end of the stream to parse
			charSequenceIterator.pushBack(trailingSeparators);
			return new MeaninglessStepTokenParser(charSequenceIterator, trailingSeparators);
		}

		public StepToken getStepToken() {
			return new StaticStepToken(charSequenceIterator.subSequence().toString(), true);
		}
	}

	private static class CharSequenceIterator {
		
		private CharSequence charSequence;
		private CharacterAnalyzer characterAnalyzer;
		private int currentPosition;
		private int startPosition;
		
		public CharSequenceIterator(CharSequence charSequence, CharacterAnalyzer characterAnalyzer) {
			this.charSequence = charSequence;
			this.characterAnalyzer = characterAnalyzer;
		}
		
		public boolean hasNext() {
			return currentPosition < charSequence.length();
		}
		
		public char getNext() {
			return charSequence.charAt(currentPosition);
		}
		
		public void popNext() {
			++currentPosition;
		}

		public void popNext(int firstCharsToByPass) {
			currentPosition += firstCharsToByPass;
		}

		public int getNextType() {
			return characterAnalyzer.getCharacterType(charSequence.charAt(currentPosition));
		}
		
		public void pushBack(int i) {
			currentPosition = Math.max(startPosition, currentPosition - i);
		}
		
		public CharSequence subSequence() {
			CharSequence subSequence = charSequence.subSequence(startPosition, currentPosition);
			startPosition = currentPosition;
			return subSequence;
		}
		
		public char getExpectedQuoteTail(char quoteHead) {
			return characterAnalyzer.getExpectedQuoteTail(quoteHead);
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
