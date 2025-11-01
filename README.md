# Farewell

An addon PaperMC plugin for BromineMC designed to sunset the server.

## FAQ

**Q**: Why separate plugin?  
**A**: I deleted the original source code and don't plan to maintain it.

**Q**: Why 1.20.2?  
**A**: It's the version currently targeted by the server.

**Q**: Why "Farewell"?  
**A**: Because we are sunsetting.

## License

This project is provided under the GNU General Public License version 3. (or later)
Check out [LICENSE](https://github.com/BromineMC/Farewell/blob/main/LICENSE)
for more information.

## Development

### Building (Compiling)

1. Have 1 GB of free RAM, 10 GB of free disk space,
   and an active internet connection.
2. Install Java 21 and dump it into PATH and/or JAVA_HOME.
3. Run `./gradlew assemble` from the terminal/PowerShell.
4. Grab the JAR from the `./build/libs` folder.

### Developing/Debugging

Run the `./gradlew runServer` command to launch the server. You can attach a
debugger to that process. Hotswap is supported. "Enhanced" hotswap
(class redefinition) and hotswap agent will work if supported by your JVM.

Join the server at `127.0.0.1:30006`.

The development environment has stricter preconditions: Netty detector,
Java assertions, etc. Code with bugs might (and probably will) fail faster here
than in a production environment.

The recommended IDE for development is IntelliJ IDEA (Community or Ultimate)
with the Minecraft Development plugin. This is not a strict requirement,
however. Any IDE/editor should work just fine.
