# Java Testenvironment

The given files provide the source code for the executable .jar file in the parent folder.
The project encompasses testing of:

1. repeated inferences of randomly generated Mealy machines,
2. repeated inferences of DFAs via fixed regular expressions,
3. evaluation of parameter m for Mealy machines,
4. evaluation of parameter alpha for Mealy machines,
5. evaluation of parameter m for DFAs,
6. evaluation of parameter alpha for DFAs,
7. evaluation of noise within the training data for DFA,
8. evaluation of noise within the training data for Mealy machines,
10. alpha analysis for DFA with incrementing training data
11. structural analysis 

## Noise Tests

The noise for the evaluation in option 7. and 8. is calculated by:

- DFA: swapping pos. and neg. samples, with the amount of swaps being a percentage of the overall training data,
- Mealy machines: changing random output characters, s.t., the resulting training data can still return a deterministic automaton.

## Automata Forests

The files contain two different implementations for automata forests:

1. ForestCV and ForestMV classes,
2. AutomataForestDFA and AutomataForestMealyMachine classes.

ForestCV and ForestMV are used in the paper "Using Forest Structures for Passive Automata Learning".
AutomataForestDFA and AutomataForestMealyMachine are used in "Automata Forests: a Framework to Utilize Subsamples in Grammatical Inference for DFA".

AutomataForest classes encompass both evaluation methods of the fixed automata class (CV and MV).
ForestCV/MV encompass both automata classes with a fixed evaluation method.

Between the two, we recommend to use AutomataForest classes, if using the provided implementation.
This is because, ForestCV/MV are deprecated.

Additional files needed to apply the provided AutomataForest classes separately are:

1. Helperfunctions.java as a static class providing shared methods across the project,
2. HelperForestThreadsDFA.java providing threading for inference of the m automata.

Threading is only implemented for AutomataForestDFA 

