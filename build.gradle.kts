
import magik.createGithubPublication
import magik.github
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	alias(libs.plugins.kotlin)
	alias(libs.plugins.loom)
	alias(libs.plugins.ksp)
	alias(libs.plugins.magik)
	`maven-publish`
}

version = "${libs.versions.modVersion.get()}+${libs.versions.minecraft.get()}"
group = project.property("maven_group") as String

repositories {
	mavenCentral()
	exclusiveContent {
		forRepository {
			mavenLocal() // The github repo has jvm compatibility issues, so install it locally first.
//			github("Emirlol/Maven")
		}
		filter {
			@Suppress("UnstableApiUsage")
			includeGroupAndSubgroups("me.rime")
		}
	}
}

dependencies {
	minecraft(libs.minecraft.get())
	mappings("net.fabricmc:yarn:${libs.versions.yarnMappings.get()}:v2")
	modImplementation(libs.fabricLoader.get())
	modImplementation(libs.fabricApi.get())

	modImplementation(libs.fabricLanguageKotlin.get())
	include(implementation(libs.apacheText.get())!!)
	include(implementation(libs.apacheMath4.get())!!)
	compileOnly(libs.mcdev.get())

	compileOnly(libs.initAnnotation)
	ksp(libs.initProcessor.get())

	testImplementation(libs.fabricLoaderJunit.get())
}

loom {
	accessWidenerPath = project.file("src/main/resources/rimelib.accesswidener")
}

base {
	archivesName = "rimelib"
}

val targetJavaVersion = 21
java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(targetJavaVersion)
		vendor = JvmVendorSpec.ADOPTIUM
	}
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()
}

tasks {
	processResources {
		val props = mapOf(
			"version" to project.version as String,
			"minecraft_version" to libs.versions.minecraft.get(),
			"loader_version" to libs.versions.fabricLoader.get(),
			"flk_version" to libs.versions.fabricLanguageKotlin.get()
		)

		inputs.properties(props)
		filteringCharset = "UTF-8"

		filesMatching("fabric.mod.json") {
			expand(props)
		}
	}

	withType<JavaCompile>().configureEach {
		// ensure that the encoding is set to UTF-8, no matter what the system default is
		// this fixes some edge cases with special characters not displaying correctly
		// see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
		// If Javadoc is generated, this must be specified in that task too.
		options.encoding = "UTF-8"
		options.release.set(targetJavaVersion)
	}

	withType<KotlinCompile>().configureEach {
		compilerOptions {
			jvmTarget = JvmTarget.fromTarget(targetJavaVersion.toString())
		}
	}

	jar {
		from("LICENSE") {
			rename { "${it}_${project.base.archivesName}" }
		}
	}

	test {
		useJUnitPlatform()
	}
}

publishing {
	publications {
		createGithubPublication {
			groupId = project.group as String
			artifactId = project.base.archivesName.get()
			version = project.version as String

			from(components["java"])
		}
	}
	repositories {
		github {
			name = "RimeMaven"
			domain = "Emirlol/Maven"
		}
	}
}
