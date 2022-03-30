# CommandParser

This is a Kotlin/Multiplatform command parser inspired by brigadier and kordex using kotlinx.coroutines.    
Its intended use is for CLI applications (or similar environments like chat bots).

## Features

- Easy to extend
- Simple to use and understand
- Supports kotlinx.coroutines
- Contextual argument parsing
- DSL builders for command trees

## Usage

### Gradle

```kotlin
repositories {
    maven("https://maven.martmists.com/releases")
    maven("https://maven.martmists.com/snapshots")  // For getting builds of a specific commit 
}

dependencies {
    implementation("com.martmists:command_parser:1.2.4")
}
```

### Example

```kotlin
// Create a class to hold your context, you can do whatever with it.
class MyContext(input: String) : Context(input) {  // Somehow pass input to the constructor
    // You can add your own fields and methods here.
}

suspend fun main() {
    // This object will hold the commands
    val dispatcher = Dispatcher<MyContext>()

    // Start adding some commands
    build(dispatcher) {
        // Add a command
        command("hello") {
            // Give it an action
            action {
                // Here, `this` is the context object
                println("Hello world!")
            }
        }
    }

    dispatcher.dispatch(MyContext("hello"))        // Prints "Hello world!" and returns true
    dispatcher.dispatch(MyContext("hello world"))  // Doesn't match and returns false

    // Calling build again does NOT remove the previously registered commands!
    build(dispatcher) {
        // Add a command with arguments
        command("add") {
            argument("num1", IntegerArgumentType.int()) { num1 ->
                argument("num2", IntegerArgumentType.int()) { num2 ->
                    action {
                        // No need to worry about calling these functions multiple times, the context caches them.
                        println("${num1()} + ${num2()} = ${num1() + num2()}")
                    }
                }
            }

            // It's possible to create a tree of commands as well
            // These are traversed in the same order as they are added
            // The first command that matches is executed and the rest are ignored
            argument("float1", FloatArgumentType.float()) { float1 ->
                argument("float2", FloatArgumentType.float()) { float2 ->
                    action {
                        println("${float1()} + ${float2()} = ${float1() + float2()}")
                    }
                }
            }
        }
    }

    dispatcher.dispatch(MyContext("add 1 2"))      // Prints "1 + 2 = 3"
    dispatcher.dispatch(MyContext("add 1.0 2.0"))  // Prints "1.0 + 2.0 = 3.0"
    
    // Arguments can be marked as optional
    build(dispatcher) {
        command("ls") {
            // You can specify a default value for an argument
            argument("path", StringArgumentType.greedy(), default=".") { path ->
                action {
                    println("Listing directory: ${path()}")
                }
            }
        }
        
        command("find") {
            // Or mark it as optional, which will make it nullable
            argument("path", StringArgumentType.string(), optional = true) { path ->
                action {
                    if (path() != null) {
                        println("Finding in directory: ${path()}")
                    } else {
                        println("Finding in current directory")
                    }
                }
            }
        }
    }
    
    dispatcher.dispatch(MyContext("ls"))            // Prints "Listing directory: ."
    dispatcher.dispatch(MyContext("ls /tmp"))       // Prints "Listing directory: /tmp"
    dispatcher.dispatch(MyContext("find"))          // Prints "Finding in current directory"
    dispatcher.dispatch(MyContext("find /tmp"))     // Prints "Finding in directory: /tmp"

    // You can place checks in the command tree
    build(dispatcher) {
        command("divide") {
            argument("num1", IntegerArgumentType.int()) { num1 ->
                argument("num2", IntegerArgumentType.int()) { num2 ->
                    check {
                        // If this check fails, the command will not be executed, 
                        // nor will any of its children be traversed
                        num2() != 0
                    }

                    action {
                        println("${num1()} / ${num2()} = ${num1().toFloat() / num2()}")
                    }

                    // Add a subcommand to this command
                    argument("num3", IntegerArgumentType.int()) { num3 ->
                        check {
                            num3() > 0
                        }

                        action {
                            // num2 != 0 and num3 > 0
                            println("${num1()} / (${num2()}^${num3()}) = ${num1().toFloat() / num2().pow(num3())}")
                        }
                    }
                }
            }
        }
    }
    
    dispatcher.dispatch(MyContext("divide 1 0"))    // Doesn't match and returns false
    dispatcher.dispatch(MyContext("divide 1 2"))    // Prints "1 / 2 = 0.5"
    dispatcher.dispatch(MyContext("divide 1 2 3"))  // Prints "1 / (2^3) = 0.125"
    dispatcher.dispatch(MyContext("divide 1 2 0"))  // Doesn't match and returns false
    dispatcher.dispatch(MyContext("divide 1 0 3"))  // Doesn't match and returns false

    // You can also use literals
    build(dispatcher) {
        command("git") {
            literal("add") {
                action {
                    println("Adding files...")
                }
            }

            literal("commit") {
                action {
                    println("Committing...")
                }
            }

            literal("push") {
                action {
                    println("Pushing...")
                }
            }
        }
    }

    dispatcher.dispatch(MyContext("git add"))     // Prints "Adding files..."
    dispatcher.dispatch(MyContext("git commit"))  // Prints "Committing..."
    dispatcher.dispatch(MyContext("git push"))    // Prints "Pushing..."

    // Commands and Literals support aliases
    build(dispatcher) {
        command("info", "i") {
            literal("register", "reg", "r") {
                action {
                    println("Dumping register info...")
                }
            }
        }
    }

    // These all print "Dumping register info..."
    dispatcher.dispatch(MyContext("info register"))
    dispatcher.dispatch(MyContext("info r"))
    dispatcher.dispatch(MyContext("i reg"))
}

// Custom argument types are also easy to add:

data class Coordinate(val x: Int, val y: Int)

class CoordinateArgumentType<C : Context>() : ArgumentType<C, Coordinate>() {
    private val regex = """\((\d+),\s*(\d+)\)""".toRegex()

    // Return the part that matches, this MUST be at the start of `input`
    override suspend fun parse(context: C, input: String): String? {
        val match = regex.matchAt(input, 0) ?: return null
        return match.value
    }

    // Convert the matching string into the Coordinate object
    override suspend fun value(context: C, value: String): Coordinate {
        val groups = regex.matchEntire(value)!!.groupValues
        return Coordinate(groups[1].toInt(), groups[2].toInt())
    }
}

suspend fun main() {
    // ...
    build(dispatcher) {
        command("move") {
            argument("coord", CoordinateArgumentType()) { coord ->
                action {
                    println("Moving to ${coord().x}, ${coord().y}")
                }
            }
        }
    }

    dispatcher.dispatch(MyContext("move (1, 2)"))      // Prints "Moving to 1, 2"
    dispatcher.dispatch(MyContext("move (1.0, 2.0)"))  // Doesn't match and returns false
}

// Permission nodes are also supported

class MyPermissionContext(input: String, vararg permissions: String) : ContextWithPermissions(input) {
    private val perms = PermissionNode.fromList(*permissions)
    override fun getPermissions(): List<PermissionNode> {
        return perms
    }
}

suspend fun main() {
    private val permissionDispatcher = Dispatcher<MyPermissionContext>()
    
    // ...
    build(permissionDispatcher) {
        command("admin") {
            literal("add") {
                check {
                    hasPermission("admin.add")
                }

                action {
                    println("Adding admin.")
                }
            }

            literal("remove") {
                check {
                    hasPermission("admin.remove")
                }

                action {
                    println("Removing admin.")
                }
            }
        }
    }

    permissionDispatcher.dispatch(MyPermissionContext("admin add", "admin.add"))     // Prints "Adding admin."
    permissionDispatcher.dispatch(MyPermissionContext("admin remove", "admin.add"))  // Doesn't match and returns false
    permissionDispatcher.dispatch(MyPermissionContext("admin add", "admin.*"))       // Prints "Adding admin." (wildcard)
    permissionDispatcher.dispatch(MyPermissionContext("admin remove", "admin.*"))    // Prints "Removing admin." (wildcard)
}
```

## License

This project is licensed under the BSD-4-Clause license.
