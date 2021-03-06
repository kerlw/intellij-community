/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.java.psi.codeStyle.arrangement

import com.intellij.application.options.CodeStyle
import com.intellij.formatting.fileSet.NamedScopeDescriptor
import com.intellij.psi.codeStyle.CodeStyleSettings
import groovy.transform.CompileStatic
import org.junit.Before

import static com.intellij.psi.codeStyle.arrangement.std.StdArrangementTokens.Modifier.PROTECTED
import static com.intellij.psi.codeStyle.arrangement.std.StdArrangementTokens.Modifier.PUBLIC
import static com.intellij.psi.codeStyle.arrangement.std.StdArrangementTokens.Order.BY_NAME

/**
 * @author Denis Zhdanov
 * @since 11/14/12 1:29 PM
 */
@CompileStatic
class JavaRearrangerByNameTest extends AbstractJavaRearrangerTest {

  @Before
  void setUp() {
    super.setUp()
    commonSettings.BLANK_LINES_AROUND_METHOD = 0
    commonSettings.BLANK_LINES_AROUND_CLASS = 0
  }
  
  void "test only name condition"() {
    doTest(
      initial: '''\
class Test {
  public void setI() {}
  public void getI() {}
  public void test() {}
}''',
      expected: '''\
class Test {
  public void getI() {}
  public void setI() {}
  public void test() {}
}''',
      rules: [nameRule("get.*")]
    )
  }
  
  void "test name condition and others"() {
    doTest(
      initial: '''\
class Test {
  private void getInner() {}
  public void getOuter() {}
  protected void test() {}
}''',
      expected: '''\
class Test {
  public void getOuter() {}
  protected void test() {}
  private void getInner() {}
}''',
      rules: [nameRule("get.*", PUBLIC), rule(PROTECTED)]
    )
  }
  
  void "test name and sort"() {
    doTest(
      initial: '''\
class Test {
  private void getC() {}
  public void test() {}
  public void getA() {}
  public void getB() {}
}''',
      expected: '''\
class Test {
  public void getA() {}
  public void getB() {}
  private void getC() {}
  public void test() {}
}''',
      rules: [ruleWithOrder(BY_NAME, nameRule("get.*"))]
    )
  }

  void "test with formatting disabled"() {
    CodeStyleSettings settings = CodeStyle.getSettings(project)
    NamedScopeDescriptor descriptor = new NamedScopeDescriptor("testScope");
    descriptor.setPattern("file:*.java")
    settings.getExcludedFiles().addDescriptor(descriptor)

    doTest(
      initial: '''\
class Test {
  private void getC() {}
  public void test() {}
  public void getA() {}
  public void getB() {}
}''',

      expected: '''\
class Test {
  private void getC() {}
  public void test() {}
  public void getA() {}
  public void getB() {}
}''',
      rules: [ruleWithOrder(BY_NAME, nameRule("get.*"))]
    )
  }
}
