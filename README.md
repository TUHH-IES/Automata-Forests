# Automata Forests - a Framework to Utilize Subsamples in Grammatical Inference for DFA

This repository contains the necessary java files, R scripts, and datasets to reproduce the content of the papers "Automata Forests - a Framework to Utilize Subsamples in Grammatical Inference for DFA"
and "Using Forest Structures for Passive Automata Learning".

## Getting Started

The repository is structured into:

1. Java test environment used to generate the datasets and implementing automata forests
2. Datasets and R scripts related to the paper "Automata Forests - a Framework to Utilize Subsamples in Grammatical Inference for DFA"
3. Datasets and R scripts used in the paper "Using Forest Structures for Passive Automata Learning"
4. ForestDataSets.jar executable - to reproduce presented data and conduct own analysis comparing RPNI and automata forests if wished

For completeness: figures and datasets used in both publications can be found within both repositories.

Within each directory, there exists a separate README.md file with further guidance, specific for the given directory.

## Inconsistency to Paper: Using Forest Structures for Passive Automata Learning

Due to an error on our side, the originally provided results in the paper "Using Forest Structures for Passive Automata Learning" are not as originally provided.
The error is related to Figure 1b and 2b.
We recreated the data according to the provided documentation and did not obtain the exact same values.
The data provided within this repository shifts the results of the y-axis in Figure 1b and 2b by a factor of 2.

Therefore, the provided results are not equivalent to the original results.
Nonetheless, the analysis with respect to the hyperparameters and correlations we named do not change.


## Contact

For further questions ask the corresponding author at arne.krumnow@tuhh.de.