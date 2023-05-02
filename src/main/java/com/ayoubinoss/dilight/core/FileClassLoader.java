package com.ayoubinoss.dilight.core;

import java.net.URI;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ayoubinoss
 */
public class FileClassLoader {

    private List<Path> paths;

    private FileClassLoader() {
        paths = new ArrayList<>();
    }

    /**
     * load classes from the default class path
     *
     * @return A Stream of Class objects representing the loaded classes.
     * @throws RuntimeException if the default class path does not contain a directory.
     */
    public Stream<Class<?>> load() {
        Optional<Path> defaultDirectory = paths.stream().findFirst();
        if (defaultDirectory.isEmpty()) {
            throw new RuntimeException("The default class path does not contain a directory");
        }

        return load(defaultDirectory.get());
    }

    /**
     * return stream of loaded classes from the given directory path
     * method doesn't throw exception if class is not found it's skipped
     *
     * @param dirPath path to the target directory
     * @return all loaded class
     */
    public Stream<Class<?>> load(Path dirPath) {
        URI uri = dirPath.toUri();
        try {

            URL url = uri.toURL();
            String root = dirPath.toString();
            URLClassLoader classLoader = new URLClassLoader(new URL[]{url});

            List<Path> pathList = Files.walk(dirPath)
                    .filter(e -> !Files.isDirectory(e))
                    .collect(Collectors.toList());

            List<Class<?>> classes = new ArrayList<>();

            for (Path path : pathList) {
                String fullyQualifiedName = path.toString()
                        .substring(root.length() + 1)
                        .replaceAll("[/\\\\]", ".");

                fullyQualifiedName = fullyQualifiedName.substring(0, fullyQualifiedName.length() - 6); // remove ".class" from the end of the name

                try {
                    Class<?> clazz = classLoader.loadClass(fullyQualifiedName);
                    if (clazz != null) {
                        classes.add(clazz);
                    }
                } catch (Exception e) {
                    continue; // tolerate the exception
                }
            }

            return classes.stream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Builder class for the FileClassLoader class.
     */
    public static class FileClassLoaderBuilder {

        private final FileClassLoader classLoader;

        public FileClassLoaderBuilder() {
            classLoader = new FileClassLoader();
        }

        public FileClassLoaderBuilder with(Path path) {
            classLoader.paths.add(path);
            return this;
        }

        public FileClassLoaderBuilder with(Iterable<Path> paths) {
            paths.forEach(classLoader.paths::add);
            return this;
        }

        public FileClassLoader build() {
            sanitize();
            return classLoader;
        }

        /**
         * take only directories from the given files, and make sure the directory physically exists
         */
        private void sanitize() {
            classLoader.paths = classLoader.paths.stream()
                    .filter(Files::isDirectory)
                    .filter(Files::exists)
                    .distinct()
                    .collect(Collectors.toList());
        }
    }

    public String toString() {
        return "FileClassLoader " + paths;
    }
}