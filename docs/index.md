---
layout: default
---

<br>

[![Github](https://img.shields.io/badge/github-manifold-blue.svg)](https://github.com/manifold-systems/manifold)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/systems.manifold/manifold-all/badge.svg)](https://maven-badges.herokuapp.com/maven-central/systems.manifold/manifold-all)
[![IJ Plugin](https://img.shields.io/jetbrains/plugin/d/10057-manifold.svg)](https://plugins.jetbrains.com/plugin/10057-manifold)
[![Gitter](https://badges.gitter.im/manifold-systems/community.svg)](https://gitter.im/manifold-systems/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

## What is Manifold?
[Manifold](http://manifold.systems) plugs into Java to supplement it with game-changing features, from Type-safe Metaprogramming,
Structural Typing, and Extension Methods to an integrated Template Engine and a Preprocessor. All fully supported in IntelliJ IDEA.
Simply add Manifold to your project and begin taking advantage of it.

## What can you do with Manifold?

### [**Ⓘ** Meta-programming](https://github.com/manifold-systems/manifold/tree/master/manifold-core-parent/manifold)
Use the framework to gain direct, type-safe access to <i>any</i> type of metadata, such as **GraphQL**, **JSON Schema**
and **YAML**. Remove the code gen step in your build process. [&nbsp;**▶**&nbsp;Check&nbsp;it&nbsp;out!](http://manifold.systems/images/graphql.mp4)
```java
// Use your User.json schema file directly as a type, no code gen!
User user = User.builder("myid", "mypassword", "Scott")
  .withGender(male)
  .withDob(LocalDate.of(1987, 6, 15))
  .build();
User.request("http://api.example.com/users").postOne(user);
```

### [**Ⓘ** Extensions](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-ext)
Add extension methods to existing Java classes, even String, List, and File. Eliminate boilerplate code. [&nbsp;**▶**&nbsp;Check&nbsp;it&nbsp;out!](http://manifold.systems/images/ExtensionMethod.mp4)
```java
String greeting = "hello";
greeting.myMethod(); // Add your own methods to String!
```

### [**Ⓘ** Preprocessor](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-preprocessor)
Use familiar directives such as `#define` and `#if` to conditionally compile your Java projects. The preprocessor offers
a simple and convenient way to support multiple build targets with a single codebase. [&nbsp;**▶**&nbsp;Check&nbsp;it&nbsp;out!](http://manifold.systems/images/preprocessor.mp4)
```java
#if JAVA_8_OR_LATER
  @Override
  public void setTime(LocalDateTime time) {...)
#else
  @Override
  public void setTime(Calendar time) {...}
#endif
```   

### [**Ⓘ** Structural Typing](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-ext#structural-interfaces-via-structural)
Unify disparate APIs. Bridge software components you do not control. Access maps through type-safe interfaces. [&nbsp;**▶**&nbsp;Check&nbsp;it&nbsp;out!](http://manifold.systems/images/structural%20typing.mp4)
```java
Map<String, Object> map = new HashMap<>();
MyThingInterface thing = (MyThingInterface) map; // O_o
thing.setFoo(new Foo());
Foo foo = thing.getFoo();
out.println(thing.getClass()); // prints "java.util.HashMap"
```
 
### [**Ⓘ** Type-safe Reflection](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-ext#type-safe-reflection-via-jailbreak)
Access private features with <b>@Jailbreak</b> to avoid the drudgery and vulnerability of Java reflection. [&nbsp;**▶**&nbsp;Check&nbsp;it&nbsp;out!](http://manifold.systems/images/jailbreak.mp4) 
```java
@Jailbreak Foo foo = new Foo();
// Direct, *type-safe* access to *all* foo's members
foo.privateMethod(x, y, z);
foo.privateField = value;
```

### [**Ⓘ** Checked Exception Handling](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-exceptions)
You now have an option to make checked exceptions behave like unchecked exceptions!  No more unintended exception
swallowing, no more boilerplate `try`/`catch`/`wrap`/`rethrow` nonsense.
```java
List<String> strings = ...;
List<URL> urls = list
  .map(URL::new) // No need to handle the MalformedURLException!
  .collect(Collectors.toList());
```

### [**Ⓘ** String Templates](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-strings)
Embed variables and expressions in String literals, no more clunky string concat! [&nbsp;**▶**&nbsp;Check&nbsp;it&nbsp;out!](http://manifold.systems/images/string_interpolation.mp4)
```java
int hour = 15;
// Simple variable access with '$'
String result = "The hour is $hour"; // Yes!!!
// Use expressions with '${}'
result = "It is ${hour > 12 ? hour-12 : hour} o'clock";
``` 
       
### [**Ⓘ** Template Files with *ManTL*](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-templates)
Author template files with the full expressive power of Java, use your templates directly in your code as types.
Supports type-safe inclusion of other templates, shared layouts, and more. [&nbsp;**▶**&nbsp;Check&nbsp;it&nbsp;out!](http://manifold.systems/images/mantl.mp4)
```java
List<User> users = ...;
String content = abc.example.UserSample.render(users);
```
A template file `abc/example/UserSample.html.mtl`
```html
<%@ import java.util.List %>
<%@ import com.example.User %>
<%@ params(List<User> users) %>
<html lang="en">
<body>
<% users.stream()
   .filter(user -> user.getDateOfBirth() != null)
   .forEach(user -> { %>
    User: ${user.getName()} <br>
    DOB: ${user.getDateOfBirth()} <br>
<% }); %>
</body>
</html>
```

### [**Ⓘ** Libraries](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-ext#extension-libraries)
Leverage stock Manifold extension libraries for standard Java classes. Save time and reduce boilerplate code.
```java
File file = new File(path);
// Use refreshing extensions to File
String content = file.readText();
```

### [**Ⓘ** IntelliJ](http://manifold.systems/docs.html#ide--intellij-idea)
Use the [Manifold IntelliJ IDEA plugin]((https://plugins.jetbrains.com/plugin/10057-manifold) ) to fully leverage
Manifold in your development cycle. The plugin provides comprehensive support for IntelliJ features including code
completion, navigation, usage searching, refactoring, incremental compilation, hotswap debugging, full-featured
template editing, integrated preprocessor, and more.

<p><img src="http://manifold.systems/images/ManifoldPlugin.png" alt="manifold ij plugin" width="60%" height="60%"/></p>

### [**Ⓘ** Projects](http://manifold.systems/projects.html)
The Manifold project consists of the core Manifold framework and a collection of sub-projects implementing SPIs provided
by the core framework. Each project represents a separate **dependency** you can easily add to your project:

[Manifold : _Core_](https://github.com/manifold-systems/manifold/tree/master/manifold-core-parent/manifold)<br>

[Manifold : _GraphQL_](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-graphql)<br>
[Manifold : _JSON_](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-json)<br>
[Manifold : _YAML_](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-yaml)<br>
[Manifold : _Properties_](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-properties)<br>
[Manifold : _Image_](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-image)<br>
[Manifold : _Dark Java_](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-darkj)<br>
[Manifold : _JavaScript_](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-js)<br>

[Manifold : _Java Extension_](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-ext)<br>

[Manifold : _Templates (ManTL)_](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-templates)<br>

[Manifold : _String Interpolation_](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-strings)<br>
[Manifold : _[Un]checked Exceptions_](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-exceptions)<br>

[Manifold : _Preprocessor_](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-preprocessor)<br>

[Manifold : _Collections_](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-collections)<br>
[Manifold : _I/0_](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-io)<br>
[Manifold : _Text_](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-text)<br>

[Manifold : _All&nbsp;&nbsp;(Über jar)_](https://github.com/manifold-systems/manifold/tree/master/manifold-all)<br>

>Experiment with sample projects:<br>
>* [Manifold : _Sample App_](https://github.com/manifold-systems/manifold-sample-project)<br>
>* [Manifold : _Sample GraphQL App_](https://github.com/manifold-systems/manifold-sample-graphql-app)<br>
>* [Manifold : _Sample REST API App_](https://github.com/manifold-systems/manifold-sample-rest-api)<br>
>* [Manifold : _Sample Web App_](https://github.com/manifold-systems/manifold-sample-web-app)
<br>

### [**Ⓘ** Learn More](http://manifold.systems/docs.html)
<br>