package edu.scripps.yates.utilities.collections;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.iterators.ArrayListIterator;

public class ObservableArrayListIterator<T> extends ArrayListIterator {
	private final Set<CollectionObserver<T>> collectionObservers = new HashSet<CollectionObserver<T>>();

	public ObservableArrayListIterator(ArrayList<T> arrayList, CollectionObserver<T> collectionObserver) {
		super(arrayList);
		collectionObservers.add(collectionObserver);
	}

	public ObservableArrayListIterator(ArrayList<T> arrayList, Set<CollectionObserver<T>> collectionObservers) {
		super(arrayList);
		this.collectionObservers.addAll(collectionObservers);
	}

	@Override
	public void remove() {
		for (final CollectionObserver<T> collectionListener : collectionObservers) {

			final T objectAtIndex = (T) Array.get(array, index);
			collectionListener.remove(objectAtIndex);
		}
		super.remove();
	}

}
