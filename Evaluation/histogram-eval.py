import matplotlib.pyplot as plt
import numpy as np
import pandas as pd

data = pd.read_csv("DFA random 20.csv")
if len(data) % 2:
    raise ValueError("Expected each experiment to contain a baseline row and a forest row.")

baseline_rows = data.iloc[::2]
forest_rows = data.iloc[1::2]

methods = {
    "RPNI": (baseline_rows["RPNI"], baseline_rows["TimeRPNI"]),
    "EDSM": (baseline_rows["EDSM"], baseline_rows["TimeEDSM"]),
    "GRBM": (baseline_rows["GRBM"], baseline_rows["TimeGRBM"]),
    "FPM": (forest_rows["FPM"], forest_rows["TimeForest"]),
    "FMV": (forest_rows["FMV"], forest_rows["TimeForest"]),
}

mean_df = pd.DataFrame(
    [
        {
            "Method": method,
            "Mean_Performance": performance.mean(),
            "Mean_Time": runtime.mean(),
        }
        for method, (performance, runtime) in methods.items()
    ]
)
mean_df.to_csv("mean_results-dfa-20.csv", index=False)
print(mean_df.to_string(index=False))

performance_bins = np.arange(0, 1001, 10)
for metric, x_label in (
    ("Performance", "Correctly translated words"),
    ("Time", "Time (seconds)"),
):
    histogram_rows = []

    for method, (performance, runtime) in methods.items():
        values = (performance if metric == "Performance" else runtime).dropna().to_numpy()
        if values.size == 0:
            raise ValueError(f"No {metric.lower()} values found for {method}.")

        bins = (
            performance_bins
            if metric == "Performance"
            else np.histogram_bin_edges(values, bins="auto")
        )
        counts, bin_edges = np.histogram(values, bins=bins)
        histogram_rows.extend(
            {
                "Method": method,
                "Bin_Start": bin_edges[index],
                "Bin_End": bin_edges[index + 1],
                "Frequency": counts[index],
            }
            for index in range(len(counts))
        )

        plt.figure()
        plt.hist(values, bins=bins, alpha=0.7, color="blue", edgecolor="black")
        plt.title(f"{method} {metric.lower()} histogram")
        plt.xlabel(x_label)
        plt.ylabel("Frequency")
        plt.grid(axis="y", alpha=0.75)
        plt.tight_layout()
        plt.savefig(f"{method}_{metric.lower()}_histogram.png")
        plt.close()

    pd.DataFrame(histogram_rows).to_csv(
        f"{metric.lower()}_histogram_data-dfa-20.csv", index=False
    )
