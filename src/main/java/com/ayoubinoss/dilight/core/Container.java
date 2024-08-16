package com.ayoubinoss.dilight.core;


import com.ayoubinoss.dilight.annotations.Component;
import com.ayoubinoss.dilight.annotations.Inject;
import com.ayoubinoss.dilight.exceptions.ContainerException;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;


/**
 * @author ayoubinoss
 */
public class Container {

    private static final Logger LOGGER = Logger.getLogger(Container.class.getName());

    private static final Map<Class<?>, Object> registry = new HashMap<>();

    private static Container instance;

    private Container() {
        try {
            discover();
        } catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "could not start discovery error message : [{0}]", exception.getMessage());
            throw new ContainerException("could not start discovery " + exception.getMessage(), exception);
        }
    }

    public static Container boot() {
        if (instance == null) {
            instance = new Container();
        }

        return instance;
    }

    private void discover() throws Exception {
        String[] classPath = System.getProperty("java.class.path").split(";");
        Optional<Path> path = Stream.of(classPath)
                .map(Paths::get)
                .filter(Files::isDirectory)
                .findFirst();

        if (path.isEmpty()) {
            LOGGER.log(Level.SEVERE, "the class path does not exist!");
            throw new ContainerException("the class path does not exist!");
        }

        FileClassLoader classLoader = new FileClassLoader.FileClassLoaderBuilder().with(path.get()).build();
        Stream<Class<?>> loadedClasses = classLoader.load();

        loadedClasses.filter(e -> e.isAnnotationPresent(Component.class))
                .forEach(clazz -> {
                    try {
                        register(clazz);
                    } catch (Exception exception) {
                        LOGGER.log(Level.SEVERE, "could not register class : [{0}] error message : [{1}]", new Object[]{clazz.getName(), exception.getMessage()});
                    }
                });
    }

    private static void injectClassDependencies(Object object) throws Exception {

        Class<?> objectClass = object.getClass();

        for (Field field : objectClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                Class<?> fieldClass = field.getType();
                if (!registry.containsKey(fieldClass)) {
                    register(fieldClass);
                }
                field.setAccessible(true);
                field.set(object, registry.get(fieldClass));
            }
        }
    }

    /**
     * register a given class with a resolved instance in the container's registry
     *
     * @param clazz the desired class
     * @throws Exception all kind of reflection exceptions are handled by the calling class
     */
    public static void register(Class<?> clazz) throws Exception {
        if (clazz.isAnnotation() || clazz.isInterface()) {
            // TODO : should resolve interfaces
            return;
        }

        Field[] fields = clazz.getDeclaredFields();

        if (!registry.containsKey(clazz)) {
            registerClass(clazz);
        }

        for (Field field : fields) {
            if (field.isAnnotationPresent(Inject.class) && !registry.containsKey(field.getType())) {
                registerClass(field.getType());
            }
        }
    }

    /**
     * register the given class and add its dependencies
     *
     * @param clazz the target class
     * @throws Exception in case of exception, the calling method should be able to handle that
     */
    private static void registerClass(Class<?> clazz) throws Exception {
        registry.put(clazz, clazz.newInstance());
        register(clazz);
        injectClassDependencies(registry.get(clazz));
    }

    /**
     * return an instance of the given class
     *
     * @param appClass the desired class
     * @return an instance of appClass if the class is already registered or the class is annotated with @Component
     * or null in case it does not find it in the registry
     */
    public static Object resolve(Class<?> appClass) {
        if (registry.containsKey(appClass))
            return appClass.cast(registry.get(appClass));
        return null;
    }
}
