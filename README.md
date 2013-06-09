Scalext
=======

[![Build Status](https://www.travis-ci.org/Rovak/Scalext.png?branch=master)](https://www.travis-ci.org/Rovak/Scalext)
[![Coverage Status](https://coveralls.io/repos/Rovak/Scalext/badge.png?branch=master)](https://coveralls.io/r/Rovak/Scalext?branch=master)

 Ext JS Driver for Play Framework 2.1

## Features

 * Supports single or batched JSON requests
 * Automatic serialization and deserialization of Ext.Direct actions
 * Direct API configuration using annotations
 * DSL for creating Ext JS objects
 * Parallel execution of batched requests

## Roadmap

 * Unit tests
 * Support for more frameworks
 * Expanding DSL

## Getting started

 Download the latest distributable jar [here](http://data.razko.nl/projects/scalext/) and put it in your `lib` folder

 Add the classes from which you want to generate a Direct Api to `conf/application.conf`, for example:

```
scalext.direct.classes="direct.ProfileForm,direct.RegistrationForm,direct.EchoService"
```

Include the javascript file which generates the Direct Api inside a view:

```scala
<script type="text/javascript" src="@com.scalext.controllers.routes.Api.buildApi()"></script>
```

 The Direct Api is now available!

## Configuring the Direct API

 Classes can be exposed to the Direct API by adding annotations to methods.

```scala
case class Profile(name: String, email: String, company: String)

@Remotable(name = "Scalext.example.Profile")
class Form {

  @Remotable
  def getBasicInfo(): FormResult = {
    var profile = Profile(
      "Roy van Kaathoven",
      "ik@royvankaathoven.nl",
      "Roy")

    FormResult(profile)
  }

  @FormHandler
  def updateBasicInfo(profile: Profile): FormResult = {
    FormResult(
      null,
      errors = Map("name" -> "wrong info"))
  }
}
```

```scala
@Remotable(name = "Scalext.example.Upload")
class Upload {

  @FormHandler
  def uploadFile(post: Any, files: Seq[TemporaryFile]): FormResult = {

    println("Post " + post)
    println("Files " + files)

    FormResult(Map("msg" -> s"${files.size} file(s) succesfully uploaded"))
  }

}
```

The `@Remotable` annotation marks a method as Direct Method, which means it will be exposed to the Direct API.
By default the method- or classname will be used, this can be overridden by setting the `name` parameter.

The `@FormHandler` annotation marks a method as as Form submit handler, which means it can handle form submissions and even file uploads.
If the method has to read file uploads then make sure the second parameter can handle the `Seq[TemporaryFile]` type.

## Documentation

 * [API](http://ci.razko.nl/job/Scalext/Documentation/index.html)
 * [Example project](https://github.com/Rovak/ScalextExample)