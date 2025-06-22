plugins {
	alias(libs.plugins.kotlin)
	`maven-publish`
}

version = property("symbols_version") as String

publishing {
	publications {
		create<MavenPublication>("maven") {
			groupId = "me.ancientri.symbols"
			artifactId = "config-annotation"
			version = project.version as String
			from(components["java"])
		}
	}
}