# WebFlux教程  

## 一、框架介绍  

考虑到在Spring MVC基础上支持反应式需要大量的工作，为了降低工作量直接摆脱了
现有的框架而是采用独立的Spring WebFlux来实现控制器层面的反应式支持。如果
我们需要基于反应式的方式开发控制器我们需要单独增加额外的引用至pom文件中。  

本例中的相关代码可访问[Github](https://github.com/yaozhenfa/ReactorDemo)  

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```  

> 尽管Spring WebFlux控制器通常会返回Mono和Flux，但是这并不意味着Spring 
> MVC无法体验反应式类型的乐趣。如果你愿意，那么Spring MVC也可以返回Mono和
> Flux。  
> 主要区别就是Spring WebFlux是真正的反应式框架，允许在事件轮询中处理请求；
> 而Spring MVC是基于Servlet的，依赖于多线程来处理多个请求。  

## 二、开发控制器  

最简单的开发方式就是基于Spring MVC原有的控制器将其中的返回值进行修改来完成
最快的转换方式，比如下述方式。  

```java
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/ingredient")
public class IngredientController {
    private final IngredientRepository repository;

    @GetMapping
    public Flux<Ingredient> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Ingredient> find(@PathVariable("id") Long id) {
        return repository.findById(id);
    }
}
```  

但是这种基于注解的编程模型虽然从Spring 2.5就存在了，但是它也有一些缺点。所有
注解属性本身定义了该做什么，而具体如何去做则是在框架代码的其他部分定义的。如果
想要进行自定义或扩展，编程模型就会变得很复杂，因为这样的变更需要修改注解之外的
代码。为此Spring 5引入了一个新的函数式编程模型。其中主要有以下几个核心类型。  

- RequestPredicate: 声明要处理的请求类型  
- RouterFuncation: 声明如何将请求路由到处理器代码中  
- ServerRequest: 代表一个HTTP请求，包括对请求头和请求体的访问  
- ServerResponse: 代表一个HTTP响应，包括响应头和响应体信息  

介绍完主要的核心类型后我们通过函数式的方式在编写一个控制器。  

```java
@Configuration
public class TacoRouteFunctionConfig {
    @Autowired
    private TacoRepository repository;
    
    @Bean
    public RouterFunction<?> routerFunctions() {
        return RouterFunctions.route(RequestPredicates.GET("/taco"), this::getAll);
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse.ok()
            .body(repository.findAll(), Taco.class);
    }
}
```

通过上述代码我们可以清楚的看到所有的控制权均在用户手中，并且可以在任意地方定义
控制器的实现代码，而仅仅只需要通过函数式的方式赋值即可绑定。  

## 三、测试控制器  

为了测试反应式控制，在Spring 5中也为我们增加了对应的类`WebTestClient`以便于
我们测试具体的接口。下面我们就以上面所提及的接口为例进行测试。  

```java
@Test
public void shouldSaveIngredient() {
    Ingredient ingredient = new Ingredient(1l, "First", Type.SAUCE);
    Mono<Ingredient> monoIngredient = Mono.just(ingredient);

    IngredientRepository repo = Mockito.mock(IngredientRepository.class);
    when(repo.save(any())).thenReturn(monoIngredient);

    WebTestClient testClient = WebTestClient.bindToController(
        new IngredientController(repo)).build();

    testClient.post().uri("/ingredient")
        .contentType(MediaType.APPLICATION_JSON)
        .body(monoIngredient, Ingredient.class)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Ingredient.class)
        .isEqualTo(ingredient);
}
```

上述我们可以看到这里通过`Mockito`对仓储接口进行了模拟，然后通过`WebTestClient`的
`bindToController`进行了绑定，完成绑定后我们就可以开始模拟请求并针对回应接口进行测试。
