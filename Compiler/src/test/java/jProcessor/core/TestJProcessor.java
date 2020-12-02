package jProcessor.core;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class TestJProcessor {
    @Test
    public void testCompileSimpleModule() {
        JavaFileObject moduleFile = JavaFileObjects.forSourceLines("test.TestModule",
                "package test;",
                "",
                "import jProcessor.Module;",
                "import jProcessor.Provides;",
                "",
                "@Module",
                "public final class TestModule {",
                "    @Provides",
                "    public String string() {",
                "        return \"\";",
                "    }",
                "}");
        JavaFileObject moduleProviderFile = JavaFileObjects.forSourceLines("test.testModule.TestModule_StringProvider",
                "package test.testModule;",
                "",
                "import javax.inject.Provider;",
                "import test.TestModule;",
                "",
                "public class TestModule_StringProvider implements Provider<String> {",
                "    private final TestModule module;",
                "",
                "    public TestModule_StringProvider(TestModule module) {",
                "        this.module = module;",
                "    }",
                "",
                "    @Override",
                "    public String get() {",
                "        return string(module);",
                "    }",
                "",
                "    public static String string(TestModule instance) {",
                "        return instance.string();",
                "    }",
                "}");
        assertAbout(javaSource()).that(moduleFile)
                .processedWith(new JProcessor())
                .compilesWithoutError()
                .and().generatesSources(moduleProviderFile);
    }
}
