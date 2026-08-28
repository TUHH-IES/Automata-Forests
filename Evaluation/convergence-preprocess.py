import numpy as np
import pandas as pd

# 1. Load dataset & establish indices
df = pd.read_csv("convergence-15_all.csv")
df["line_idx"] = df.index % 102

# Separate instances
single_automata = df[df["line_idx"] < 100].copy()
single_rpni = df[df["line_idx"] == 100].copy()
forestPM = df[df["line_idx"] == 101].copy()
forestMV = df[df["line_idx"] == 101].copy()

# Set performance metrics
single_automata["perf"] = single_automata["RPNI"]
single_rpni["perf"] = single_rpni["RPNI"]
forestPM["perf"] = forestPM["FPM"]
forestMV["perf"] = forestMV["FMV"]

# Calculate 2D Euclidean Distance to Target (States=15, Perf=1000)
for d in [single_automata, single_rpni, forestPM]:
    d["dist_to_target"] = np.sqrt(
        (d["ModelSizeRPNI"] - 15) ** 2 + (d["perf"] - 1000) ** 2
    )

# --- Compute Aggregations & Selections ---

# Best single automaton per trainSize by min distance
best_single_perf = (
    single_automata.loc[
        single_automata.groupby("trainSize")["dist_to_target"].idxmin()
    ]
    .sort_values("trainSize")
    .reset_index(drop=True)
)

# Mean performance over trainSize (Panel B)
rpni_perf = single_rpni.groupby("trainSize")["perf"].mean().reset_index()
forestPM_perf = forestPM.groupby("trainSize")["perf"].mean().reset_index()
forestMV_perf = forestMV.groupby("trainSize")["perf"].mean().reset_index()

# Subsampled datasets (every 10th row for cleaner scatter/pairs)
single_rpni_sub = single_rpni.iloc[::10].copy()
forestPM_sub = forestPM.iloc[::10].copy()

# Paired connectors dataset: Merges corresponding RPNI and Forest entries for dashed lines
paired_connectors = pd.merge(
    single_rpni_sub[["trainSize", "ModelSizeRPNI", "RPNI"]],
    forestPM_sub[["trainSize", "ModelSizeRPNI", "FPM"]],
    on="trainSize",
    suffixes=("_rpni", "_forest"),
)

# --- Export CSV files for TikZ / PGFPlots ---

single_automata[
    ["ModelSizeRPNI", "RPNI", "trainSize"]
].to_csv("pgf_single_automata_pool.csv", index=False)
best_single_perf[
    ["trainSize", "ModelSizeRPNI", "RPNI", "perf"]
].to_csv("pgf_best_single.csv", index=False)
single_rpni_sub[
    ["trainSize", "ModelSizeRPNI", "RPNI"]
].to_csv("pgf_rpni_subsampled.csv", index=False)
forestPM_sub[
    ["trainSize", "ModelSizeRPNI", "FPM"]
].to_csv("pgf_forest_subsampled.csv", index=False)

paired_connectors.to_csv("pgf_paired_connectors.csv", index=False)

rpni_perf.to_csv("pgf_rpni_perf.csv", index=False)
forestPM_perf.to_csv("pgf_forest_pm_perf.csv", index=False)
forestMV_perf.to_csv("pgf_forest_mv_perf.csv", index=False)

print("CSV files successfully generated for PGFPlots.")