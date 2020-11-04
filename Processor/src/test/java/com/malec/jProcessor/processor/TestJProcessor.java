package com.malec.jProcessor.processor;

import com.google.common.base.Joiner;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import java.net.MalformedURLException;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

public class TestJProcessor {
    final JavaFileObject input = JavaFileObjects
            .forSourceString("com.malec.jProcessor.processor.Person", Joiner.on("\n")
                    .join("package com.malec.jProcessor.processor;", "",
                            "import com.malec.jProcessor.processor.annotation.Data;", "", "@Data",
                            "public class Person {", "", "    private Integer age = 0;",
                            "    private String name;", "    private String surname;",
                            "    private String passport;", "    private String gender;", "}"
                    ));

    final JavaFileObject output = JavaFileObjects
            .forSourceString("com.malec.jProcessor.processor.Person", Joiner.on("\n")
                    .join("package com.malec.jProcessor.processor;", "",
                            "import com.malec.jProcessor.processor.annotation.Data;", "", "@Data",
                            "public class Person {", "", "    private Integer age = 0;",
                            "    private String name;", "    private String surname;",
                            "    private String passport;", "    private String gender;", "",
                            "    public void setAge(Integer age) {", "        this.age = age;",
                            "    }", "", "    public void setName(String name) {",
                            "        this.name = name;", "    }", "",
                            "    public void setSurname(String surname) {",
                            "        this.surname = surname;", "    }", "",
                            "    public void setPassport(String passport) {",
                            "        this.passport = passport;", "    }", "",
                            "    public void setGender(String gender) {",
                            "        this.gender = gender;", "    }", "}"
                    ));

    @Test
    public void test() throws MalformedURLException {

        final Compilation result = javac().withProcessors(new JProcessor()).compile(input);
        assertThat(result).succeeded();
    }
}
