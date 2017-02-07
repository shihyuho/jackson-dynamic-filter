package com.github.shihyuho.jackson.databind;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.github.shihyuho.jackson.databind.MockController.Config;

/**
 * 
 * @author Matt S.Y. Ho
 *
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
public class FilterOutAllExceptTest {

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

    mockMvc.perform(get("/object/only/ab")).andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.a").exists()).andExpect(jsonPath("$.b").exists())
        .andExpect(jsonPath("$.c").doesNotExist());

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

    mockMvc.perform(get("/collection/only/ab")).andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$[*].a").exists()).andExpect(jsonPath("$[*].b").exists())
        .andExpect(jsonPath("$[*].c").doesNotExist());

    mockMvc.perform(get("/collection")).andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$[*].a").exists()).andExpect(jsonPath("$[*].b").exists())
        .andExpect(jsonPath("$[*].c").exists());
  }

}
