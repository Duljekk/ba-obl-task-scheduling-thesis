import os
from pathlib import Path
import matplotlib.pyplot as plt
import numpy as np

# Class distribution data
kelas = [
    ("class 1", 862), ("class 2", 31), ("class 3", 14), ("class 4", 6), ("class 5", 10),
    ("class 6", 8), ("class 7", 4), ("class 8", 1), ("class 9", 1), ("class 10", 1),
    ("class 11", 2), ("class 12", 10), ("class 13", 0), ("class 14", 1), ("class 15", 1),
    ("class 16", 2), ("class 17", 1), ("class 18", 1), ("class 19", 1), ("class 20", 3),
    ("class 21", 1), ("class 22", 1), ("class 23", 2), ("class 24", 1), ("class 25", 1),
    ("class 26", 0), ("class 27", 0), ("class 28", 0), ("class 29", 1), ("class 30", 3),
    ("class 31", 1), ("class 32", 2), ("class 33", 2), ("class 34", 3), ("class 35", 2),
    ("class 36", 0), ("class 37", 1), ("class 38", 1), ("class 39", 1), ("class 40", 1),
    ("class 41", 0), ("class 42", 1), ("class 43", 0), ("class 44", 0), ("class 45", 1),
    ("class 46", 0), ("class 47", 2), ("class 48", 0), ("class 49", 0), ("class 50", 0),
    ("class 51", 0), ("class 52", 0), ("class 53", 0), ("class 54", 0), ("class 55", 0),
    ("class 56", 1), ("class 57", 1), ("class 58", 0), ("class 59", 0), ("class 60", 0),
    ("class 61", 1), ("class 62", 0), ("class 63", 0), ("class 64", 1), ("class 65", 0),
    ("class 66", 0), ("class 67", 1), ("class 68", 1), ("class 69", 1), ("class 70", 0),
    ("class 71", 0), ("class 72", 0), ("class 73", 0), ("class 74", 0), ("class 75", 1),
    ("class 76", 0), ("class 77", 0), ("class 78", 0), ("class 79", 1), ("class 80", 0),
    ("class 81", 0), ("class 82", 0), ("class 83", 0), ("class 84", 0), ("class 85", 0),
    ("class 86", 0), ("class 87", 0), ("class 88", 0), ("class 89", 0), ("class 90", 0),
    ("class 91", 0), ("class 92", 0), ("class 93", 0), ("class 94", 1), ("class 95", 1),
    ("class 96", 0), ("class 97", 0), ("class 98", 0), ("class 99", 0), ("class 100", 1),
]

def analyze_dataset(dataset_path, multiplier):
    """Analyze the distribution of values in a dataset."""
    # Read the data
    with open(dataset_path, 'r') as f:
        data = [int(line.strip()) for line in f.readlines()]
    
    # Calculate the expected number of values for each class
    expected_counts = {i: int(multiplier * count) for i, (_, count) in enumerate(kelas, 1)}
    
    # Calculate the range for each class
    ranges = []
    start = 0
    end = 88000
    for _ in range(100):
        ranges.append((start, end))
        start = end
        end += 88000
    
    # Count the actual number of values in each range
    actual_counts = {i: 0 for i in range(1, 101)}
    for value in data:
        for i, (range_start, range_end) in enumerate(ranges, 1):
            if range_start <= value < range_end:
                actual_counts[i] += 1
                break
    
    # Print the results
    print(f"\nDataset: {dataset_path.name}")
    print(f"Total values: {len(data)}")
    print(f"Expected total: {sum(expected_counts.values())}")
    
    # Compare expected vs actual counts
    print("\nClass Distribution Analysis:")
    print("Class | Expected | Actual | Match?")
    print("-" * 40)
    
    matches = 0
    for i in range(1, 101):
        expected = expected_counts[i]
        actual = actual_counts[i]
        match = expected == actual
        if match:
            matches += 1
        print(f"{i:5d} | {expected:8d} | {actual:6d} | {'Yes' if match else 'No'}")
    
    print(f"\nMatch rate: {matches}/100 classes ({matches/100*100:.2f}%)")
    
    # Create a visualization
    plt.figure(figsize=(15, 8))
    
    # Plot expected vs actual counts
    x = np.arange(1, 101)
    width = 0.35
    
    plt.bar(x - width/2, [expected_counts[i] for i in x], width, label='Expected')
    plt.bar(x + width/2, [actual_counts[i] for i in x], width, label='Actual')
    
    plt.xlabel('Class')
    plt.ylabel('Count')
    plt.title(f'Class Distribution in {dataset_path.name}')
    plt.legend()
    plt.grid(True, alpha=0.3)
    
    # Save the plot
    plot_path = dataset_path.parent / f"{dataset_path.stem}_distribution.png"
    plt.savefig(plot_path)
    print(f"Distribution plot saved to {plot_path}")
    
    return matches == 100

def analyze_random_stratified(base_dir):
    """Analyze all randomStratified files in the directory."""
    for multiplier in range(1, 11):
        dataset_dir = base_dir / f"dataset{multiplier*1000}"
        if not dataset_dir.exists():
            print(f"Dataset directory {dataset_dir} does not exist.")
            continue
            
        txt_file = dataset_dir / f"RandStratified{multiplier*1000}.txt"
        if not txt_file.exists():
            print(f"File {txt_file} does not exist.")
            continue
            
        print(f"\nAnalyzing {txt_file.name}...")
        analyze_dataset(txt_file, multiplier)

def main():
    """Analyze all datasets."""
    base_dir = Path("datasets/randomStratified/generateRandomStrat")
    
    if not base_dir.exists():
        print(f"Directory {base_dir} does not exist.")
        return
    
    print("Analyzing randomStratified files...")
    analyze_random_stratified(base_dir)

if __name__ == "__main__":
    main() 