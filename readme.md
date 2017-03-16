# Maven plugin #

### Verifies that a project does not contain identical versions of Flyway(https://flywaydb.org/) sql and java migrations. ###

### Add plugin to existing project ###
* In POM file, add the plugin to build plugins section as shown below.

```xml
<build>
		<pluginManagement>
			<plugins>
				<plugin>
					<groupId>mx.iwa</groupId>
					<artifactId>flyway-maven-plugin</artifactId>
					<version>0.0.1</version>
					<executions>
						<execution>
							<phase>compile</phase>
							<goals>
								<goal>flyway-check</goal>
							</goals>
						</execution>
					</executions>
					<configuration>
						<sql>./src/main/resources/db/migration/</sql>
						<java>./src/main/java/db/migration/</java>
						<prefix>V</prefix>
						<separator>__</separator>
					</configuration>
				</plugin>
			</plugins>
		</pluginManagement>
	</build>
```

	
* Set plugin properties in configuration section.
	* In plugin configuration section, specify the folders containing sql and java migrations. By default, plugin will look in folders "./src/main/resources/db/migration/" and "./src/main/java/db/migration".
	* If necessary, change the default flyway naming strategy for migrations, prefix and separator properties. Example of default naming:  "V1_1__initialization.sql".