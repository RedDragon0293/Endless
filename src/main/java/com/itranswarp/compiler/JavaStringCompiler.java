package com.itranswarp.compiler;

import cn.asone.endless.utils.ClientUtils;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * In-memory compile Java source code as String.
 *
 * @author michael
 */
public class JavaStringCompiler {

	JavaCompiler compiler;
	StandardJavaFileManager stdManager;
	//DiagnosticCollector<JavaFileObject> errorHandler = new DiagnosticCollector<>();

	public JavaStringCompiler() {
		this.compiler = ToolProvider.getSystemJavaCompiler();
		this.stdManager = compiler.getStandardFileManager(null, null, null);
	}

	/**
	 * Compile a Java source file in memory.
	 *
	 * @param fileName Java file name, e.g. "Test.java"
	 * @param source   The source code as String.
	 * @return The compiled results as Map that contains class name as key,
	 * class binary as value.
	 * @throws IOException If compile error.
	 */
	public Map<String, byte[]> compile(String fileName, String source) throws IOException {
		try (MemoryJavaFileManager manager = new MemoryJavaFileManager(stdManager)) {
			JavaFileObject javaFileObject = manager.makeStringSource(fileName, source);
			CompilationTask task = compiler.getTask(null, manager, null, null, null, Collections.singletonList(javaFileObject));
			Boolean result = task.call();
			if (result == null || !result) {
				throw new RuntimeException("Compilation failed.");
			}
			return manager.getClassBytes();
		} catch (RuntimeException e) {
			ClientUtils.logger.error("Compilation failed.", e);
			return new HashMap<>();
		}
	}

	/**
	 * Load class from compiled classes.
	 *
	 * @param name       Full class name.
	 * @param classBytes Compiled results as a Map.
	 * @return The Class instance.
	 * @throws IOException If load error.
	 */
	public Class<?> loadClass(String name, Map<String, byte[]> classBytes) throws IOException {
		try (MemoryClassLoader classLoader = new MemoryClassLoader(classBytes)) {
			return classLoader.loadClass(name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
