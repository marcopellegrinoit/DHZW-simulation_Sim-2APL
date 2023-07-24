library(ggplot2)
library(this.path)
library(tidyr)
library(dplyr)

# read proportions from the simulations
setwd(this.dir())
df_simulations <- read.csv('output_proportions.csv')

# read real ODiN proportions
setwd(this.dir())
setwd('../../../../../DHZW_assign_location/data/processed/analysis/ODiN')
df_real <- read.csv('mode_choice_overall.csv')

# format ODiN proportions to match the other dataframes
df_real$Simulation <- 'ODiN'
df_real <- df_real %>%
  rename(MeanOfTransport = mode_choice,
         Proportion = proportion)

# calculate average proportion for the simulations
df_average <- colMeans(df_simulations)
df_average <- data.frame(MeanOfTransport = names(df_average), Proportion = df_average, Simulation = "Average")

# format simulations
df_long <- df_simulations %>%
  mutate(Simulation = rownames(.)) %>%
  pivot_longer(-Simulation, names_to = "MeanOfTransport", values_to = "Proportion")

# Combine the original, average and ODiN dataframes
df_combined <- rbind(df_long, df_average, df_real)

# set colours to red and blue to highlight the average and ODiN
simulation_colors <- c(rep("gray", nrow(df_simulations)), "red", "blue")

# Bar plot
library(ggplot2)
ggplot(df_combined, aes(x = MeanOfTransport, y = Proportion, fill = Simulation)) +
  geom_bar(stat = "identity", position = "dodge", color='black') +
  scale_fill_manual(values = simulation_colors) +
  labs(x = "Mean of Transport", y = "Proportion", fill = "Simulation") +
  ggtitle("Proportions of Mode Choice in Simulations with the Uncalibrated Parameter Set")