import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin

plugins {
	alias(libs.plugins.kotlin)
	alias(libs.plugins.loom)
	alias(libs.plugins.ksp)
	`maven-publish`
}

group = properties["group"] as String
version = "${properties["version"]}+${libs.versions.minecraft.get()}"

dependencies {
	minecraft(libs.minecraft)
	mappings(variantOf(libs.yarnMappings) { classifier("v2") })
	modImplementation(libs.fabricLoader)
	modImplementation(libs.fabricApi)
	modImplementation(libs.fabricLanguageKotlin)

	include(implementation(libs.apacheText.get())!!)

	compileOnly(libs.mcdev)
	compileOnly(libs.init.annotation)
	compileOnly(project(":annotations"))

	ksp(libs.init.processor)

	api(libs.pods4k)
	include(implementation(libs.pods4k.core.get())!!)
	include(implementation(libs.pods4k.transformations.get())!!)

	testImplementation(libs.fabricLoaderJunit)
}

allprojects {
	version = property("version") as String

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

	plugins.withType<JavaPlugin> {
		extensions.configure<JavaPluginExtension>() {
			withSourcesJar()
		}
	}

	plugins.withType<KotlinBasePlugin> {
		extensions.configure<KotlinJvmProjectExtension> {
			jvmToolchain(21)
		}
	}
}

loom {
	accessWidenerPath = project.file("src/main/resources/rimelib.accesswidener")
}

base {
	archivesName = properties["archives_base_name"] as String
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

	jar {
		from("LICENSE") {
			rename { "${it}_${project.base.archivesName.get()}" }
		}
	}

	test {
		useJUnitPlatform()
	}
}

publishing {
	repositories {
		maven("https://ancientri.me/maven/releases") {
			name = "AncientRime"
			credentials(PasswordCredentials::class)
			authentication {
				create<BasicAuthentication>("basic")
			}
		}
	}
	publications {
		create<MavenPublication>("maven") {
			groupId = project.group as String
			artifactId = project.base.archivesName.get()
			version = project.version as String
			from(components["java"])
		}
	}
}