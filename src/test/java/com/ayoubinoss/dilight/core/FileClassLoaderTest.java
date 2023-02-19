package com.ayoubinoss.dilight.core;
import org.junit.Test;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import static org.junit.Assert.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileClassLoaderTest {

    @Test
    public void testLoad() throws Exception {
        // create a temporary directory
        Path tempDir = Files.createTempDirectory("test");
        // Create a test class file in the temp directory
        String className = "TestClass";
        String fileContent = "public class " + className + " {}";
        Path testClassFile = Paths.get(tempDir.toString(), className + ".java");
        Files.write(testClassFile, fileContent.getBytes());
        // Compile the test class
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, testClassFile.toString());

        // Create an instance of the FileClassLoader
        FileClassLoader classLoader = new FileClassLoader.FileClassLoaderBuilder()
                .with(tempDir)
                .build();

        // Test the load method
        Stream<Class<?>> classes = classLoader.load();
        assertTrue(classes.anyMatch(cls -> cls.getName().equals(className)));

        // Test the load(Path dirPath) method
        classes = classLoader.load(tempDir);
        assertTrue(classes.anyMatch(cls -> cls.getName().equals(className)));
    }
}
