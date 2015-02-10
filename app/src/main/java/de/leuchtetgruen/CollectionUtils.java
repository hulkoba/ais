package de.leuchtetgruen;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.leuchtetgruen.F;

/**
 * This class extends the basic collection class with a couple of useful methods partially 
 * implementing concepts of functional programming.
 * 
 * @author Hannes Walz<info@leuchtetgruen.de>
 *
 * @param <T>
 */
public class CollectionUtils<T> extends AbstractCollection<T> {

	private Collection<T> collection;
	
	/**
	 * Copy constructor. Pass any collection in and get basically the same
	 * collection only with the extended functionality of this class.
	 * 
	 * @param collection - the collection you want to extend
	 */
	public CollectionUtils(Collection<T> collection) {
		this.collection = collection;
	}
	
	/**
	 * Creates an extended collection that is empty
	 */
	public CollectionUtils() {
		this.collection = new ArrayList<T>();
	}
	
	@Override
	public Iterator<T> iterator() {
		return collection.iterator();
	}

	@Override
	public int size() {
		return collection.size();
	}
	
	/**
	 * Joins the elements of this collection. If you want to get
	 * anything useful from this method you have to implement toString
	 * on the elements of this collection.
	 * 
	 * @param delimiter - The delimiter to be inserted between the elements
	 * @return A joined string containing each elements toString-representation, seperated by the given delimiter
	 */
	public String join(String delimiter) {
		StringBuilder build = new StringBuilder("");
		for (T element : this) {
			build.append(element);
			build.append(delimiter);
		}
		if (build.length() > 0) {
			build.replace(build.length() - delimiter.length(), build.length(), ""); // remove the last delimiter
		}
		return build.toString();
	}
	
	/**
	 * Basically the same as a for-Loop. Pass a runner to be run for each element of the collection.
	 * The difference is that you should not change the values of variables that are not part of the runner.
	 * 
	 * @param runner
	 */
	public void each(F.Runner<T> runner) {
		F.each(this, runner);
	}
	
	/**
	 * Returns a filtered copy of this collection, that contains all elements that pass the test that is given
	 * as the decider parameter.
	 * 
	 * @param decider
	 * @return a filtered copy of this collection
	 */
	public CollectionUtils<T> filter(F.Decider<T> decider) {
		return new CollectionUtils<T>(F.filter(this, decider));
	}
	
	/**
	 * Returns the first element of the collection that passes the test that is given as the decider parameter.
	 * 
	 * @param decider
	 * @return the first element that passes the test.
	 */
	public T find(F.Decider<T> decider) {
		return F.find(this, decider);
	}
	
	/**
	 * Returns a hashmap that is grouped by the values returned by the mapper for each element of the collections.
	 * E.g. you could have a hash map that contains even elements and odd elements for a list of Integers. Therefore
	 * you would have to write a mapper that maps from Integer to maybe String ("odd","even"). You would get a hash
	 * that contains all odd numbers with the key "even" and all odd numbers with the key "odd".
	 * 
	 * @param mapper
	 * @return a hashmap that is grouped by the return values of the mapper.
	 */
	public <U> HashMap<U, List<T>> group(F.Mapper<T, U> mapper) {
		return F.group(this, mapper);
	}
	
	/**
	 * Returns true if all elements of this collections pass the decider test. Otherwise it returns false. 
	 * 
	 * @param decider
	 * @return true if all elements pass the decider.
	 */
	public boolean isValidForAll(F.Decider<T> decider) {
		return F.isValidForAll(this, decider);
	}
	
	/**
	 * Returns true if any element of this collection passes the decider test. Otherwise it returns false.
	 * 
	 * @param decider
	 * @return true if any element passes the decider.
	 */
	public boolean isValidForAny(F.Decider<T> decider) {
		return F.isValidForAny(this, decider);
	}
	
	/**
	 * Converts the collection using the mapper. Return the converted value of each element in the mapper.
	 * 
	 * @param mapper
	 * @return a converted copy of the collection using the mapper.
	 */
	public <U> CollectionUtils<U> map(F.Mapper<T, U> mapper) {
		return new CollectionUtils<U>(F.map(this, mapper));
	}
	
	/**
	 * Returns the maximum element of the list. Comperation is done using the comperator element of the list.
	 * 
	 * @param comparator
	 * @return the maximum element of the list
	 */
	public T max(F.Comparator<T> comparator) {
		return F.max(this, comparator);
	}
	
	/**
	 * Returns the minimum element of the list. Comperation is done using the comperator element of the list.
	 * 
	 * @param comparator
	 * @return the minimum of the list
	 */
	public T min(F.Comparator<T> comparator) {
		return F.min(this, comparator);
	}
	
	/**
	 * Reduces the collection to a single value. The reducer is called for each element of the collection with the
	 * result of the reduction so far (as the memo). This way you could calculate the sum of all elements in the list.
	 * 
	 * 
	 * @param reducer
	 * @param memo the initial memo passed to the first reducer-call
	 * @return The reduction result of this list
	 */
	public <U> U reduce(F.Reducer<T, U> reducer, U memo) {
		return F.reduce(this, reducer, memo);
	}
	
	/**
	 * The opposite function of {@link filter}.
	 * 
	 * @param decider
	 * @return
	 */
	public CollectionUtils<T> reject(F.Decider<T> decider) {
		return new CollectionUtils<T>(F.reject(this, decider));
	}

	
	/**
	 * @return the collection as a set
	 */
	public Set<T> toSet() {
		return new HashSet<T>(this);
	}
	
	/**
	 * @return the collection as a list
	 */
	public List<T> toList() {
		return new ArrayList<T>(this);
	}
	
	/**
	 * @return a copy of the list that does not contain any null elements.
	 */
	public CollectionUtils<T> withoutEmpty() {
		return reject(new F.Decider<T>() {
			public boolean decide(T o) {
				return (o==null);
			}
		});
	}
	
}
