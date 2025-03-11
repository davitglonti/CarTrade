plugins {
	java
	id("org.springframework.boot") version "3.5.0-M1"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "davlaga"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(23)
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
	compileOnly("org.projectlombok:lombok:1.18.36")
	annotationProcessor("org.projectlombok:lombok:1.18.36")

	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.postgresql:postgresql:42.7.4")
	implementation("org.liquibase:liquibase-core:4.30.0")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("com.nimbusds:nimbus-jose-jwt:10.0.1")

	implementation("software.amazon.awssdk:s3:2.20.129")
	implementation("software.amazon.awssdk:auth:2.20.129")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.mockito:mockito-core:5.7.0")
	testImplementation("org.mockito:mockito-junit-jupiter:5.7.0")



}

tasks.withType<Test> {
	useJUnitPlatform()
}
