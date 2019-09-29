package org.di.provider;

import org.di.provider.annotation.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Runner {
    private Map<String, Object> map;

    public Object getBean(String name) {
        return map.get(name);
    }

    //Should be removed
    public int size() {
        return map.size();
    }

    public void run() throws Exception {

        Class<? extends Runner> clazz = this.getClass();
        String packageName = clazz.getPackage().getName();
        List<Class<?>> classes = getClasses(packageName);

        map = classes.stream()
                .filter(cl -> cl.isAnnotationPresent(Component.class))
                .collect(Collectors.toMap(Class::getSimpleName, this::newInstance));
    }

    private  Object newInstance(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getConstructor();
            return constructor.newInstance();

        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages
     * and are annotated by annotation {@link Component}.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private  List<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException {
       //TODO: resolve issue with class loader
        ClassLoader classLoader = this.getClass().getClassLoader();

        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<Class<?>> classes = new ArrayList<>();

        for (File directory : getFiles(resources)) {
            classes.addAll(findClasses(directory, packageName));
        }

        return classes;
    }

    /**
     * @param resources
     * @return
     */
    private static List<File> getFiles(Enumeration<URL> resources) {
        List<File> dirs = new ArrayList<>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        return dirs;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class<?>> findClasses(File directory, String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        try {
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    classes.addAll(findClasses(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                }
            }

        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
        return classes;
    }

}
