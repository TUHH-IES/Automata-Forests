import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import seaborn as sns

# 1. Load dataset & establish indices
df = pd.read_csv("dfa convergence-15_all.csv")
df["line_idx"] = df.index % 102

# Separate instances
single_automata = df[df["line_idx"] < 100].copy()
single_rpni = df[df["line_idx"] == 100].copy()
forestPM = df[df["line_idx"] == 101].copy()
forestMV = df[df["line_idx"] == 101].copy()

# Set correct performance metrics
single_automata["perf"] = single_automata["RPNI"]
single_rpni["perf"] = single_rpni["RPNI"]
forestPM["perf"] = forestPM["FPM"]
forestMV["perf"] = forestMV["FMV"]

# Calculate 2D Euclidean Distance to Target (States=15, Perf=1000)
for d in [single_automata, single_rpni, forestPM]:
    d["dist_to_target"] = np.sqrt(
        (d["ModelSizeRPNI"] - 15) ** 2 + (d["perf"] - 1000) ** 2
    )

# Compute performance and minimum distance metrics across training sizes
best_single_dist = (
    single_automata.groupby("trainSize")["dist_to_target"].min().reset_index()
)
# Identify best single automaton per trainSize by min distance to pull its performance
best_single_perf = (
    single_automata.loc[
        single_automata.groupby("trainSize")["dist_to_target"].idxmin()
    ]
    .sort_values("trainSize")
    .reset_index(drop=True)
)

rpni_perf = single_rpni.groupby("trainSize")["perf"].mean().reset_index()
forestPM_perf = forestPM.groupby("trainSize")["perf"].mean().reset_index()
forestMV_perf = forestMV.groupby("trainSize")["perf"].mean().reset_index()

# 2. Create 2-Panel Figure
fig, axes = plt.subplots(1, 2, figsize=(18, 7))

# --- Panel A: Search Space Coverage with trainSize Color Mapping ---
ax1 = axes[0]

# Scatter plot for single automata pool colored by trainSize
sc_sa = ax1.scatter(
    single_automata["ModelSizeRPNI"],
    single_automata["RPNI"],
    c=single_automata["trainSize"],
    cmap="viridis",
    alpha=0.4,
    s=30,
    label="Single Automata Pool",
    zorder=2,
)

# Colorbar for trainSize
cbar = plt.colorbar(sc_sa, ax=ax1)
cbar.set_label("Learning Set Size (trainSize)", rotation=270, labelpad=15)

# Target marker
ax1.scatter(
    15,
    1000,
    color="#9b59b6",
    marker="*",
    s=300,
    label="Target Automaton (15 states, 1000 words)",
    zorder=6,
    edgecolors="black",
)

# Plot Single RPNI Baseline & Forest Automaton with trainSize color fill
# to visually correspond across learning set sizes

ax1.scatter(
    best_single_perf["ModelSizeRPNI"],
    best_single_perf["RPNI"],
    c = best_single_perf["trainSize"],
    cmap="viridis",
    marker="D",
    s=100,
    label="Closest Instance in Search Space",
    zorder=5,
    edgecolors="black",
)

sc_rpni = ax1.scatter(
    single_rpni.iloc[::10]["ModelSizeRPNI"],
    single_rpni.iloc[::10]["RPNI"],
    c=single_rpni.iloc[::10]["trainSize"],
    cmap="viridis",
    marker="o",
    s=100,
    label="Single RPNI Baseline",
    zorder=4,
    edgecolors="black",
    linewidths=1.5,
)

sc_forest = ax1.scatter(
    forestPM.iloc[::10]["ModelSizePM"],
    forestPM.iloc[::10]["FPM"],
    c=forestPM.iloc[::10]["trainSize"],
    cmap="viridis",
    marker="^",
    s=110,
    label="Selected Forest Automaton",
    zorder=5,
    edgecolors="red",  # Distinct edge color to highlight forest entries
    linewidths=1.5,
)

# Annotate corresponding pairs with trainSize labels for easy identification
for idx, row in single_rpni.iloc[::10].iterrows():
    ts = row["trainSize"]
    f_row = forestPM[forestPM["trainSize"] == ts]
    if not f_row.empty:
        f_x, f_y = f_row["ModelSizePM"].values[0], f_row["FPM"].values[0]
        # Draw thin dotted line connecting matching RPNI and Forest entries
        ax1.plot(
            [row["ModelSizeRPNI"], f_x],
            [row["RPNI"], f_y],
            color="black",
            linestyle=":",
            alpha=0.9,
            zorder=3,
        )

ax1.set_title(
    "A. 2D Search Space Coverage & Target Proximity",
    fontsize=12,
    fontweight="bold",
    pad=10,
)
ax1.set_xlabel("Number of States (ModelSizeRPNI)")
ax1.set_ylabel("Performance (Correctly Translated Words)")
ax1.axhline(1000, color="gray", linestyle=":", alpha=0.5)
ax1.axvline(15, color="gray", linestyle=":", alpha=0.5)
ax1.legend(loc="lower right", frameon=True, fontsize=9)

# --- Panel B: Performance over Training Steps ---
ax2 = axes[1]
ax2.plot(
    best_single_perf["trainSize"],
    best_single_perf["perf"],
    label="Closest Single Automaton (Pool Minimum)",
    color="#f39c12",
    marker="D",
    lw=2.5,
)
ax2.plot(
    rpni_perf["trainSize"],
    rpni_perf["perf"],
    label="Single RPNI Baseline",
    color="#e74c3c",
    marker="o",
    lw=2,
)
ax2.plot(
    forestPM_perf["trainSize"],
    forestPM_perf["perf"],
    label="Selected Forest Automaton",
    color="#2ecc71",
    marker="^",
    lw=2,
)

ax2.plot(
    forestMV_perf["trainSize"],
    forestMV_perf["perf"],
    label="Forest with Majority Voting",
    color="#2e38cc",
    marker="^",
    lw=2,
)

ax2.set_title(
    "B. Performance vs. Train Size",
    fontsize=12,
    fontweight="bold",
    pad=10,
)
ax2.set_xlabel("Train Set Size (trainSize)")
ax2.set_ylabel("Performance (Correctly Translated Words)")
ax2.axhline(1000, color="gray", linestyle=":", alpha=0.5, label="Target Performance (1000)")
ax2.legend(loc="lower right", frameon=True, fontsize=10)

plt.tight_layout()
plt.show()