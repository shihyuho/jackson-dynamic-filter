[![Build Status](https://travis-ci.org/shihyuho/jackson-dynamic-filter.svg?branch=master)](https://travis-ci.org/shihyuho/jackson-dynamic-filter)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.shihyuho/jackson-dynamic-filter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.shihyuho/jackson-dynamic-filter)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/shihyuho/jackson-dynamic-filter/blob/master/LICENSE)


# Jackson Dynamic Property Filter

Basically, when you are using Gson and you need to exclude specific fields from Serialization WITHOUT annotations on the target object, you will use `ExclusionStrategy`. But I didn't find an similar way to do that in Jackson. So this repo provides an easy way to determine filters dynamically, and it also well integration with Spring MVC.

## Requirements

- Java 8
- Dependency versions controlled by Spring IO Platform: [Athens-SR1](http://docs.spring.io/platform/docs/Athens-SR1/reference/htmlsingle/#appendix-dependency-versions)

## Download

To add a dependency using Maven, use the following:

```xml
<dependency>
	<groupId>com.github.shihyuho</groupId>
	<artifactId>jackson-dynamic-filter</artifactId>
	<version>1.0</version>
</dependency>
```

To add a dependency using Gradle:

```xml
dependencies {
    compile 'com.github.shihyuho:jackson-dynamic-filter:1.0'
}
```

To download directly: [Releases](https://github.com/shihyuho/jackson-dynamic-filter/releases)


## Usage

```java
ObjectMapper mapper = new ObjectMapper();
mapper.addMixIn(Object.class, DynamicFilterMixIn.class);
mapper.setFilterProvider(new DynamicFilterProvider());

String jsonWithAllFields = mapper.writeValueAsString(someObject);

PropertyFilter someFilter = SimpleBeanPropertyFilter.serializeAllExcept("someField");
String jsonWithoutSomeField = mapper
	.writer(new DynamicFilterProvider(someFilter)) // determine custom filter 
    .writeValueAsString(someObject);
```

## Spring intergration

### Enabling in your Spring MVC

All you need to do is to wire `DynamicFilterResponseBodyAdvice` into your application. `DynamicFilterResponseBodyAdvice` implements Spring's`AbstractMappingJacksonResponseBodyAdvice` and can be plugged in as follows:

```java
@EnableWebMvc
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.addMixIn(Object.class, DynamicFilterMixIn.class);
		mapper.setFilterProvider(new DynamicFilterProvider());
		converters.add(new MappingJackson2HttpMessageConverter(mapper));
	}

	@Bean
	public DynamicFilterResponseBodyAdvice dynamicFilterResponseBodyAdvice() {
		return new DynamicFilterResponseBodyAdvice();
	}
}
```

### Using annotation

- `@SerializeAllExcept` - Same as `SimpleBeanPropertyFilter.serializeAllExcept(...)`
- `@FilterOutAllExcept` - Same as `SimpleBeanPropertyFilter.filterOutAllExcept(...)`

```java
@RestController
public class SomeController {
  
	@SerializeAllExcept({"someField", "anotherField"})
	@RequestMapping(value = "/without/some-fields", method = RequestMethod.GET)
	public SomeObject withoutSomeFields() {
		return someObject;
	}
	
	@FilterOutAllExcept({"someField", "anotherField"})
	@RequestMapping(value = "/only/some-fields", method = RequestMethod.GET)
	public SomeObject onlySomeFields() {
		return someObject;
	}
}
```

> [SimpleBeanPropertyFilter javadoc](https://fasterxml.github.io/jackson-databind/javadoc/2.3.0/com/fasterxml/jackson/databind/ser/impl/SimpleBeanPropertyFilter.html)

### Custom annotation

You can annotate a custom annotation:

```java
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WithoutAuditingFields {
}
```

```java
public class WithoutAuditingFieldsResolver extends DynamicFilterProvider<WithoutAuditingFields> {
	@Override
	public PropertyFilter apply(WithoutAuditingFields annotation) {
		return SimpleBeanPropertyFilter.serializeAllExcept("id", "createdBy", "createdTime",
			"modifiedBy", "modifiedTime");
	}
}
```

register into `DynamicFilterResponseBodyAdvice`

```java
@Bean
public DynamicFilterResponseBodyAdvice dynamicFilterResponseBodyAdvice() {
	DynamicFilterResponseBodyAdvice advice = new DynamicFilterResponseBodyAdvice();
	advice.addResolvers(new WithoutAuditingFieldsResolver());
	return advice;
}
```

and then use it for you controller as follows:

```java
@RestController
public class SomeController {
  
	@WithoutAuditingFields
	@RequestMapping(value = "/some-path", method = RequestMethod.GET)
	public SomeObject getSomeObject() {
		return someObject;
	}
}
```
