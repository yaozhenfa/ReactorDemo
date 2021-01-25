# 反应式仓储  

考虑到实际开发中常用的关系型数据库与NoSQL为MySQL与MongoDB，所以本教程将主要以
这两类数据库为主要切入口进行介绍，如果读者需要使用其他的数据库可自行安装对应的
驱动并修改配置文件。  

本例中的相关代码可访问[Github](https://github.com/yaozhenfa/ReactorDemo)  

## 一、 MongoDB持久化  

### 1. 前期准备

我们将主要采用JPA的方式进行开发，首先需要在我们的Spring Boot项目的pom.xml中
引用我们所需要的依赖项目。  

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
</dependency>
```

如果读者周围没有MongoDB，可以使用嵌入式数据库来便于测试，类似于H2一样。我们
就需要额外增加引用。  

```xml
<dependency>
	<groupId>de.flapdoodle.embed</groupId>
	<artifactId>de.flapdoodle.embed.mongo</artifactId>
</dependency>
```

嵌入式数据库对于开发和测试是很不错的，一旦我们将应用部署到生产环境，就需要设置几个属性
，让Spring Data MongoDB知道访问何处的MongoDB数据库。  

```yaml
spring.data.mongodb.host:localhost
spring.data.mongodb.port:27017
spring.data.mongodb.database:admin
```  

### 2. 映射文档  

其提供了诸多的注解属性，但是其中只有三个比较常用，具体如下。  

- @Id: 将某个属性指明为文档的ID  
- @Document: 将领域类型声明为要持久化到MongoDB中的文档  
- @Field: 指定某个属性持久化到文档中的字段名称  

在了解上述的注解属性后我们就可以将领域对象映射为文档了。  

```java
@Data
@Document
@AllArgsConstructor
public class Taco {
    @Id
    private final String id;
    private final String name;
    private final String remark;
}
```  

默认设置将会导致数据存储到名为taco的集合中，如果需要重命名我们可以通过使用`Document`注
解中的`collection`属性改变。  

### 3. 编写仓储接口  

如同非反应式编程的JPA编写一样，我们需要扩展对应接口。我们可以选择扩展
`ReactiveCrudRepository`或`ReactiveMongoRepository`接口，两者的区别主要在于后者提供
了多个特殊的`insert`方法。  

> 如果出于某种原因，你希望使用非反应式的repository，那么可以通过让repository接口扩展
> CrudRepository或MongoRepository接口，而不是选择扩展ReactiveCrudRepository或
> ReactiveMongoRepository。这样我们就可以让repository返回带有Mongo注解的领域。  

我们采用扩展`ReactiveCrudRepository`编写基于MongoDB的仓储，并增加额外的查询方法。  

```java
public interface TacoRepository extends ReactiveCrudRepository<Taco, String> {
    Flux<Taco> findByNameOrderByRemark(String name, Pageable pageable);

    Flux<Taco> findByRemarkContaining(String remark, Sort sort);

    Mono<Integer> deleteByName(String name);
}
```  

其中我们扩展的若干的方法关于这三个方法的说明如下。  

- findByNameOrderByRemark：根据Name查询并按照Remark排序分页获取数据  
- findByRemarkContaining: 根据Remark模糊查询数据  
- deleteByName: 根据Name删除数据  

完成编写后为了能够进行测试，我们通过单元测试进行测试。其中摘取了一部分进行演示。  

```java
@Test
public void readsByNameCorrectly() {
    StepVerifier.create(repository.findByNameOrderByRemark("Mysql", PageRequest.of(0, 1)).map(x -> x.getName()))
        .expectNext("Mysql")
        .verifyComplete();
}
```  

至此就完成了针对MongoDB数据库的持久化。  

## 二、 MySQL持久化  

### 1. 前期准备  

为了能够接近于实战开发，我们将在上述基础上再增加额外的数据库，为了保证互相扫描注入我们
需要限定它们的命名空间。  

我们首先将MongoDB限定扫描的命名空间，新建一个`MongoConfiguration.java`文件，然后通过
`EnableReactiveMongoRepositories`属性进行约束。  

```java
@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.orvillex.reactordemo.repository.mongodb")
public class MongoConfiguration {
}
```  

后续如果是使用MongoDB进行持久化的需要放在`com.orvillex.reactordemo.repository.mongodb`
命名空间下。配置好MongoBD我们就开始安装R2DBC依赖并使用H2暂时替代MySQL以便于测试开发。我
们打开对应项目的pom.xml。  

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-r2dbc</artifactId>
</dependency>
<dependency>
	<groupId>io.r2dbc</groupId>
	<artifactId>r2dbc-h2</artifactId>
	<version>0.8.4.RELEASE</version>
</dependency>
```  

为了防止R2DBC扫描MongoDB的命名空间导致重叠，还需要针对其进行配置，我们新建
`MySQLConfiguration.java`文件，并通过`EnableR2dbcRepositories`进行配置。  

```java
@Configuration
@EnableR2dbcRepositories(basePackages = "com.orvillex.reactordemo.repository.mysql")
public class MySQLConfiguration {
    @Bean
	ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
		Resource schemaResource = new ClassPathResource("db/schema.sql");
		Resource dataResource = new ClassPathResource("db/data.sql");
		ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
		initializer.setConnectionFactory(connectionFactory);
		initializer.setDatabasePopulator(new ResourceDatabasePopulator(schemaResource, dataResource));
		return initializer;
	}
}
```  

其中我们可以看到除了配置了对应需要扫描的命名空间，我们还设置的初始化的脚本语
句。对于使用JPA的读者来说这本来可以通过设置指定初始化，但是在R2DBC暂时还不能
支持，所以需要我们手动进行初始化。完成初始化工作后我们还需要配置对应的属性。  

```yaml
logging.level.org.springframework.r2dbc=DEBUG
spring.datasource.url=r2dbc:h2:mem:///test?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
```

### 2. 映射模型  

具体使用方式类似于常规的方式，具体代码如下。  

```java
@Data
@With
@Table
@AllArgsConstructor
@NoArgsConstructor
public class Ingredient {
    @Id
    private Long id;
    private String name;
    private Type type;
}
```

熟悉的读者可以看到其中的注解属性都比较熟悉，这里就不在额外阐述。  

### 3. 编写仓储接口  

与MongoDB设置一样，依然是扩展`ReactiveCrudRepository`接口即可。  

```java
public interface IngredientRepository extends ReactiveCrudRepository<Ingredient, Long> {
    Flux<Ingredient> findByTypeOrderByName(Type type, Pageable pageable);

    Flux<Ingredient> findByNameContaining(String name, Sort sort);

    Flux<Ingredient> findByNameContainingAndType(String name, Type type);

    Mono<Integer> deleteByType(Type type);
}
```  

其中具体的写法如传统的JPA并无太大差距，如果存在自定义的查询语句依然可以通过`Query`注解属性
完成。接着我们针对上述接口进行单元测试。  

```java
StepVerifier.create(repository.findByTypeOrderByName(Type.WRAP, PageRequest.of(0, 1)))
    .expectNext(new Ingredient(2l, "Corn Tortilla", Type.WRAP))
    .verifyComplete();
```
