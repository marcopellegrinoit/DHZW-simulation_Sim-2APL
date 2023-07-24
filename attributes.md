![GitHub](https://img.shields.io/badge/license-GPL--3.0-blue)

## Agent-Based Simulation of Human Mobility Behaviour

**Utrecht University, The Netherlands. 2022 - 2023**
  
  Author: Marco Pellegrino

Contributors: Jan de Mooij, Tabea Sonnenschein, Mehdi Dastani, Dick Ettema, Brian Logan, Judith A. Verstegen


### Agents and Households attributes

Agents are composed of the following attributes:

*   **agent ID**
*   **household ID**
*   **age**: integer age
*   gender: male, female
*   migration background: Dutch, non-Western, Western
*   is child: true/false
*   current education level: low, middle, high
*   education attainment: low, middle, high
*   **car license**: true/false
*   moped license: true/false

Households are composed of the following attributes:

*   **household ID**
*   size: integer value
*   postcode PC4
*   postcode PC6
*   neighbourhood code
*   household type: single, single-parent, couple with children, couple without children
*   standardised income group
*   **car ownership**

Attributes in bold are currently used in the simulation. The remaining attributes could be used for further choice modelling and (spatial-)statistical evaluation. For example, one could analyze the mode choice distribution of residents in a certain area, or of a specific household type.

### Implemented Mode Choices

The following mode choices are implemented:

*   car driver
*   car passenger
*   bus and tram
*   train
*   bicycle
*   walk

### Output Distributions

The following output distributions are implemented:

*   mode choice distribution
*   total km distance per mode
*   total km distance per activity
*   activity type distribution
*   mode choice per day distribution
*   mode choice per activity distribution
*   mode choice per car license ownership distribution
*   mode choice per household car ownership distribution

### Travel information as belief

In the simulation, travel information such as travel times, distances, and the number of bus changes is provided as input via a [file](src/main/resources/routing). This information is then distributed to agents by selecting only the relevant data for the locations visited by each agent.

Alternatively, a non-distributed approach is possible by adopting a shared database of routing information that agents can fetch for each trip. However, in this work, a distributed approach was chosen to enable future manipulations of agents' beliefs about travel times based on demographic information, for example. Although a non-distributed approach would reduce memory usage by storing the information only once without duplicates, the distributed approach provides flexibility for future extensions and modifications.

For walk, bike, and car modes, the routing information is symmetric, meaning that travel times and distances are equal in both directions. To avoid redundancy, the data is stored only once with a unique identifier that is computed by sorting the departure and arrival locations in alphabetical order. Consequently, the information from location A to location B is stored in the same manner as from location B to location A.