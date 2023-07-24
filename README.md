![GitHub](https://img.shields.io/badge/license-GPL--3.0-blue)

# Agent-Based Simulation of Human Mobility Behaviour

**Utrecht University, The Netherlands. 2022 - 2023**

Author: Marco Pellegrino

Contributors: Jan de Mooij, Tabea Sonnenschein, Mehdi Dastani, Dick Ettema, Brian Logan, Judith A. Verstegen

## Description

A large-scale data-driven agent-based model designed to simulate individual agents' mode choice decisions for specific activity schedules within Den Haag's Zuid-West district in The Netherlands. The model integrates real-world data to precisely capture transportation preferences, providing a valuable tool for enhancing sustainable urban planning and informing transportation policy.

## Invoking program

Call --help when running the application for command line arguments.

The JAR file is automatically generated and placed in the `target` directory.  
In order to use the JAR file, make sure to use the Java version also used by Maven, and call

```plaintext
$ java -jar sim2apl-dhzw-simulation-1.0-SNAPSHOT-jar-with-dependencies.jar [args]
```

The file [`args_example.txt`](src/main/resources/args_example.txt) contains the arguments to launch the simulation.

## Build instructions

This section describes how to set up the code in your own development environment.

### Prerequisites

This manual assumes Maven is installed for easy package management

Prerequisites:

*   Java 11+ (not tested with lower versions)
*   [Sim-2APL](https://github.com/A-Practical-Agent-Programming-Language/Sim-2APL)
*   (Maven)

### Sim-2APL

Download [Sim-2APL](https://github.com/A-Practical-Agent-Programming-Language/Sim-2APL) from Github,  
and, to ensure compatibility, checkout the `v2.0.0` version tag.

```plaintext
$ git clone https://github.com/A-Practical-Agent-Programming-Language/Sim-2APL.git sim-2apl
$ cd sim-2apl
$ git checkout v2.0.0
```

Install the package using Maven:

```plaintext
$ mvn -U clean install
```

This will automatically add the library to your local Maven repository, so no further action is required here.

### This library

Clone the master branch of this library and install with Maven, or open in an IDE with Maven support (e.g. VSCode, Idea Intellij, Eclipse or NetBeans) and let the IDE set up the project.

```plaintext
$ git clone https://github.com/marcopellegrinoit/DHZW-simulation_Sim-2APL.git
$ cd DHZW-simulation_Sim-2APL
$ mvn -U clean install
```

The application requires various arguments, either when invoked from the command line or when used in an IDE.  
Invoke the program with the argument `--help`

In the [resource](src/main/resources/) directory, an example [configuration](src/main/resources/config_full.toml) TOML file is given.

## Detailled explanation
-   [See this page](attributes.md) for the agents attributes and the information needed for the simulation
-   [See this page](model_process.md) for a detailled explanation of the simulation.


## License

This library contains free software; The code can be freely used under the Mozilla Public License 2.0. See the [license](LICENSE) file for details.  
This code comes with ABSOLUTELY NO WARRANTY, to the extent permitted by applicable law.