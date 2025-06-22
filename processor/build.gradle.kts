plugins {
	alias(libs.plugins.kotlin)
	alias(libs.plugins.ksp)
	`maven-publish`
}

version = property("symbols_version") as String

dependencies {
	implementation(libs.symbolProcessingApi)
	libs.bundles.kotlinpoet.get().map(::implementation)
	implementation(project(":annotations"))
}

publishing {
	publications {
		create<MavenPublication>("maven") {
			groupId = "me.ancientri.symbols"
			artifactId = "config-processor"
			version = project.version as String
			from(components["java"])
		}
	}
}