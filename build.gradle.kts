import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin

plugins {
	alias(libs.plugins.kotlin)
	alias(libs.plugins.loom)
	alias(libs.plugins.ksp)
	alias(libs.plugins.modPublish)
	`maven-publish`
}

group = properties["group"] as String
version = if (hasProperty("snapshot")) {
	"${properties["version"]}+${libs.versions.minecraft.get()}-SNAPSHOT"
} else {
	"${properties["version"]}+${libs.versions.minecraft.get()}"
}

dependencies {
	minecraft(libs.minecraft)
	mappings(variantOf(libs.yarnMappings) { classifier("v2") })
	modImplementation(libs.fabricLoader)
	modImplementation(libs.fabricApi)
	modImplementation(libs.fabricLanguageKotlin)

	compileOnly(libs.mcdev)
	compileOnly(project(":annotations"))

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
		maven {
			name = "AncientRime"
			url = uri(
				if (project.hasProperty("snapshot")) "https://ancientri.me/maven/snapshots"
				else "https://ancientri.me/maven/releases"
			)
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

publishMods {
	file = tasks.remapJar.get().archiveFile
	modLoaders.add("fabric")
	type = STABLE
	displayName = "RimeLib ${project.version}"
	changelog = ""

	modrinth {
		accessToken = providers.environmentVariable("MODRINTH_TOKEN")
		projectId = "ayoldSYl"
		minecraftVersions.addAll(libs.versions.minecraft.get())
		requires("fabric-language-kotlin")
		requires("fabric-api")
		projectDescription = providers.fileContents(layout.projectDirectory.file("README.md")).asText
		featured = true
	}
}