import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	alias(libs.plugins.kotlin)
	alias(libs.plugins.loom)
	alias(libs.plugins.ksp)
	`maven-publish`
}

group = properties["group"] as String
version = "${properties["version"]}+${libs.versions.minecraft.get()}"

repositories {
	mavenCentral()
	exclusiveContent {
		forRepositories(
			maven("https://ancientri.me/maven/releases") {
				name = "AncientRime"
			}
		)
		filter {
			@Suppress("UnstableApiUsage")
			includeGroupAndSubgroups("me.ancientri")
		}
	}
}

dependencies {
	minecraft(libs.minecraft)
	mappings(variantOf(libs.yarnMappings) { classifier("v2") })
	modImplementation(libs.fabricLoader)
	modImplementation(libs.fabricApi)
	modImplementation(libs.fabricLanguageKotlin)

	include(implementation(libs.apacheText.get())!!)
	include(implementation(libs.apacheMath4.get())!!)
	include(implementation(libs.pods4k.get())!!)

	compileOnly(libs.mcdev)
	compileOnly(libs.init.annotation)
	ksp(libs.init.processor)

	testImplementation(libs.fabricLoaderJunit)
}

loom {
	accessWidenerPath = project.file("src/main/resources/rimelib.accesswidener")
}

base {
	archivesName = properties["archives_base_name"] as String
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()
}

kotlin {
	jvmToolchain(21)
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
	}

	withType<KotlinCompile>().configureEach {
		compilerOptions {
			freeCompilerArgs.add("-Xsuppress-warning=NOTHING_TO_INLINE")
		}
	}

	jar {
		from("LICENSE") {
			rename { "${it}_${project.base.archivesName.get()}" }
		}
	}

	test {
		useJUnitPlatform()
	}
}
