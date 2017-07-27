# Random Object Generator

This project helps with generating random Java objects for unit/integration tests.

## Features

* Generates any arbitrary Java class.
* User can override setter methods for specific classes or globally for all classes.
* (Beta) Support for circular object references.
* Sequential IDs used for any setters ending in "Id" (when instantiating "IdAwareObjectGenerator").

## Usage

Create a new object generator.
```
ObjectGenerator generator = new IdAwareObjectGenerator();
```

Generate a random object.
```
generator.generate(MyClass.class);
```

Override a setter method for a **specific class**.
```
generator.addSetterOverride(MyClass.class, "setName", () -> "Denver");
generator.generate(MyClass.class);
```

Override a setter method for **all classes**.
```
generator.addSetterOverride("setName", () -> "Denver");
generator.generate(MyClass.class);
```

Override a setter method for a specific class for a single call to the generator.
```
Map<String, Callable> setterOverrides = new HashMap<>();
setterOverrides.put("setName", () -> "Denver");
generator.generate(MyClass.class, setterOverrides);
```

Provide constructor arguments.
```
generator.generate(MyClass.class, arg1, arg2, arg3, ...);
```

## Future work

* combine generator.generate(...) and generator.random(...) methods.

## Changelog

### Version 1.0.3 | Mar 10, 2016
* By default, strings in objects will be a single word from the
  dictionary, instead of some garbage impossible to read string.

### Version 1.0.2 | Nov 30, 2016
* Fixed a bug where collections of generated objects were all the same.

### Version 1.0.1 | Nov 14, 2016
* first release
