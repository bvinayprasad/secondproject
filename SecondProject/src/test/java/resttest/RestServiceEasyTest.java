/**
 * 
 */
package resttest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.easymock.EasyMock;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.TestProperties;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import static javax.ws.rs.HttpMethod.*;
import static org.junit.Assert.assertEquals;

/**
 * @author napattan
 *
 */
@SuppressWarnings("rawtypes")
public abstract class RestServiceEasyTest extends JerseyTest {
	public static final String BASE_URI = "http://localhost:";
	private static final int DEFAULT_PORT = 9998;
	private static final int PARALLEL_PORT = 0;
	private Context context;
	private Map<Class, Object> mockedReference = null;
	private RestServiceEasyTest restServiceEasyTest = null;
	private String currentMethodName = null;

	public RestServiceEasyTest() {
		restServiceEasyTest = this;
	}

	@Rule
	public TestWatcher testWatcher = new TestWatcher() {
		@Override
		protected void starting(final Description description) {
			String methodName = description.getMethodName();
			String className = description.getClassName();
			className = className.substring(className.lastIndexOf('.') + 1);
			restServiceEasyTest.setCurrentMethodName(methodName);
		}
	};

	/**
	 * Initializes 'context' for launching container to execute tests. This
	 * involves providing rest resource class(s) to test, its dependencies to
	 * mock.
	 * 
	 * @param context
	 */
	protected void init(Context context) {
	}

	/**
	 * Returns the Mock object reference to your Test (Classes), which can be
	 * used to mock the expected result
	 * 
	 * @param type
	 * @return
	 */
	protected final <T> T getMockedObject(Class<T> type) {
		Object object = mockedReference.get(type);
		EasyMock.reset(object);
		return type.cast(object);
	}

	private void process(Expectations allExpectations) {
		Set<Class> serviceSet = new HashSet<Class>();
		Set<Object> replaySet = new HashSet<Object>();

		for (Expectation expectation : allExpectations.value()) {
			if (serviceSet.add(expectation.type())) {
				Object obj = process(expectation, Boolean.TRUE);
				replaySet.add(obj);
			} else {
				Object obj = process(expectation, Boolean.FALSE);
				replaySet.add(obj);
			}
		}
		for (Object obj : replaySet) {
			EasyMock.replay(obj);
		}
	}

	private Set<Class> loadSet(Class[] items) {
		Set<Class> params = new LinkedHashSet<Class>();
		for (Class item : items) {
			params.add(item);
		}
		return params;
	}

	private Method searchForMethod(Class type, String name, Class[] params) {

		Set<Class> inputParams = loadSet(params);
		Set<Class> methodParams = new LinkedHashSet<Class>();
		Method[] methods = type.getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (!methods[i].getName().equals(name))
				continue;

			Class[] types = methods[i].getParameterTypes();
			for (Class classType : types) {
				if (classType.isPrimitive()) {
					methodParams.add(getWrapperClass(classType));

				} else {
					methodParams.add(classType);
				}

			}
			if (types.length != params.length)
				continue;
			if (inputParams.toString().equals(methodParams.toString()))
				return methods[i];
		}
		return null;
	}

	private Object process(Expectation singleExpectation, boolean resetRequired) {

		Method method = null;
		Object object = null;
		try {
			object = mockedReference.get(singleExpectation.type());
			try {
				method = object.getClass().getMethod(
						singleExpectation.method(),
						singleExpectation.paramTypes());
			} catch (Exception ex) {
				method = searchForMethod(object.getClass(),
						singleExpectation.method(),
						singleExpectation.paramTypes());
			}

			Class[] methodParamTypes = method.getParameterTypes();
			if (null != object) {
				if (resetRequired) {
					EasyMock.reset(object);
				}

				if (method.getReturnType().equals(Void.TYPE)) {
					method.invoke(object,
							getMockedObjectParams(singleExpectation
									.paramTypes()));
					EasyMock.expectLastCall().times(singleExpectation.times());

				} else {
					EasyMock.expect(
							method.invoke(object,
									getMockedObjectParams(methodParamTypes)))
							.andReturn(
									getReturnedObject(singleExpectation
											.expect()))
							.times(singleExpectation.times());

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}

	private Object getReturnedObject(ReturnVal returnVal) {
		Object returnedObject = null;
		try {
			returnedObject = getReturnedObject(returnVal.type(),
					returnVal.value());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnedObject;
	}

	@SuppressWarnings("unchecked")
	private Object getReturnedObject(Class type, String value) throws Exception {

		Object object = new Object();
		if (type.equals(String.class) || type.equals(Boolean.class)
				|| type.equals(Integer.class) || type.equals(Float.class)
				|| type.equals(Long.class) || type.equals(Double.class)
				|| type.equals(Byte.class) || type.equals(Short.class)
				|| type.equals(Character.class)) {
			object = type.getConstructor(value.getClass()).newInstance(value);
		} else if (Modifier.isFinal(type.getModifiers())) {
			object = type.newInstance();
		} else {
			object = EasyMock.anyObject(type);
		}
		return object;
	}

	@SuppressWarnings("unchecked")
	private Object getWrapperObject(Class type) throws Exception {

		Object object = new Object();

		if (type.equals(String.class)) {
			object = EasyMock.anyString();
		} else if (type.getName().equals("boolean")
				|| type.equals(Boolean.class)) {
			object = EasyMock.anyBoolean();
		} else if (type.getName().equals("int") || type.equals(Integer.class)) {
			object = EasyMock.anyInt();
		} else if (type.getName().equals("float") || type.equals(Float.class)) {
			object = EasyMock.anyFloat();
		} else if (type.getName().equals("long") || type.equals(Long.class)) {
			object = EasyMock.anyLong();
		} else if (type.getName().equals("double") || type.equals(Double.class)) {
			object = EasyMock.anyDouble();
		} else if (type.getName().equals("byte") || type.equals(Byte.class)) {
			object = EasyMock.anyByte();
		} else if (type.getName().equals("short") || type.equals(Short.class)) {
			object = EasyMock.anyShort();
		} else if (type.getName().equals("char")
				|| type.equals(Character.class)) {
			object = EasyMock.anyChar();
		} else {
			object = EasyMock.anyObject(type);
		}

		return object;
	}

	private Class getWrapperClass(Class type) {

		Class clazz = Class.class;

		if (type.getName().equals("boolean")) {
			clazz = Boolean.class;
		} else if (type.getName().equals("int")) {
			clazz = Integer.class;
		} else if (type.getName().equals("float")) {
			clazz = Float.class;
		} else if (type.getName().equals("long")) {
			clazz = Long.class;
		} else if (type.getName().equals("double")) {
			clazz = Double.class;
		} else if (type.getName().equals("byte")) {
			clazz = Byte.class;
		} else if (type.getName().equals("short")) {
			clazz = Short.class;
		} else if (type.getName().equals("char")) {
			clazz = Character.class;
		}

		return clazz;
	}

	private Object[] getMockedObjectParams(Class[] types) {
		List<Object> mockedParams = new ArrayList<Object>();
		for (Class type : types) {
			try {
				mockedParams.add(getWrapperObject(type));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mockedParams.toArray(new Object[mockedParams.size()]);

	}

	/**
	 * helper method to return a test Entity
	 * 
	 * @return Entity
	 */
	protected final Entity getTestEntity() {
		return Entity.text("test");
	}

	/**
	 * helper method to support the method call allowed for a rest method ,
	 * Asserts 405 (Reason: method not allowed)
	 * 
	 * @param path
	 * @param methodNames
	 */
	protected final void checkNotAllowedMethods(String path,
			String... methodNames) {
		Response response = null;
		for (String methodName : methodNames) {
			if (methodName.equalsIgnoreCase(GET)) {
				response = target(path).request().get();
				assertEquals(405, response.getStatus());
			}
			if (methodName.equalsIgnoreCase(POST)) {
				response = target(path).request().post(this.getTestEntity());

				assertEquals(405, response.getStatus());
			}
			if (methodName.equalsIgnoreCase(PUT)) {
				response = target(path).request().put(this.getTestEntity());
				assertEquals(405, response.getStatus());
			}
			if (methodName.equalsIgnoreCase(DELETE)) {
				response = target(path).request().delete();
				assertEquals(405, response.getStatus());

			}
			if (methodName.equalsIgnoreCase(HEAD)) {
				response = target(path).request().head();
				assertEquals(405, response.getStatus());
			}
		}

	}

	@Override
	protected Application configure() {
		enable(TestProperties.LOG_TRAFFIC);
		enable(TestProperties.DUMP_ENTITY);

		AbstractBinder binder = new AbstractBinder() {
			@SuppressWarnings("unchecked")
			@Override
			protected void configure() {
				mockedReference = new HashMap<Class, Object>();
				Set<Class> dependencySet = context.getDependencySet();
				Iterator<Class> dependencyIterator = dependencySet.iterator();
				while (dependencyIterator.hasNext()) {
					bindMockDependency(dependencyIterator.next());
				}
				Set<Entry<Class, Object>> mockedDependenciesEntries = mockedReference
						.entrySet();
				Iterator<Entry<Class, Object>> dependentIterator = mockedDependenciesEntries
						.iterator();
				while (dependentIterator.hasNext()) {
					Entry<Class, Object> dependentEntry = dependentIterator
							.next();
					bindFactory(new InstanceFactory(dependentEntry.getValue()))
							.to(dependentEntry.getKey());

				}
				processExpectations(restServiceEasyTest.getCurrentMethodName());

			}
		};
		ResourceConfig config = new ResourceConfig();
		Set<Class> resourceSet = context.getResourceSet();
		Iterator<Class> resourceSetItr = resourceSet.iterator();
		while (resourceSetItr.hasNext()) {
			config.register(resourceSetItr.next());
		}
		config.register(binder);
		config.register(RestMessageBodyReader.class);
		config.register(RestMessageBodyWriter.class);

		return config;
	}

	@Override
	protected TestContainerFactory getTestContainerFactory()
			throws TestContainerException {
		return new GrizzlyWebTestContainerFactory();

	}

	@Override
	protected DeploymentContext configureDeployment() {
		supportAnnotations();
		context = Context.getContext();
		init(context);
		if (context.getPortNumber() > 0) {
			forceSet(TestProperties.CONTAINER_PORT,
					String.valueOf(context.getPortNumber()));
		} else if (context.isParallel()) {
			forceSet(TestProperties.CONTAINER_PORT,
					String.valueOf(PARALLEL_PORT));
		} else {
			forceSet(TestProperties.CONTAINER_PORT,
					String.valueOf(DEFAULT_PORT));
		}
		ServletDeploymentContext servletDeploymentContext = ServletDeploymentContext
				.forServlet(new ServletContainer((ResourceConfig) configure()))
				.initParam(ServerProperties.PROVIDER_PACKAGES,
						"com.oracle.sites.visitors.core.rest").build();
		return servletDeploymentContext;

	}

	@Override
	protected void configureClient(ClientConfig config) {
		config.register(RestMessageBodyReader.class);
		config.register(RestMessageBodyWriter.class);
	}

	/**
	 * @return the currentMethodName
	 */
	protected String getCurrentMethodName() {
		return currentMethodName;
	}

	/**
	 * @param currentMethodName
	 *            the currentMethodName to set
	 */
	protected void setCurrentMethodName(String currentMethodName) {
		this.currentMethodName = currentMethodName;
	}

	/** This method supports the annotation set into the Test class */
	private void supportAnnotations() {
		if (this.getClass().isAnnotationPresent(ResourceContext.class)) {
			Annotation annotation = this.getClass().getAnnotation(
					ResourceContext.class);
			ResourceContext resourceContext = (ResourceContext) annotation;
			convertArraytoSet(resourceContext.names(), Context.getContext()
					.getResourceSet());
			convertArraytoSet(resourceContext.depends(), Context.getContext()
					.getDependencySet());
		}
		if (this.getClass().isAnnotationPresent(FilterContext.class)) {
			Annotation annotation = this.getClass().getAnnotation(
					FilterContext.class);
			FilterContext filterContext = (FilterContext) annotation;
			convertArraytoSet(filterContext.names(), Context.getContext()
					.getResourceSet());
			convertArraytoSet(filterContext.depends(), Context.getContext()
					.getDependencySet());
		}
		if (this.getClass().isAnnotationPresent(MapperContext.class)) {
			Annotation annotation = this.getClass().getAnnotation(
					MapperContext.class);
			MapperContext mapperContext = (MapperContext) annotation;
			convertArraytoSet(mapperContext.names(), Context.getContext()
					.getResourceSet());
			convertArraytoSet(mapperContext.depends(), Context.getContext()
					.getDependencySet());
		}
		if (this.getClass().isAnnotationPresent(ExecutionContext.class)) {
			Annotation annotation = this.getClass().getAnnotation(
					ExecutionContext.class);
			ExecutionContext executionContext = (ExecutionContext) annotation;
			Context.getContext().setPortNumber(executionContext.port());
			Context.getContext().executeParallel(executionContext.parallel());
		}

	}

	private void convertArraytoSet(Class[] classArray, Set<Class> contextSet) {
		for (Class clazz : classArray) {
			contextSet.add(clazz);
		}
	}

	private void processExpectations(String methodName) {
		Expectations allExpectations = null;
		Expectation singleExpectation = null;
		for (Method method : restServiceEasyTest.getClass()
				.getDeclaredMethods()) {
			if (method.getName().equals(methodName)) {
				if (method.isAnnotationPresent(Expectations.class)) {
					Annotation annotation = method
							.getAnnotation(Expectations.class);
					allExpectations = (Expectations) annotation;
				} else if (method.isAnnotationPresent(Expectation.class)) {

					Annotation annotation = method
							.getAnnotation(Expectation.class);
					singleExpectation = (Expectation) annotation;

				}

				if (null != allExpectations) {
					process(allExpectations);
				} else if (null != singleExpectation) {
					Object obj = process(singleExpectation, Boolean.TRUE);
					EasyMock.replay(obj);
				}
			}

		}
	}

	private <T> T bindMockDependency(Class<T> mockClass) {
		T mockedObject = EasyMock.createNiceMock(mockClass);
		bindDependency(mockClass, mockedObject);
		return mockedObject;
	}

	private <T> void bindDependency(Class<T> type, Object bindInstance) {
		mockedReference.put(type, bindInstance);
	}

	private class InstanceFactory<T> implements Factory<T> {

		private T instance;

		public InstanceFactory(T instance) {
			this.instance = instance;
		}

		@Override
		public void dispose(T t) {
		}

		@Override
		public T provide() {
			return instance;
		}

	}

}
