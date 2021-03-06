# node-spigot-bridge

A minecraft server plugin to enable other plugins to be written in TypeScript or JavaScript.
(Or any other language where a transpiler or compiler can be made to build a Node.js module.)

## Dependencies

The system running the plugin must have *Java 9* or greater running the Minecraft server, as well as *[Node.js 10](https://nodejs.org/)* or later installed.

## Installing

Get a hold of the JAR file and place it in the plugins folder. After the first run a couple of directories and files will be created if they did not exist before, these are
- `plugins/NodeSpigotBridge/`
  - `config.yml` - *see [configuration](#configuration)*
  - `NodeBridge.js` - *overwritten with latest every startup*
- `node-plugins/`
  - `package.json` - *see [plugin package](#plugin-package)*

The JAR file may be released as a, well, "[release](https://github.com/TimLuq/node-spigot-bridge/releases)" at the GitHub repository or could be built from source using the following toolchains:
 - [TypeScript](https://www.typescriptlang.org/) - *a transpiler and ecosystem providing strong typing to JavaScript*
 - [rollup.js](https://rollupjs.org) - *a bundler for web or javascript projects*
 - [Gradle](https://gradle.org/) - *a tool for primarily java development and building*

### Building

Prepare by downloading the source:
```sh
> git clone https://github.com/TimLuq/node-spigot-bridge.git
```

Then place `spigot-api-shaded.jar` into the `node-spigot-bridge/libs` directory.
```sh
> cp Spigot/Spigot-API/target/spigot-api*-shaded.jar node-spigot-bridge/libs/spigot-api-shaded.jar
```

To build run `gradle build` from the project directory as displayed here:
```sh
> cd "node-spigot-bridge"
> gradle build; gradle build
```

This will create a JAR file at `node-spigot-bridge/build/libs/node-spigot-bridge-*.jar` where the asterisk is the version of `node-spigot-bridge`.

## Configuration

The file `plugins/NodeSpigotBridge/config.yml` contains only a few configurable options:
- `directory: node-plugins`
  *# which directory to use for finding packages (see [plugin package](#plugin-package)), relative to working directory of server*
- `executable: node`
  *# which executable should act as the Node.js process, may be an absolute path*

## Plugin package

The `package.json` file used is located at `node-plugins/package.json` if not configured to be another path.
This file conatins information about which plugins are installed and which should be loaded.

You may manually change dependencies in this file but I would recommand a package manager, such as `yarn`.

Additionally, when neccessary, this file may contain one of the two optional fields `spigotmc` or `main` to describe which module should handle plugin loading if the default behavior is lacking in flexibility.

### Package management

A great way to install and manage plugins is using a package manager. Among the most common are `yarn` and `npm`.
Both of these use `npm-registry` to resolve packages and versions by default. It is open for anyone to publish their public packages to `npm-registry` without charge.

The recommended package manager is [`yarn`](https://yarnpkg.com), due to its ability to handle flat dependency graphs.

#### Flat depenency graphs

The default `package.json` sets the option `flat: true` to signal use of a flat dependency graph to package managers supporting this feature.

A flat dependency graph forces all plugins to use the same version of common libraries; lowering RAM usage and giving a slight improvement to performance.
The drawback is that this is hard to solve if not impossible when two plugins, one being activly maintained while the other has stagnated, depend on incompatible versions of the same library.

Unless such a dependency conflict is noticed, the recommendation is to use flat dependencies.

## Plugin development

A plugin is a `Node.js` module exporting a subclass of `Plugin` from `spigot-bridge` as `default`.

### Plugin example

```javascript
import { Plugin, Player } from "spigot-bridge";

export default class SpamBotPlugin extends Plugin {
    constructor() {
        // if name or version are undefined or the empty string
        // info will be loaded from package.json
        this.name = "@timluq/spigot-spambot";
        this.version = "0.0.1";

        // initialize plugin variables
        this.variance = 32000; // 32s
        this.silence = 16000; // 16s
        this.target = "069a79f4-44e9-4726-a5be-fca90e38aaf5";

        // bind to `this` to be able to use `this` in method callbacks
        this.spam = this.spam.bind(this);
        this.spamtarget = this.spamtarget.bind(this);
    }
    start() {
        this.registerCommand("spamtarget", this.spamtarget, {
            description: "Changes which player to spam",
            permission: "spam.target",
            usage: "<command> <UUID>"
        });
        this.timeout = setTimeout(this.spam, this.silence);
    }
    stop() {
        clearTimeout(this.timeout);
    }
    spam() {
        Player.get(this.target).sendMessage("This is spam!!!1!");
        const wait = this.silence + Math.random() * this.variance;
        this.timeout = setTimeout(this.spam, wait);
    }
    spamtarget(sender, alias, arg1) {
        if (sender.player) {
            if (!arg1 || arg1 === "self" || arg1 === "me") {
                arg1 = sender.player;
            }
            Player.get(sender.player).sendMessage("Changed spamtarget to: " + arg1);
        }
        this.target = arg1;
    }
}
```

### Plugin package

Which module is loaded is decided by the `spigotmc` field, or `main` if that field is missing.
This gives you the possibility to expose parts of your plugin as a library for other plugins to use.
The module referenced must be the one to export a subclass of `Plugin` from `spigot-bridge` as `default`.

`spigot-bridge` must be part of the devDependencies.

```json
{
    "name": "@timluq/my-spigot-plugin",
    "version": "1.0.0",
    "spigotmc": "dist/plugin.js",
    "types": "typings/index.d.ts",
    "main": "dist/library.js",
    "dependencies": {
        "@timluq/some-lib": "^1.2.3"
    },
    "devDependencies": {
        "@types/node": "^10.0.0",
        "spigot-bridge": "^0.1.0"
    }
}
```

### Plugin distribution

Recommended practice is publishing your plugin to [`npm-registry`](https://www.npmjs.com). This will allow standard package managers to be used.

You may additionally publish your work in any way you would like to give people the oppurtunity to use your plugin.
