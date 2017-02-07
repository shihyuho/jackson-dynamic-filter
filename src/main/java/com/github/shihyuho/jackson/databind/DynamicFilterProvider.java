package com.github.shihyuho.jackson.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import static java.util.Objects.*;


/**
 * It's a delegator class for {@code PropertyFilter} and extends {@code SimpleFilterProvider} at the
 * same time. The purpose to use this class is just for convenience setting into
 * {@code ObjectMapper}.
 * 
 * @author Matt S.Y. Ho
 *
 */
public class DynamicFilterProvider extends SimpleFilterProvider implements PropertyFilter {

  public static final String FILTER_ID = "DynamicFilterProvider$FILTER";
  private static final long serialVersionUID = -362486406389944351L;

  private final PropertyFilter delegate;

  /**
   * Construct a {@code PropertyFilterHolder} that delegate
   * {@link SimpleBeanPropertyFilter#serializeAll()}
   */
  public DynamicFilterProvider() {
    this(SimpleBeanPropertyFilter.serializeAll());
  }

  /**
   * Construct a {@code PropertyFilterHolder} for given {@code delegate}
   * 
   * @param delegate PropertyFilter
   * @throws NullPointerException if {@code delegate} is {@code null}
   */
  public DynamicFilterProvider(PropertyFilter delegate) {
    this.delegate = requireNonNull(delegate);
    addFilter(FILTER_ID, this);
  }

  @Override
  public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider prov,
      PropertyWriter writer) throws Exception {
    delegate.serializeAsField(pojo, jgen, prov, writer);
  }

  @Override
  public void serializeAsElement(Object elementValue, JsonGenerator jgen, SerializerProvider prov,
      PropertyWriter writer) throws Exception {
    delegate.serializeAsElement(elementValue, jgen, prov, writer);
  }

  @SuppressWarnings("deprecation")
  @Override
  public void depositSchemaProperty(PropertyWriter writer, ObjectNode propertiesNode,
      SerializerProvider provider) throws JsonMappingException {
    delegate.depositSchemaProperty(writer, propertiesNode, provider);
  }

  @Override
  public void depositSchemaProperty(PropertyWriter writer, JsonObjectFormatVisitor objectVisitor,
      SerializerProvider provider) throws JsonMappingException {
    delegate.depositSchemaProperty(writer, objectVisitor, provider);
  }

}
