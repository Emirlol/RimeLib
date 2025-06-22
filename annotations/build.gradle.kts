plugins {
	alias(libs.plugins.kotlin)
	`maven-publish`
}

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