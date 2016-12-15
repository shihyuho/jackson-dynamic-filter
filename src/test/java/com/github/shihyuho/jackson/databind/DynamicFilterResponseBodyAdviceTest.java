package com.github.shihyuho.jackson.databind;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.github.shihyuho.jackson.databind.DynamicFilterResponseBodyAdvice;
import com.github.shihyuho.jackson.databind.resolver.DynamicFilterResolver;

/**
 * 
 * @author Matt S.Y. Ho
 *
 */
public class DynamicFilterResponseBodyAdviceTest {

  @Test
  public void testContainsOnlyOnceReference() {
    DynamicFilterResponseBodyAdvice advice = new DynamicFilterResponseBodyAdvice();
    DynamicFilterResolver<WithoutAB> b = new B();
    advice.addResolvers(b, b, b);
    assertThat(advice.resolvers.values()).containsOnlyOnce(b);
  }

  @Test
  public void testContainsOnlyOnceAnnotation() {
    DynamicFilterResponseBodyAdvice advice = new DynamicFilterResponseBodyAdvice();
    DynamicFilterResolver<WithoutAB> a = new A();
    DynamicFilterResolver<WithoutAB> b = new B();
    advice.addResolvers(a, b);
    assertThat(advice.resolvers.values()).containsOnlyOnce(b).doesNotContain(a);
  }

  public static class A extends DynamicFilterResolver<WithoutAB> {
    @Override
    public PropertyFilter apply(WithoutAB t) {
      return null;
    }
  }

  public static class B extends DynamicFilterResolver<WithoutAB> {
    @Override
    public PropertyFilter apply(WithoutAB t) {
      return null;
    }
  }

}
