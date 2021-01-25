# 反应式编程  

## 一、 基础  

当今系统对于并发的要求越来越高，限于当前的命令式编程模型的约束。难以发挥最大化
的并发计算能力，为了能够弥补这一缺点，推出了反应式编程。而在Spring 5.0中已经
加入并支持。  

为了体现反应式编程与命令式编程的差异性，读者可以阅读以下两段代码。  

```java
String name = "Craig";
String capitalName = name.toUpperCase();
String greeting = "Hello, " + capitalName + "!";
System.out.println(greeting);
```  

使用命令式模型，每行代码执行一个步骤，按部就班，并且肯定在同一个线程中进行。每一
步在执行完成之前都会组织执行线程执行下一个。  

```java
Mono.just("Craig")
    .map(n -> n.toUpperCase())
    .map(cn -> "Hello, " + cn + "!")
    .subscribe(System.out::println);
```  

上述代码虽然看起来依然保持着按部执行的模型，但实际是数据会流过处理管线。具体执行在
哪个线程我们并不能判断。`Mono`是反应式类型其一，另一个则是`Just`，前者针对数据项
不超过一个的场景，后者则能够代表零个、一个或更多个。  

> 如果你熟悉RxJava或者ReactiveX，那么你可能认为Mono和Flux类似于Observable和Single。
> 事实上它们不仅在语义上大致相同，还共享了很多相同的操作符。所以Reactor和RxJava的类型
> 可以相互转换。  

如果是在`Spring Boot`中使用，则直接将以下依赖放入`pom.xml`中即可：  

```xml
<dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
</dependency>
<dependency>
	<groupId>io.projectreactor</groupId>
	<artifactId>reactor-test</artifactId>
	<scope>test</scope>
</dependency>
```

## 二、 使用  

### 1. 创建类型  

最普遍，也是最简单的方式就是根据对象创建。例如，下面的测试方法将从5个String对象中创建一个Flux：  

```java
Flux<String> fruitFlux = Flux.just("Apple", "Orange", "Grape", "Banana", "Strawberry");
```  

此时我们已经创建了Flux，但是它还没有订阅者。如果没有任何的订阅者，那么数据将不会流动。这就好比
你家的水龙头，在你没有打开的时候水是不会流动的。此时我们需要增加一个订阅者。  

```java
fruitFlux.subscribe(f -> System.out.println(f));
```  

通过打印控制台是一种好方法。实际测试Flux和Mono更好的方法是使用Reactor提供的StepVerifier。对于
给定的Flux和Mono，StepVerifier将会订阅该反应式类型，在数据流过时对数据断言，并在最后验证反应式
数据是否按预期完成。为了验证以上反应式类型，我们可以通过如下进行验证。  

```java
StepVerifier.create(fruitFlux)
    .expectNext("Apple")
    .expectNext("Orange")
    .expectNext("Grape")
    .expectNext("Banana")
    .expectNext("Strawberry")
    .expectComplete();
```

实际研发过程中往往需要从数组、List、Set和其他任意Iterable的实现来创建Flux，首先是根据数组的方
式创建Flux。  

```java
String[] fruits = new String[] {"Apple", "Orange", "Grape", "Banana", "Strawberry"};
StepVerifier.create(Flux.fromArray(fruits))
    .expectNext("Apple")
    .expectNext("Orange")
    .expectNext("Grape")
    .expectNext("Banana")
    .expectNext("Strawberry")
    .expectComplete();
```

而从Iterable创建可以通过`Flux.fromIterable`进行创建。  

### 2. 组合类型  

有时候，我们会需要操作两种反应式类型，并以某种方式将它们合并在一起。如果对于两个流的顺序没有
特别的要求，我们可以使用`mergeWith`合并两个流。  

```java
Flux<String> characterFlux = Flux.just("Garfield", "Kojak", "Barbossa")
    .delayElements(Duration.ofMillis(500));
Flux<String> foodFlux = Flux.just("Lasagna", "Lollipops", "Apples")
    .delaySubscription(Duration.ofMillis(250))
    .delayElements(Duration.ofMillis(500));
Flux<String> mergedFlux = characterFlux.mergeWith(foodFlux);
StepVerifier.create(mergedFlux)
    .expectNext("Garfield")
    .expectNext("Lasagna")
    .expectNext("Kojak")
    .expectNext("Lollipops")
    .expectNext("Barbossa")
    .expectNext("Apples")
    .expectComplete();
```  

上述代码我们通过`delaySubscription`和`delayElements`延迟了数据的推送，保证数据按照1对1的
顺序进行流动，但是实际场景下并不能保证这一特征，那么我们就可以使用`zip`方法进行合并。  

```java
Flux<String> zipedFlux = Flux.zip(characterFlux, foodFlux, (c, f) -> c + " eats " + f);
StepVerifier.create(zipedFlux)
    .expectNext("Garfield eats Lasagna")
    .expectNext("Kojak eats Lollipops")
    .expectNext("Barbossa eats Apples")
    .expectComplete();
```  

需要注意的是，与`mergeWith`方法不同，`zip`方法是一个静态的创建操作。创建出来的Flux会完美的
对齐。  

### 3. 转换过滤  

数据在从Flux流出时，进行过滤的最基本方法之一是简单地忽略第一批指定数目的数据项。`skip`操作
就能完成这样的工作。  

```java
Flux<String> skipFlux = Flux.just("one", "two", "skip a few", "nine", "hundred")
    .skip(3);
```  

往往有时候我们并不想跳过特定数量的条目，而是想要在一段时间之内跳过所有的第一批数据，此时就需要
`skip`操作的另一种形式。  

```java
Flux<String> skipDelayFlux = Flux.just("one", "two", "skip a few", "nine", "hundred")
    .delayElements(Duration.ofSeconds(1))
    .skip(Duration.ofSeconds(4));
```  

我们已经看过`skip`操作的示例，根据对此操作的描述来看，`take`可以认为是与`skip`相反的操作。

```java
Flux<String> takeFlux = Flux.just("yellow", "yosem", "grand", "zion", "teton").take(3);
StepVerifier.create(takeFlux).expectNext("yellow", "yosem", "grand");

Flux<String> takeDelayFlux = Flux.just("yellow", "yosem", "grand", "zion", "teton")
    .delayElements(Duration.ofSeconds(1))
    .take(Duration.ofMillis(3500));
```

这里我们直接将另一种根据时间获取数据的方式进行展现。对于过滤来说大部分情况下我们都需要灵活的
支持来对数据进行过滤筛选，那么我们就可以使用`filter`，此方法接受一个`Predicate`用于决定数据
是否通过Flux。  

```java
Flux<String> filterFlux = Flux.just("yellow", "yosem", "grand", "zion", "teton")
    .filter(p -> !p.contains("zion"));
```  

我们还可能想要过滤掉已经接收过的数据条目，可以采用`distinct`操作。  

### 4. 映射数据  

为了将数据转换为其他我们需要类型，我们就需要使用`map`操作。  

```java
Flux<String> mapFlux = Flux.just("yellow", "yosem", "grand")
    .map(n -> {
        return n + "s";
    });
```  

但是在每个数据项被源Flux发布时，`map`操作是同步执行的，如果你想要异步的转换过程，那么你
应该考虑使用`flatMap`操作。  

```java
Flux<String> flatMapFlux = Flux.just("yellow", "yosem", "grand")
    .flatMap(n -> Mono.just(n).map(p -> {
        return p + "s";
    }).subscribeOn(Schedulers.parallel())
);
```  

### 5. 缓存数据  

在处理流经Flux的数据时，你可能会发现将数据流拆分为小块会带来一定的收益。那么你可以使用
`buffer`操作解决这个问题。  

```java
Flux.just("yellow", "yosem", "grand")
    .buffer(2)
    .flatMap(x ->
        Flux.fromIterable(x)
        .map(y -> y + "s")
        .subscribeOn(Schedulers.parallel())
        .log()
    ).subscribe();
```  

其中`buffer`将会产生指定数量的`List`数据作为Flux的类型进行流经。如果我们需要将所有数
据转换为单个`List`对象我们可以使用无参数的`buffer`或`collectList`。除了转换为`List`
对象外，也可以使用`collectMap`将对象转换为`Mono<Map<?, ?>>`类型。  

### 6. 逻辑操作  

有时候我们想要知道由Mono或者Flux发布的条目是否满足某些条件，那么`all`和`any`方法可以
实现这样的逻辑。  

```java
Mono<Boolean> has = Flux.just("a1", "a2", "a3", "a4", "a5")
    .all(a -> a.contains("a"));

has = Flux.just("a1", "a2", "a3")
    .any(a -> a.contains("2"));
```  

具体的测试代码我们参考[本代码](../src/test/java/com/orvillex/reactordemo/task/ReactorTest.java)
