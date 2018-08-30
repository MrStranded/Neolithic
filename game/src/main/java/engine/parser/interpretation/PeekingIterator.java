package engine.parser.interpretation;

import engine.parser.tokenization.Token;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class PeekingIterator<T> implements Iterator {

	private List<T> list;

	int currentPosition = 0;
	int length = 0;

	public PeekingIterator(List<T> list) {
		this.list = list;
		length = list.size();
	}

	public T peek() {
		return list.get(currentPosition);
	}

	@Override
	public boolean hasNext() {
		return (currentPosition < length);
	}

	@Override
	public T next() {
		return list.get(currentPosition++);
	}

	/**
	 * Removing is not supported here, because it is not needed.
	 */
	@Override
	public void remove() {
		return;
	}

	/**
	 * forEachRemaining is not supported here, because it is not needed.
	 * @param action
	 */
	@Override
	public void forEachRemaining(Consumer action) {

	}
}
