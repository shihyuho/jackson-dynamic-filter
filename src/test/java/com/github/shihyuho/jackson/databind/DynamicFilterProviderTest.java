package com.github.shihyuho.jackson.databind;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ParseContext;

/**
 * 
 * @author Matt S.Y. Ho
 *
 */
public class DynamicFilterProviderTest {

  private ParseContext parser;
  private ObjectMapper mapper;

  @Before
  public void setUp() {
    parser = JsonPath
        .using(Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL));

    mapper = new ObjectMapper();
    mapper.addMixIn(Object.class, DynamicFilterMixIn.class);
    mapper.setFilterProvider(new DynamicFilterProvider());
  }

  @Test
  public void test() throws Exception {
    SomeObject someObject = new SomeObject(1, 2, 3);
    PropertyFilter withoutB = SimpleBeanPropertyFilter.serializeAllExcept("b");

    String actual = mapper.writeValueAsString(someObject);
    Assert.assertEquals(1, parser.parse(actual).read("$.a", Integer.class).intValue());
    Assert.assertEquals(2, parser.parse(actual).read("$.b", Integer.class).intValue());
    Assert.assertEquals(3, parser.parse(actual).read("$.c", Integer.class).intValue());

    actual = mapper.writer(new DynamicFilterProvider(withoutB)).writeValueAsString(someObject);
    Assert.assertEquals(1, parser.parse(actual).read("$.a", Integer.class).intValue());
    Assert.assertNull(parser.parse(actual).read("$.b"));
    Assert.assertEquals(3, parser.parse(actual).read("$.c", Integer.class).intValue());

    actual = mapper.writeValueAsString(someObject);
    Assert.assertEquals(1, parser.parse(actual).read("$.a", Integer.class).intValue());
    Assert.assertEquals(2, parser.parse(actual).read("$.b", Integer.class).intValue());
    Assert.assertEquals(3, parser.parse(actual).read("$.c", Integer.class).intValue());

    actual = mapper.writer(new DynamicFilterProvider()).writeValueAsString(someObject);
    Assert.assertEquals(1, parser.parse(actual).read("$.a", Integer.class).intValue());
    Assert.assertEquals(2, parser.parse(actual).read("$.b", Integer.class).intValue());
    Assert.assertEquals(3, parser.parse(actual).read("$.c", Integer.class).intValue());
  }

}
