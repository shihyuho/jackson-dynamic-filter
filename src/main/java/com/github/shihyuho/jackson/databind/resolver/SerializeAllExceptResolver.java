package com.github.shihyuho.jackson.databind.resolver;

import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.github.shihyuho.jackson.databind.SerializeAllExcept;

/**
 * Use {@link SimpleBeanPropertyFilter#serializeAllExcept(String...)} to build
 * {@code PropertyFilter}
 * 
 * @author Matt S.Y. Ho
 *
 */
public class SerializeAllExceptResolver extends DynamicFilterResolver<SerializeAllExcept> {

  @Override
  public PropertyFilter apply(SerializeAllExcept annotation) {
    return SimpleBeanPropertyFilter.serializeAllExcept(annotation.value());
  }

}
