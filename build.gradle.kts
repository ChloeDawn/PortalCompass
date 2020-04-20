import java.nio.charset.StandardCharsets

plugins {
  java
  id("fabric-loom") version "0.2.7-SNAPSHOT"
}

group = "io.github.chloedawn"
version = "0.1.0"

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

minecraft {
  refmapName = "mixins/portalcompass/refmap.json"
  runDir = "run"
}

dependencies {
  minecraft("com.mojang:minecraft:1.15.2")
  mappings("net.fabricmc:yarn:1.15.2+build.14")
  modImplementation("net.fabricmc:fabric-loader:0.7.8+build.187")
  fabricApi(name = "api-base", version = "0.1.3+12a8474cad")
  fabricApi(name = "networking-v0", version = "0.1.9+d249f7b6d1")
  fabricApi(name = "registry-sync-v0", version = "0.2.7+9421522de7")
  fabricApi(name = "resource-loader-v0", version = "0.1.13+94c7834aad")
  implementation("org.jetbrains:annotations:19.0.0")
  implementation("org.checkerframework:checker-qual:3.2.0")
}

tasks.withType<ProcessResources> {
  filesMatching("fabric.mod.json") {
    expand("version" to version)
  }
}

tasks.withType<JavaCompile> {
  options.run {
    isFork = true
    isVerbose = true
    encoding = StandardCharsets.UTF_8.displayName()
    compilerArgs.addAll(listOf(
      "-Xlint:all",
      "-Xmaxerrs", "${Int.MAX_VALUE}",
      "-Xmaxwarns", "${Int.MAX_VALUE}",
      "-XprintProcessorInfo",
      "-XprintRounds"
    ))
  }
}

fun DependencyHandlerScope.fabricApi(name: String, version: String) {
  modImplementation(group = "net.fabricmc.fabric-api", name = "fabric-$name", version = version)
  include(group = "net.fabricmc.fabric-api", name = "fabric-$name", version = version)
}
