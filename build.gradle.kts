import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	java
	id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "de.terecian"
version = "0.0.1-SNAPSHOT"
description = "Media Share application for use with OBS"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.github.ben-manes.caffeine:caffeine:3.2.3")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("uk.co.caprica:vlcj:4.12.1")
	implementation("commons-codec:commons-codec:1.21.0")
	implementation("org.json:json:20231013")
	implementation("org.apache.commons:commons-compress:1.28.0")
	implementation("com.squareup.okhttp3:okhttp:5.2.1")}

tasks.withType<Test> {
	useJUnitPlatform()
}