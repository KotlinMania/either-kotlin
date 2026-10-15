# either-kotlin

[![GitHub link](https://img.shields.io/badge/GitHub-KotlinMania%2Feither--kotlin-blue.svg)](https://github.com/KotlinMania/either-kotlin)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kotlinmania/either-kotlin)](https://central.sonatype.com/artifact/io.github.kotlinmania/either-kotlin)
[![Build status](https://img.shields.io/github/actions/workflow/status/KotlinMania/either-kotlin/ci.yml?branch=main)](https://github.com/KotlinMania/either-kotlin/actions)

This is a Kotlin Multiplatform line-by-line transliteration port of [`rayon-rs/either`](https://github.com/rayon-rs/either).

**Original Project:** This port is based on [`rayon-rs/either`](https://github.com/rayon-rs/either). All design credit and project intent belong to the upstream authors; this repository is a faithful port to Kotlin Multiplatform with no behavioural changes intended.

### Installation

```kotlin
dependencies {
    implementation("io.github.kotlinmania:either-kotlin:0.1.1")
}
```

### Building

```bash
./gradlew build
./gradlew test
```

### License

This Kotlin port is dual-licensed under either of [Apache License, Version 2.0](LICENSE-APACHE) or [MIT license](LICENSE-MIT), at your option, matching upstream license terms.
