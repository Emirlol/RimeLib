plugins {
	alias(libs.plugins.kotlin)
	`maven-publish`
}

version = property("symbols_version") as String

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
			groupId = "me.ancientri.symbols"
			artifactId = "config-annotation"
			version = project.version as String
			from(components["java"])
		}
	}
}