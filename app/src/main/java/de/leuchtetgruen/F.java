package de.leuchtetgruen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.util.ListIterator;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


/**
 * This class provides some functional programming to collection/array handling in
 * java allowing you to write much more concise code. 
 *
 * @author Hannes Walz<info@leuchtetgruen.de>
 *
 *
 */
public class F {
	// INTERFACES


	/**
	 * Runners are used to be run on each element of a set of elements
	 * 
	 * @author Hannes Walz<info@leuchtetgruen.de>
	 *
	 * @param <T>
	 *
	 * 
	 */
	public static interface Runner<T> {
		/**
		 * This method will be called for each element. Please make sure
		 * that you dont assume sequential execution of elements.
		 * 
		 * @param o - the current element
		 */
		public void run(T o);
	};

	/**
	 * Runners are used to be run on each key value pair of the hash.
	 * 
	 * @author Hannes Walz<info@leuchtetgruen.de>
	 *
	 * @param <T>
	 * @param <U>
	 *
	 * 
	 */
	public static interface HashRunner<T,U> {
		/**
		 * This method will be called for each element. Please make sure
		 * that you dont assume sequential execution of elements.
		 * 
		 * @param k
		 * @param v
		 */
		public void run(T k, U v);
	}

	/**
	 * A mapper converts elements. E.g. a mapper could calculate the double of each value in an integer set.
	 * 
	 * @author Hannes Walz<info@leuchtetgruen.de>
	 *
	 * @param <T>
	 * @param <U>
	 */
	public static interface Mapper<T,U> {
		/**
		 * This method is called for each item of a set.
		 * 
		 * @param o
		 * @return
		 */
		public U map(T o);
	}

	/**
	 * A lazymapper is used to create a lazylist from a mapping function.
	 * 
	 * @author Hannes Walz<info@leuchtetgruen.de>
	 *
	 * @param <T>
	 * @param <U>
	 */
	public static interface LazyMapper<T,U> {
		public U map(T o, LazyList<U> l);
	}

	/**
	 * Reduces a set of elements to a single one that has not necessarily to be of the
	 * same class. 
	 * 
	 * @author Hannes Walz <info@leuchtetgruen.de>
	 *
	 * @param <T>
	 * @param <U>
	 */
	public static interface Reducer<T,U> {
		/**
		 * Called for each element of the set with the result of the computations done
		 * so far 
		 * 
		 * @param memo - result of the computations done so far
		 * @param o - the current element
		 * @return
		 */
		public U reduce(U memo, T o);
	}

	/**
	 * A decider returns true or false for an element
	 * 
	 * @author Hannes Walz <info@leuchtetgruen.de>
	 *
	 * @param <T>
	 */
	public static interface Decider<T> {
		/**
		 * Called for each element of the set. Should return true or false
		 * 
		 * @param o
		 * @return
		 */
		public boolean decide(T o);
	}


	/**
	 * Interface used to find min/max elements of a set. 
	 * 
	 * @author Hannes Walz <info@leuchtetgruen.de>
	 *
	 * @param <T>
	 * 
	 */
	public static interface Comparator<T> {
		/**
		 * Should compare both elements o1 and o2 and
		 * return 1 if o1 is greater, -1 of o2 is greater
		 * and 0 if both are equal.
		 * 
		 * @param o1
		 * @param o2
		 * @return
		 */
		public int compare(T o1, T o2);
	}

	/**
	 * A datasource for a lazy list
	 * 
	 * @author Hannes Walz<info@leuchtetgruen.de>
	 *
	 * @param <T>
	 */
	public static interface LazyListDataSource<T> {
		public T get(int index, LazyList<T> ll);
		public int size();
		public boolean shouldCache();
	}


	// EACH
	/**
	 * Run for each element of the iterable c, using the runner r
	 * 
	 * @param c
	 * @param r
	 */
	public static <T> void each(Iterable<T> c, Runner<T> r) {
		for (T o : c) {
			r.run(o);
		}
	}

	/**
	 * Run for each element of the array arr, using the runner r
	 * 
	 * @param arr
	 * @param r
	 */
	public static <T> void each(T[] arr, Runner<T> r) {
		for (T o : arr) {
			r.run(o);
		}
	}

	/**
	 * Run for each element of the hashmap map using the hashrunner r
	 * 
	 * @param map
	 * @param r
	 */
	public static <T,U> void each(HashMap<T, U> map, HashRunner<T,U> r) {
		Set<T> s = map.keySet();
		for (T k : s) {
			r.run(k, map.get(k));
		}
	}

	// MAP
	/**
	 * Convert each element of the iterable c using the mapper r
	 * 
	 * @param c
	 * @param r
	 * @return
	 */
	public static <T,U> List<U> map(Iterable<T> c, Mapper<T,U> r) {
		ArrayList<U> ret = new ArrayList<U>();
		for (T o : c) {
			ret.add(r.map(o));
		}
		return ret;
	}

	/**
	 * Convert each element of the array arr using the mapper r
	 * 
	 * @param arr
	 * @param r
	 * @return
	 */
	public static <T,U> List<U> map(T[] arr, Mapper<T,U> r) {
		ArrayList<U> ret = new ArrayList<U>();
		for (T o : arr) {
			ret.add(r.map(o));
		}
		return ret;
	}



	// REDUCE
	/**
	 * Reduce the iterable c to a single value using the reducer r. Initialize
	 * it with the memo parameter.
	 * 
	 * @param c
	 * @param r
	 * @param memo
	 * @return
	 */
	public static <T,U> U reduce(Iterable<T> c, Reducer<T,U> r, U memo) {
		for (T o : c) {
			memo = r.reduce(memo, o);
		}
		return memo;
	}

	/**
	 * Reduce the array arr to a single value using the reducer r. Initialize
	 * it with the memo parameter.
	 * 
	 * @param arr
	 * @param r
	 * @param memo
	 * @return
	 */
	public static <T,U> U reduce(T[] arr, Reducer<T,U> r, U memo) {
		for (T o : arr) {
			memo = r.reduce(memo, o);
		}
		return memo;
	}

	// FILTER
	/**
	 * Filter the iterable c with the decider r.
	 * 
	 * @param c
	 * @param r
	 * @return
	 */
	public static <T> List<T> filter(Iterable<T> c,Decider<T> r) {
		ArrayList<T> ret = new ArrayList<T>();
		for (T o: c) {
			if (r.decide(o)) ret.add(o);
		}
		return ret;
	}

	/**
	 * Filter the array arr with the decider r. 
	 * @param arr
	 * @param r
	 * @return
	 */
	public static <T> T[] filter(T[] arr, Decider<T> r) {
		ArrayList<T> ret = new ArrayList<T>();
		for (T o: arr) {
			if (r.decide(o)) ret.add(o);
		}
		@SuppressWarnings("unchecked")
		T[] array = (T[]) new Object[ret.size()];
		return ret.toArray(array);
	}

	// FIND
	/**
	 * Find the first element in the iterable c that passes the test
	 * in the decider r.
	 * 
	 * @param c
	 * @param r
	 * @return
	 */
	public static <T> T find(Iterable<T> c, Decider<T> r) {
		for (T o: c) {
			if (r.decide(o)) return o;
		}
		return null;
	}

	/**
	 * Find the first element in the array arr that passes the test
	 * in the decider r.
	 *  
	 * @param arr
	 * @param r
	 * @return
	 */
	public static <T> T find(T[] arr, Decider<T> r) {
		for (T o: arr) {
			if (r.decide(o)) return o;
		}
		return null;
	}

	// REJECT
	/**
	 * Opposite of filter.
	 * 
	 * @param c
	 * @param r
	 * @return
	 */
	public static <T> List<T> reject(Iterable<T> c,Decider<T> r) {
		ArrayList<T> ret = new ArrayList<T>();
		for (T o: c) {
			if (!r.decide(o)) ret.add(o);
		}
		return ret;
	}

	/**
	 * Opposite of filter.
	 * 
	 * @param arr
	 * @param r
	 * @return
	 */
	public static <T> T[] reject(T[] arr, Decider<T> r) {
		ArrayList<T> ret = new ArrayList<T>();
		for (T o: arr) {
			if (!r.decide(o)) ret.add(o);
		}
		@SuppressWarnings("unchecked")
		T[] array = (T[]) new Object[ret.size()];
		return ret.toArray(array);
	}

	// ISVALIDFORALL
	/**
	 * Returns true if all elements in the iterable c pass the test defined in the decider r
	 * 
	 * @param c
	 * @param r
	 * @return
	 */
	public static <T> boolean isValidForAll(Iterable<T> c, Decider<T> r) {
		boolean all = true;
		for (T o: c) {
			all = all && r.decide(o);
		}
		return all;
	}

	/**
	 * Returns true if all elements in the array arr pass the test defined in the decider r
	 * 
	 * @param arr
	 * @param r
	 * @return
	 */
	public static <T >boolean isValidForAll(T[] arr, Decider<T> r) {
		boolean all = true;
		for (T o: arr) {
			all = all && r.decide(o);
		}
		return all;
	}

	// ISVALIDFORANY
	/**
	 * Returns true if any element in the iterable c passes the test defined in the decider r
	 * 
	 * @param c
	 * @param r
	 * @return
	 */
	public static <T> boolean isValidForAny(Iterable<T> c, Decider<T> r) {
		boolean all = false;
		for (T o: c) {
			all = all || r.decide(o);
		}
		return all;
	}

	/**
	 * Returns true if any element in the array arr passes the test defined in the decider r
	 * 
	 * @param arr
	 * @param r
	 * @return
	 */
	public static <T> boolean isValidForAny(T[] arr, Decider<T> r) {
		boolean all = false;
		for (T o: arr) {
			all = all || r.decide(o);
		}
		return all;
	}

	// COUNTVALIDENTRIES
	/**
	 * Counts the number of elements in the iterable c that pass the test defined in the decider r
	 * 
	 * @param c
	 * @param r
	 * @return
	 */
	public static <T> int countValidEntries(Iterable<T> c, Decider<T> r) {
		int count = 0;
		for (T o : c) {
			if (r.decide(o)) count++;
		}
		return count;
	}

	/**
	 * Counts the number of elements in the array arr that pass the test defined in the decider r
	 * 
	 * @param arr
	 * @param r
	 * @return
	 */
	public static <T> int countValidEntries(T[] arr, Decider<T> r) {
		int count = 0;
		for (T o : arr) {
			if (r.decide(o)) count++;
		}
		return count;
	}

	// SORT
	/**
	 * Passes the list c to Collections.sort, thus does not return a copy of it but sorts the original passed list
	 * and returns it.
	 * 
	 * @param c
	 * @param r
	 * @return
	 */
	public static <T> List<T> sortWithoutCopy(List<T> c, java.util.Comparator<Object> r) {
		Collections.sort(c, r);
		return c;
	}

	public static <T> T[] sortWithoutCopy(T[] arr, java.util.Comparator<Object> r) {
		T[] copy = arr.clone();
		Arrays.sort(copy, r);
		return copy;
	}

	// MIN
	/**
	 * Returns the minimum value of the iterable c using the comparator r.
	 * 
	 * @param c
	 * @param r
	 * @return
	 */
	public static <T> T min(Iterable<T> c, final Comparator<T> r) {
		T min = null;
		return reduce(c, new Reducer<T,T>() {
			public T reduce(T memo, T o) {
				if (memo == null) return o;
				int result = r.compare(o, memo); 
				if (result < 0) {
					return o;
				}
				else {
					return memo;
				}
			}
		}, min);
	}


	/**
	 * Returns the minimum value of the array arr using the comparator r.
	 * 
	 * @param arr
	 * @param r
	 * @return
	 */
	public static <T> T min(T[] arr, final Comparator<T> r) {
		T min = null;
		return reduce(arr, new Reducer<T,T>() {
			public T reduce(T memo, T o) {
				if (memo == null) return o;
				int result = r.compare(o, memo); 
				if (result < 0) {
					return o;
				}
				else {
					return memo;
				}
			}
		}, min);
	}

	// MAX
	/**
	 * Returns the maximum value of the iterable c using the comparator r.
	 * 
	 * @param c
	 * @param r
	 * @return
	 */
	public static <T> T max(Iterable<T> c, final Comparator<T> r) {
		T max = null;
		return reduce(c, new Reducer<T,T>() {
			public T reduce(T memo, T o) {
				if (memo == null) return o;
				int result = r.compare(o, memo); 
				if (result > 0) {
					return o;
				}
				else {
					return memo;
				}
			}
		}, max);
	}

	/**
	 * Returns the maximum value of the array arr using the comparator r.
	 * 
	 * @param arr
	 * @param r
	 * @return
	 */
	public static <T> T max(T[] arr, final Comparator<T> r) {
		T max = null;
		return reduce(arr, new Reducer<T,T>() {
			public T reduce(T memo, T o) {
				if (memo == null) return o;
				int result = r.compare(o, memo); 
				if (result > 0) {
					return o;
				}
				else {
					return memo;
				}
			}
		}, max);
	}

	// GROUP
	/**
	 * Groups the iterable c by the results of the mapper. So if you want
	 * to have integer elements sorted by odd/even, you could use this method
	 * passing a mapper returning "odd" or "even" for the Integer values passed in.
	 * 
	 * @param c
	 * @param r
	 * @return
	 */
	public static <T, U> HashMap<U, List<T>> group(Iterable<T> c, Mapper<T,U> r) {
		HashMap<U, List<T>> ret = new HashMap<U, List<T>>();
		for (T o: c) {
			U mapped = r.map(o);
			ArrayList<T> list = (ArrayList<T>) ret.get(mapped);
			if (list==null) {
				list = new ArrayList<T>();
			}
			list.add(o);
			ret.put(mapped, list);
		}

		return ret;
	}

	/**
	 * See group for Iterable 
	 * 
	 * @param arr
	 * @param r
	 * @return
	 */
	public static <T, U> HashMap<U, List<T>> group(T[] arr, Mapper<T,U> r) {
		HashMap<U, List<T>> ret = new HashMap<U, List<T>>();
		for (T o: arr) {
			U mapped = r.map(o);
			ArrayList<T> list = (ArrayList<T>) ret.get(mapped);
			if (list==null) {
				list = new ArrayList<T>();
			}
			list.add(o);
			ret.put(mapped, list);
		}

		return ret;
	}

	// LAZY SETS
	/**
	 * A lazy datastructure acts as a fully filled datastructure (say a list of numbers from 1 to 100)
	 * but actually the values are computed when used.
	 * 
	 * @author Hannes Walz<info@leuchtetgruen.de>
	 *
	 * @param <T>
	 */
	public static class LazyIndexedSet<T> implements Iterable<T>, Iterator<T> {
		private Mapper<Integer, T> mapper;
		private Integer index;

		/**
		 * @param mapper should map the index to a value
		 */
		public LazyIndexedSet(Mapper<Integer, T> mapper) {
			this.mapper = mapper;
			this.index 	= -1;
		}

		public boolean hasNext() {
			return (mapper.map(index + 1) != null);
		}

		public T next() {
			index++;
			return mapper.map(index);
		}

		public void remove() {
			// do nothing
		}

		public Iterator<T> iterator() {
			return this;
		}
	}


	/**
	 * See LazySet for an explanation what a lazy datastructure is.
	 * 
	 * @author Hannes Walz<info@leuchtetgruen.de>
	 *
	 * @param <T>
	 */
	public static class LazyList<T> implements List<T> {

		private LazyListDataSource<T> dataSource;
		private HashMap<Integer, T> hCache;
		private boolean shouldCache;

		public LazyList(LazyListDataSource<T> source) {
			this.shouldCache = source.shouldCache();
			if (shouldCache) {
				this.hCache = new HashMap<Integer, T>();
			}
			this.dataSource = source;			
		}


		public boolean add(T e) { return false; }
		public void add(int index, T element)  {}
		public boolean addAll(Collection<? extends T> c) { return false; }
		public boolean addAll(int index, Collection<? extends T> c) { return false; }
		public void clear() {}
		public boolean contains(final Object o1) {
			return F.isValidForAny(this, new Decider<T>() {
				public boolean decide(T o2) {
					return o1.equals(o2);
				}
			});
		}
		@SuppressWarnings("unchecked")
		public boolean containsAll(Collection<?> c) {
			return F.isValidForAll((Collection<T>) c, new Decider<T>() {
				public boolean decide(T o2) {
					return contains(o2);
				}
			});
		}
		public boolean equals(Object o) {
			return false;
		}
		public T get(int index) {
			if (shouldCache) {
				T ret = hCache.get(index);
				if (ret==null) {
					ret = dataSource.get(index, this);
					hCache.put(index, ret);
				}
				return ret;
			}
			else return dataSource.get(index, this);
		}

		public int hashCode() {
			// TODO implement
			return -1;
		}

		public int indexOf(Object o) {
			for (int i=0; i < size(); i++) {
				if (get(i).equals(o)) return i;
			}
			return -1;
		}

		public boolean isEmpty() {
			return (size() == 0);
		}

		// ListIterator stuff
		@SuppressWarnings("hiding")
		private class LazyListIterator<T>  implements ListIterator<T> {
			private int index;

			public void add(T e) {}

			public LazyListIterator() {
				this.index = -1;
			}

			public LazyListIterator(int startIndex) {
				this.index = startIndex;
			}

			public boolean hasNext() {
				return (index < (size() - 1));
			}

			public boolean hasPrevious() {
				return (index > 0);
			}

			@SuppressWarnings("unchecked")
			public T next() {
				index++;
				return (T) get(index);
			}

			public int nextIndex() {
				return (index + 1);
			}

			@SuppressWarnings("unchecked")
			public T previous() {
				index--;
				return (T) get(index);
			}

			public int previousIndex() {
				return (index - 1);
			}

			public void remove() {
				// do nothing
			}

			public void set(T e) {}


		}


		public Iterator<T> iterator() {
			return new LazyListIterator<T>();
		}

		public int lastIndexOf(Object o) {
			int found = -1;
			for (int i=0; i < size(); i++) {
				if (get(i).equals(o)) found = i;
			}
			return found;
		}

		public ListIterator<T> listIterator() {
			return new LazyListIterator<T>();
		}

		public ListIterator<T> listIterator(int startIndex) {
			return new LazyListIterator<T>(startIndex);
		}

		public T remove(int index) { return null; }
		public boolean remove(Object o) { return false; }		
		public boolean removeAll(Collection<?> c) { return false; }
		public boolean retainAll(Collection<?> c) { return false; }
		public T set(int index, T element) { return null; }

		public int size() {
			return dataSource.size();
		}

		public List<T> subList(int fromIndex, int toIndex) {
			final int from = fromIndex;
			final int to = toIndex;
			final LazyList<T> _this = this;
			// TODO Check indices and throw exceptions
			return new LazyList<T>(new LazyListDataSource<T>() {
				public T get(int i, LazyList<T> ll) {
					return _this.get(from + i);
				}

				public int size() {
					return (to - from);
				}

				public boolean shouldCache() {
					return shouldCache;
				}
			});
		}

		public List<T> toNonLazyList() {
			ArrayList<T> ret = new ArrayList<T>();
			for (T e: this) {
				ret.add(e);
			}
			return ret;
		}

		public Object[] toArray() {
			return toNonLazyList().toArray();
		}

		@SuppressWarnings("hiding")
		public <T> T[] toArray(T[] a) {
			return toNonLazyList().toArray(a);
		}

	}

	/**
	 * Maps an actual list to a lazy list using the given mapper. You can define wether the results of the mapping
	 * should be cached for future queries.
	 * 
	 * @param c
	 * @param mapper
	 * @param shouldCache
	 * @return
	 */
	public static <T,U> LazyList<U> lazyMap(final List<T> c, final Mapper<T,U> mapper, final boolean shouldCache) {
		return new LazyList<U>(new LazyListDataSource<U>() {
			public U get(int i, @SuppressWarnings("rawtypes") LazyList ll) {
				return mapper.map(c.get(i));
			}

			public int size() {
				return c.size();
			}

			public boolean shouldCache() {
				return shouldCache;
			}			
		});
	}

	public static <T> LazyList<T> infiniteLazyList(final LazyMapper<Integer, T> mapper) {
		return new LazyList<T>(new LazyListDataSource<T>() {

			@Override
			public T get(int index, LazyList<T> ll) {
				return mapper.map(index, ll);
			}

			@Override
			public int size() {
				return Integer.MAX_VALUE;
			}

			@Override
			public boolean shouldCache() {
				return true;
			}

		});
	}

	// CONCURRENCY
	public static Concurrency FixedThreadConcurrency = new Concurrency(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()), 2000);
	public static Concurrency CachedThreadConcurrency = new Concurrency(Executors.newCachedThreadPool(), 2000);

	/**
	 * This class is used for concurrent execution of methods. Keep in mind that for each item in a set a new thread has to be created.
	 * Therefore concurrency should only be used if the actual task to be performed on each element is time consuming or otherwise
	 * suitable for parallelization. 
	 * 
	 * Refer to the methods defined in F for finding out what their concurrent counterparts in this class do.
	 * 
	 * @author Hannes Walz<info@leuchtetgruen.de>
	 *
	 */
	public static class Concurrency {
		private ExecutorService ex;
		private int timeout;


		public Concurrency(ExecutorService ex, int timeout) {
			this.ex = ex;
			this.timeout = timeout;
		}

		public void finishService() {
			ex.shutdown();
			try {
				ex.awaitTermination(timeout, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO do stuff
			}
		}

		public <T> void each(final Iterable<T> c, final Runner<T> r) {
			for (final T o : c) {
				ex.submit(new Runnable() {
					public void run() {
						r.run(o);
					}
				});
			}
			finishService();
		}

		public <T,U> List<U> map(final Iterable<T> c, final Mapper<T,U> m) throws InterruptedException, ExecutionException {
			ArrayList<Future<U>> futures = new ArrayList<Future<U>>();

			// Step 1 - create threads
			for (final T o : c) {
				futures.add(ex.submit(new Callable<U>() {
					public U call() {
						return m.map(o);
					}
				}));
			}

			// Step 2 - collect futures
			ArrayList<U> ret = new ArrayList<U>();
			for (Future<U> f : futures) {
				ret.add(f.get());
			}
			finishService();
			return ret;
		}

		public <T> List<T> filter(final List<T> c, final Decider<T> d) throws InterruptedException, ExecutionException {
			ArrayList<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();

			// Step 1 - create threads
			for (final T o : c) {
				futures.add(ex.submit(new Callable<Boolean>() {
					public Boolean call() {
						return d.decide(o);
					}
				}));
			}

			// Step 2 - collect futures
			ArrayList<T> ret = new ArrayList<T>();
			for (int i=0; i < c.size(); i++) {
				if (futures.get(i).get()) ret.add(c.get(i));
			}
			finishService();
			return ret;
		}

		public <T> List<T> reject(final List<T> c, final Decider<T> d) throws InterruptedException, ExecutionException {
			ArrayList<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();

			// Step 1 - create threads
			for (final T o : c) {
				futures.add(ex.submit(new Callable<Boolean>() {
					public Boolean call() {
						return d.decide(o);
					}
				}));
			}

			// Step 2 - collect futures
			ArrayList<T> ret = new ArrayList<T>();
			for (int i=0; i < c.size(); i++) {
				if (!futures.get(i).get()) ret.add(c.get(i));
			}
			finishService();
			return ret;
		}

		public <T> LazyListDataSource<Future<T>> getConcurrentLazyListDataSource(final LazyListDataSource<T> dataSource) {
			return new LazyListDataSource<Future<T>>() {
				public Future<T> get(final int index, final LazyList<Future<T>> ll) {
					Callable<T> c = new Callable<T>() {
						public T call() {
							// AS WE ONLY HAVE A LAZY LIST OF FUTURES, WE CANNOT HAND OVER AN APPROPRIATE LAZY LIST - THEREFORE RECURSIVE CALLS ARE NOT ALLOWED HERE
							return dataSource.get(index, null);
						}
					};
					return ex.submit(c);
				}

				public int size() {
					return dataSource.size();
				}

				public boolean shouldCache() {
					return dataSource.shouldCache();
				}
			};
		}

		// After using this lazy list remember to call Concurrency.finishService();
		public <T> LazyList<Future<T>> getConcurrentLazyList(final LazyListDataSource<T> dataSource) {
			return new LazyList<Future<T>>(getConcurrentLazyListDataSource(dataSource));
		}

	}






	// UTILS
	/**
	 * This class contains a couple of utilities that can be used in combination
	 * with the methods and datastructures defined in F
	 * 
	 * @author Hannes Walz <info@leuchtetgruen.de>
	 *
	 */
	public static class Utils {

		/**
		 * A runner that System.out.printlns each element
		 * 
		 * @author Hannes Walz<info@leuchtetgruen.de>
		 *
		 * @param <T>
		 */
		public static class Printer<T> implements Runner<T> {
			public void run(T o) {
				System.out.println(o);
			}
		}

		/**
		 * Prints all elements in the iterable
		 * 
		 * @param c
		 */
		public static <T> void print(Iterable<T> c) {
			each(c, new Printer<T>());
		}

		/**
		 * Prints all elements in the iterable
		 * 
		 * @param arr
		 */
		public static <T> void print(T[] arr) {
			each(arr, new Printer<T>());
		}

		public static interface GroupIterator {
			public void onNewGroup(Object k);
			public void onNewEntry(Object v);
		}

		/**
		 * Prints a group
		 */
		public static GroupIterator groupPrinter = new GroupIterator() {

			@Override
			public void onNewGroup(Object k) {
				System.out.println(k);
			}

			@Override
			public void onNewEntry(Object v) {
				System.out.println("\t" + v);
			}
		}; 

		public static <T, U> void iterateOverGroup(HashMap<T, List<U>> group, final GroupIterator i) {
			each(group, new HashRunner<T,List<U>>() {
				public void run(T k, List<U> v) {
					i.onNewGroup(k);
					List<U> l = (List<U>) v;
					each(l, new Runner<U>() {
						public void run(U o) {
							i.onNewEntry(o);
						}						
					});
				}
			});
		}

		public static int COMPARATOR_FIRST_IS_GREATER 	= -1;
		public static int COMPARATOR_BOTH_ARE_EQUAL		= 0;
		public static int COMPARATOR_SECOND_IS_GREATER 	= 1;

		/**
		 * Can be used for implementing Comparators.
		 * 
		 * @param i1
		 * @param i2
		 * @return
		 */
		public static int intCompare(int i1, int i2) {
			if (i1==i2) return 0;
			return (i1 > i2) ? 1 : -1;
		}

		/**
		 * Can be used for implementing Comparators.
		 * 
		 * @param d1
		 * @param d2
		 * @return
		 */
		public static int doubleCompare(double d1, double d2) {
			if (d1==d2) return 0;
			return (d1 > d2) ? 1 : -1;
		}

		/**
		 * Can be used for implementing Comparators.
		 * 
		 * @param l1
		 * @param l2
		 * @return
		 */
		public static int longCompare(long l1, long l2) {
			if (l1==l2) return 0;
			return (l1 > l2) ? 1 : -1;
		}

		/**
		 * Can be used for implementing Comparators.
		 * 
		 * @param f1
		 * @param f2
		 * @return
		 */
		public static int floatCompare(float f1, float f2) {
			if (f1==f2) return 0;
			return (f1 > f2) ? 1 : -1;
		}

		public static Collection<Integer> indexSet(Collection<?> c) {
			ArrayList<Integer> l = new ArrayList<Integer>();
			for (int i=0; i< c.size(); i++) {
				l.add(i);
			}
			return l;
		}

		// Special Lazy sets and lists
		/**
		 * A lazy set that "contains" all integers in a range
		 * 
		 * @author Hannes Walz<info@leuchtetgruen.de>
		 *
		 */
		public static class LazyIntegerSet extends LazyIndexedSet<Integer> {
			public LazyIntegerSet(final int from, final int to) {
				super(new Mapper<Integer, Integer>() {
					public Integer map(Integer index) {
						return (index > (to - from)) ? null : from + index;
					}
				});
			}
		}

		/**
		 * A lazy list that "contains" all integers in a range
		 * 
		 * @author Hannes Walz<info@leuchtetgruen.de>
		 *
		 */
		public static class LazyIntegerList extends LazyList<Integer> {
			public LazyIntegerList(final int from, final int to) {
				super(new LazyListDataSource<Integer>() {
					public Integer get(int index, @SuppressWarnings("rawtypes") LazyList ll) {
						return from + index;
					}

					public int size() {
						return 1 + (to-from);
					}

					public boolean shouldCache() {
						return true;
					}
				});
			}
		}

		/**
		 * Measures the execution time of a runner
		 * 
		 * @param r
		 * @return
		 */
		public static long measureExecutionTime(Runnable r) {
			long before = System.currentTimeMillis();
			r.run();
			long after = System.currentTimeMillis();
			return (after - before);
		}

		/**
		 * Prints the execution time of a runnable
		 * 
		 * @param r
		 */
		public static void benchmark(Runnable r) {
			System.out.println("Running...");
			long time = measureExecutionTime(r);
			System.out.println("Done.");
			System.out.println("Execution time: " + time + "ms");
		}

		public static <T> boolean in(T[] arr, final T elem) {
			return F.isValidForAny(arr, new Decider<T>() {
				public boolean decide(T o) {
					return (o.equals(elem));
				}
			});
		}
		
		/**
		 * Returns a decider that will always return the opposite of what the passed in
		 * decider would decide.
		 * 
		 * @param d
		 * @return
		 */
		public static <T> Decider<T> oppositeDecider(final Decider<T> d) {
			return new Decider<T>() {

				@Override
				public boolean decide(T o) {
					return !d.decide(o);
				}
			};
		}

	}
}
