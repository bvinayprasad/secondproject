/**
 * 
 */
package resttest;

import java.util.HashSet;
import java.util.Set;

/**
 * @author napattan
 * @param <T>
 *
 */
@SuppressWarnings("rawtypes")
public final class Context {
	private static Context context = null;
	private Set<Class> resourceSet;
	private Set<Class> dependencySet;
	private int portNumber = 0;
	private boolean parallel = Boolean.FALSE;

	private Context() {
	}

	public static Context getContext() {
		if (context == null) {
			context = new Context();
			context.resourceSet = new HashSet<Class>();
			context.dependencySet = new HashSet<Class>();
		}
		return context;
	}

	/**
	 * @param resourceSet
	 *            the resourceSet to set
	 */
	public Context addResource(Class resourceClass) {
		this.resourceSet.add(resourceClass);
		return this;
	}

	/**
	 * 
	 * @param filterClass
	 * @return Context
	 */
	public Context addFilter(Class filterClass) {
		this.resourceSet.add(filterClass);
		return this;
	}

	/**
	 * @param dependencySet
	 *            the dependencySet to set
	 */
	public Context addDependency(Class dependencyClass) {
		this.dependencySet.add(dependencyClass);
		return this;
	}

	/**
	 * 
	 * @param filterDependencyClass
	 * @return Context
	 */
	public Context addFilterDependency(Class filterDependencyClass) {
		this.dependencySet.add(filterDependencyClass);
		return this;
	}

	/**
	 * @return the resourceSet
	 */
	public Set<Class> getResourceSet() {
		return resourceSet;
	}

	/**
	 * @return the dependencySet
	 */
	public Set<Class> getDependencySet() {
		return dependencySet;
	}

	/**
	 * @return the portNumber
	 */
	public int getPortNumber() {
		return portNumber;
	}

	/**
	 * @param portNumber
	 *            the portNumber to set
	 */
	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	/**
	 * @return the parallel
	 */
	public boolean isParallel() {
		return parallel;
	}

	/**
	 * @param parallel
	 *            the parallel to set
	 */
	public void executeParallel(boolean parallel) {
		this.parallel = parallel;
	}

}
