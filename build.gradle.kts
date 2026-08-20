plugins {
    id("dev.kikugie.loom-back-compat")
}

val modid: String = sc.properties["mod.id"]
val modname: String = sc.properties["mod.name"]
val modversion: String = sc.properties["mod.version"]
val moddescription: String = sc.properties["mod.description"]
val mcversion: String = sc.current.version
val versionrange: String = sc.properties["mod.mc_compat"]
val loaderversion: String = sc.properties["deps.fabric_loader"]
val walksylibversion: String = sc.properties["deps.walksylib"]

version = "$modversion+$mcversion"
group = sc.properties.get<String>("mod.group")
base.archivesName = modid

val requiredJava: JavaVersion = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    else -> JavaVersion.VERSION_21
}

val versionedMixins: List<String> = buildList {
    if (sc.current.parsed >= "1.21.4") add("IsUsingItemMixin") else add("ItemPropertiesMixin")

    if (sc.current.parsed matches ">=1.21.2 <1.21.4") add("PlayerRenderStateMixin") else add("AvatarRendererMixin")
}

val skippedMixins: List<String> =
    listOf("IsUsingItemMixin", "ItemPropertiesMixin", "PlayerRenderStateMixin", "AvatarRendererMixin") - versionedMixins.toSet()

sourceSets.main {
    java.exclude(skippedMixins.map { "**/mixin/$it.java" })
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/") { name = "FabricMC" }
    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
        filter { includeGroup("maven.modrinth") }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$mcversion")
    loomx.applyMojangMappings()

    modImplementation("net.fabricmc:fabric-loader:$loaderversion")
    implementation("maven.modrinth:walksylib:$walksylibversion")
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")

    runConfigs.all {
        preferGradleTask = true
        runDirectory = rootProject.file("run")
    }
    runConfigs.remove(runConfigs["server"])
}

java {
    withSourcesJar()
    sourceCompatibility = requiredJava
    targetCompatibility = requiredJava

    toolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion)
    }
}

tasks {
    withType<JavaCompile>().configureEach {
        options.release = requiredJava.majorVersion.toInt()
    }

    processResources {
        val props = mapOf(
            "mod_id" to modid,
            "mod_name" to modname,
            "mod_version" to version,
            "mod_description" to moddescription,
            "minecraft_version_range" to versionrange,
            "versioned_mixins" to versionedMixins.sorted().joinToString(",\n    ") { "\"$it\"" }
        )

        inputs.properties(props)

        filesMatching(listOf("fabric.mod.json", "$modid.mixins.json")) { expand(props) }
    }

    jar {
        from(rootProject.file("LICENSE")) {
            rename { "${it}_$modid" }
        }
    }
}
