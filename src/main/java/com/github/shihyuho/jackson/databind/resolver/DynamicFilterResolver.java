package com.github.shihyuho.jackson.databind.resolver;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.core.MethodParameter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ser.PropertyFilter;

/**
 * Abstract class that define how to map annotation to {@code PropertyFilter}
 * 
 * @author Matt S.Y. Ho
 *
 * @param <A>
 */
public abstract class DynamicFilterResolver<A extends Annotation> extends TypeReference<A>
    implements Function<A, PropertyFilter> {

  @SuppressWarnings("unchecked")
  public final PropertyFilter resolve(MethodParameter methodParameter) {
    return Optional.ofNullable(methodParameter.getMethodAnnotation((Class<A>) getType()))
        .map(this::apply).orElse(null);
  }

}
