package com.github.shihyuho.jackson.databind.resolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.github.shihyuho.jackson.databind.DynamicFilterResponseBodyAdvice;
import com.github.shihyuho.jackson.databind.MockController;
import com.github.shihyuho.jackson.databind.WithoutAB;
import com.github.shihyuho.jackson.databind.resolver.DynamicFilterResolver;
import com.github.shihyuho.jackson.databind.resolver.DynamicFilterResolverTest.Config;

/**
 * 
 * @author Matt S.Y. Ho
 *
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Config.class})
public class DynamicFilterResolverTest {

  @EnableWebMvc
  @Configuration
  public static class Config extends MockController.Config {

    @Bean
    public DynamicFilterResponseBodyAdvice propertyFilterResponseBodyAdvice() {
      DynamicFilterResponseBodyAdvice advice = new DynamicFilterResponseBodyAdvice();
      advice.addResolvers(new DynamicFilterResolver<WithoutAB>() {
        @Override
        public PropertyFilter apply(WithoutAB t) {
          return SimpleBeanPropertyFilter.serializeAllExcept("a", "b");
        }
      });
      return advice;
    }
  }

  @Autowired
  private WebApplicationContext wac;
  private MockMvc mockMvc;

  @Before
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
  }

  @Test
  public void testGetEntity() throws Exception {
    mockMvc.perform(get("/object")).andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.a").exists()).andExpect(jsonPath("$.b").exists())
        .andExpect(jsonPath("$.c").exists());

    mockMvc.perform(get("/custom/object/without/ab")).andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.a").doesNotExist()).andExpect(jsonPath("$.b").doesNotExist())
        .andExpect(jsonPath("$.c").exists());

    mockMvc.perform(get("/object")).andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.a").exists()).andExpect(jsonPath("$.b").exists())
        .andExpect(jsonPath("$.c").exists());
  }


  @Test
  public void testGetCollection() throws Exception {
    mockMvc.perform(get("/collection")).andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$[*].a").exists()).andExpect(jsonPath("$[*].b").exists())
        .andExpect(jsonPath("$[*].c").exists());

    mockMvc.perform(get("/custom/collection/without/ab")).andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$[*].a").doesNotExist()).andExpect(jsonPath("$[*].b").doesNotExist())
        .andExpect(jsonPath("$[*].c").exists());

    mockMvc.perform(get("/collection")).andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$[*].a").exists()).andExpect(jsonPath("$[*].b").exists())
        .andExpect(jsonPath("$[*].c").exists());
  }

  @Test
  public void testGetGrid() throws Exception {
    mockMvc.perform(get("/response/object")).andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$[*].a").exists()).andExpect(jsonPath("$[*].b").exists())
        .andExpect(jsonPath("$[*].c").exists());

    mockMvc.perform(get("/custom/response/object/without/ab")).andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$[*].a").doesNotExist()).andExpect(jsonPath("$[*].b").doesNotExist())
        .andExpect(jsonPath("$[*].c").exists());

    mockMvc.perform(get("/response/object")).andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$[*].a").exists()).andExpect(jsonPath("$[*].b").exists())
        .andExpect(jsonPath("$[*].c").exists());
  }
}
