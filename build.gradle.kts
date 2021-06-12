import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.5.10"

    id("org.springframework.boot") version "2.5.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    war
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("plugin.noarg") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion
    kotlin("kapt") version kotlinVersion
}

group = "com.cococloudy"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    // managed dependencies version
    val jsonwebtokenVersion = "0.11.2"
    val okhttpVersion = "4.9.1"
    val openApiVersion = "1.5.6"
    val querydslVersion = "4.4.0"

    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    implementation("io.jsonwebtoken:jjwt-api:$jsonwebtokenVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jsonwebtokenVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jsonwebtokenVersion")
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("org.springdoc:springdoc-openapi-ui:$openApiVersion")
    implementation("org.springdoc:springdoc-openapi-kotlin:$openApiVersion")
    implementation("com.querydsl:querydsl-jpa")
    kapt("com.querydsl:querydsl-apt:$querydslVersion:jpa")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor(group = "com.querydsl", name = "querydsl-apt", classifier = "jpa")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

sourceSets["main"].withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
    kotlin.srcDir("$buildDir/generated/source/kapt/main")
}