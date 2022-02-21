# swgl-core ![GitHub](https://img.shields.io/github/license/Over-run/swgl-core)

[![Java CI with Gradle](https://github.com/Over-Run/swgl-core/actions/workflows/gradle.yml/badge.svg?branch=0.x&event=push)](https://github.com/Over-Run/swgl-core/actions/workflows/gradle.yml)  
![GitHub all releases](https://img.shields.io/github/downloads/Over-Run/swgl-core/total)

![GitHub issues](https://img.shields.io/github/issues/Over-Run/swgl-core)
![GitHub pull requests](https://img.shields.io/github/issues-pr/Over-Run/swgl-core)  
![GitHub closed issues](https://img.shields.io/github/issues-closed/Over-Run/swgl-core)
![GitHub closed pull requests](https://img.shields.io/github/issues-pr-closed/Over-Run/swgl-core)

![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/Over-Run/swgl-core)
![Maven Central](https://img.shields.io/maven-central/v/io.github.over-run/swgl-core)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/io.github.over-run/swgl-core?server=https%3A%2F%2Fs01.oss.sonatype.org)

![Java Version](https://img.shields.io/badge/Java%20Version-17-red)

![GitHub Discussions](https://img.shields.io/github/discussions/Over-Run/swgl-core)

swgl - A game engine:coffee:.

[If there are any bugs, tell us!](https://github.com/Over-Run/swgl-core/issues/new)

## What's going change

1. Remove `render()` in mesh, the mesh is the descriptor of the vertex data.
2. Remove `Geometry`, replace with `SimpleModel`
3. Render meshes in `*Model`
4. Add `SimpleModel`, `obj.ObjModel`, etc.

## Use for depending on

```groovy
dependencies {
    implementation "io.github.over-run:swgl-core:${current_version}"
}
```

## JVM Args

`-Dswgl.coreProfile=false`: Disable OpenGL 3.2 core profile

## Example

```java
public class Example {
//TODO
}
```
