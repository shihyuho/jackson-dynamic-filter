package com.github.shihyuho.jackson.databind;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 
 * @author Matt S.Y. Ho
 *
 */
@Controller
public class MockController {

  @EnableWebMvc
  @Configuration
  public static class Config extends WebMvcConfigurerAdapter {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
      ObjectMapper mapper = new ObjectMapper();
      mapper.addMixIn(Object.class, DynamicFilterMixIn.class);
      mapper.setFilterProvider(new DynamicFilterProvider());
      converters.add(new MappingJackson2HttpMessageConverter(mapper));
    }

    @Bean
    public MockController controller() {
      return new MockController();
    }

    @Bean
    public DynamicFilterResponseBodyAdvice propertyFilterResponseBodyAdvice() {
      return new DynamicFilterResponseBodyAdvice();
    }
  }

  @RequestMapping(value = "/object", method = RequestMethod.GET)
  @ResponseBody
  public SomeObject getObjectWithAll() {
    return new SomeObject(1, 2, 3);
  }

  @RequestMapping(value = "/collection", method = RequestMethod.GET)
  @ResponseBody
  public Collection<SomeObject> getCollectionWithAll() {
    return Arrays.asList(new SomeObject(1, 2, 3), new SomeObject(4, 5, 6));
  }

  @RequestMapping(value = "/response/object", method = RequestMethod.GET)
  @ResponseBody
  public MyResponse<SomeObject> getResponseObjectWithAll() {
    return new MyResponse<>(Arrays.asList(new SomeObject(1, 2, 3), new SomeObject(4, 5, 6)));
  }

  @FilterOutAllExcept({"a", "b"})
  @RequestMapping(value = "/object/only/ab", method = RequestMethod.GET)
  @ResponseBody
  public SomeObject getObjectOnlyAB() {
    return getObjectWithAll();
  }

  @FilterOutAllExcept({"a", "b"})
  @RequestMapping(value = "/collection/only/ab", method = RequestMethod.GET)
  @ResponseBody
  public Collection<SomeObject> getCollectionOnlyAB() {
    return getCollectionWithAll();
  }

  @SerializeAllExcept({"a", "b"})
  @RequestMapping(value = "/object/without/ab", method = RequestMethod.GET)
  @ResponseBody
  public SomeObject getObjectWithoutAB() {
    return getObjectWithAll();
  }

  @SerializeAllExcept({"a", "b"})
  @RequestMapping(value = "/collection/without/ab", method = RequestMethod.GET)
  @ResponseBody
  public Collection<SomeObject> getCollectionWithoutAB() {
    return getCollectionWithAll();
  }

  @SerializeAllExcept({"a", "b"})
  @RequestMapping(value = "/response/object/without/ab", method = RequestMethod.GET)
  @ResponseBody
  public MyResponse<SomeObject> getResponseObjectWithoutAB() {
    return getResponseObjectWithAll();
  }

  @WithoutAB
  @RequestMapping(value = "/custom/response/object/without/ab", method = RequestMethod.GET)
  @ResponseBody
  public MyResponse<SomeObject> getCustomAnnotationResponseObjectWithoutAB() {
    return getResponseObjectWithAll();
  }

  @WithoutAB
  @RequestMapping(value = "/custom/object/without/ab", method = RequestMethod.GET)
  @ResponseBody
  public SomeObject getCustomAnnotationObjectWithoutAB() {
    return getObjectWithAll();
  }

  @WithoutAB
  @RequestMapping(value = "/custom/collection/without/ab", method = RequestMethod.GET)
  @ResponseBody
  public Collection<SomeObject> getCustomAnnotationCollectionWithoutAB() {
    return getCollectionWithAll();
  }

  public static class MyResponse<T> extends ResponseEntity<Collection<T>> {

    @JsonCreator
    public MyResponse(@JsonProperty("data") final Collection<T> data) {
      super(data, HttpStatus.OK);
    }
  }

}
