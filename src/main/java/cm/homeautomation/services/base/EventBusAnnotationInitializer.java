package cm.homeautomation.services.base;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.greenrobot.eventbus.Subscribe;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.scanners.TypeElementsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.google.common.base.Predicate;

/**
 * initialize event bus annotated objects
 *
 * @author christoph
 *
 */
@AutoCreateInstance
public class EventBusAnnotationInitializer {

	private static Map<Class, Object> instances = new HashMap<>();

	public EventBusAnnotationInitializer() {
		this(false);
	}

	public EventBusAnnotationInitializer(boolean noInit) {
		if (!noInit) {
			initializeEventBus();
		}
	}
	
	public Map<Class, Object> getInstances() {
		return instances;
	}

	public void initializeEventBus() {
		final Set<Method> resources = getEventBusClasses();

		for (final Method method : resources) {
			final Class<?> declaringClass = method.getDeclaringClass();

			// check if the class has already been initialized
			if (!instances.containsKey(declaringClass)) {

				LogManager.getLogger(this.getClass()).info("Creating class: " + declaringClass.getName());

				final Runnable singleInstanceCreator = new Runnable() {
					@Override
					public void run() {
						try {
							final Object classInstance = declaringClass.newInstance();

							instances.put(declaringClass, classInstance);
						} catch (InstantiationException | IllegalAccessException e) {
							LogManager.getLogger(this.getClass()).info("Failed creating class");
						}
					}
				};
				new Thread(singleInstanceCreator).start();
			}
		}
	}

	public Set<Method> getEventBusClasses() {
		Predicate<String> filter = new FilterBuilder().includePackage("cm.homeautomation").include(".*class")
				.exclude(".*java").exclude(".*properties").exclude(".*xml");

		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setUrls(ClasspathHelper.forPackage("cm.homeautomation")).filterInputsBy(filter)
				.setScanners(new TypeElementsScanner(), new TypeAnnotationsScanner(), new MethodAnnotationsScanner()).useParallelExecutor());

		// MethodAnnotationsScanner
		final Set<Method> resources = reflections.getMethodsAnnotatedWith(Subscribe.class);
		return resources;
	}
}
