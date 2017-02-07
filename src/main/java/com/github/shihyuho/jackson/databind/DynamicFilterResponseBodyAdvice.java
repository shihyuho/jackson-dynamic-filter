package com.github.shihyuho.jackson.databind;

import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.github.shihyuho.jackson.databind.resolver.DynamicFilterResolver;
import com.github.shihyuho.jackson.databind.resolver.FilterOutAllExceptResolver;
import com.github.shihyuho.jackson.databind.resolver.SerializeAllExceptResolver;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMappingJacksonResponseBodyAdvice;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Runtime switching {@code PropertyFilter} if found any {@link DynamicFilterResolver}.
 * 
 * @author Matt S.Y. Ho
 *
 */
@ControllerAdvice
public final class DynamicFilterResponseBodyAdvice extends AbstractMappingJacksonResponseBodyAdvice {

  @SuppressWarnings("rawtypes")
  protected final Map<Class, DynamicFilterResolver<?>> resolvers = new HashMap<>();

  public DynamicFilterResponseBodyAdvice() {
    addResolvers(new SerializeAllExceptResolver(), new FilterOutAllExceptResolver());
  }

  @Override
  public boolean supports(MethodParameter returnType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return super.supports(returnType, converterType)
        && resolvers.keySet().stream().anyMatch(returnType::hasMethodAnnotation);
  }

  @Override
  protected void beforeBodyWriteInternal(MappingJacksonValue bodyContainer, MediaType contentType,
      MethodParameter returnType, ServerHttpRequest request, ServerHttpResponse response) {
    resolveFilter(returnType).map(DynamicFilterProvider::new).ifPresent(bodyContainer::setFilters);
  }

  /**
   * Add resolvers.
   * <p>
   * NOTE: Resolvers will be distinct by {@link DynamicFilterResolver#getType()}
   * 
   * @param resolver DynamicFilterResolver
   * @param more DynamicFilterResolver
   */
  public <A extends Annotation> void addResolvers(DynamicFilterResolver<?> resolver,
      DynamicFilterResolver<?>... more) {
    resolvers.put((Class<?>) resolver.getType(), resolver);
    Stream.of(more).filter(Objects::nonNull).forEach(r -> resolvers.put((Class<?>) r.getType(), r));
  }

  protected Optional<PropertyFilter> resolveFilter(MethodParameter returnType) {
    return resolvers.values().stream().map(resolver -> resolver.resolve(returnType))
        .filter(Objects::nonNull).findFirst();
  }

}
