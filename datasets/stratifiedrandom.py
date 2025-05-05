import random
import os
import json
from pathlib import Path

# Class distribution data
kelas = [
    ("kelas 1", 862), ("kelas 2", 31), ("kelas 3", 14), ("kelas 4", 6), ("kelas 5", 10),
    ("kelas 6", 8), ("kelas 7", 4), ("kelas 8", 1), ("kelas 9", 1), ("kelas 10", 1),
    ("kelas 11", 2), ("kelas 12", 10), ("kelas 13", 0), ("kelas 14", 1), ("kelas 15", 1),
    ("kelas 16", 2), ("kelas 17", 1), ("kelas 18", 1), ("kelas 19", 1), ("kelas 20", 3),
    ("kelas 21", 1), ("kelas 22", 1), ("kelas 23", 2), ("kelas 24", 1), ("kelas 25", 1),
    ("kelas 26", 0), ("kelas 27", 0), ("kelas 28", 0), ("kelas 29", 1), ("kelas 30", 3),
    ("kelas 31", 1), ("kelas 32", 2), ("kelas 33", 2), ("kelas 34", 3), ("kelas 35", 2),
    ("kelas 36", 0), ("kelas 37", 1), ("kelas 38", 1), ("kelas 39", 1), ("kelas 40", 1),
    ("kelas 41", 0), ("kelas 42", 1), ("kelas 43", 0), ("kelas 44", 0), ("kelas 45", 1),
    ("kelas 46", 0), ("kelas 47", 2), ("kelas 48", 0), ("kelas 49", 0), ("kelas 50", 0),
    ("kelas 51", 0), ("kelas 52", 0), ("kelas 53", 0), ("kelas 54", 0), ("kelas 55", 0),
    ("kelas 56", 1), ("kelas 57", 1), ("kelas 58", 0), ("kelas 59", 0), ("kelas 60", 0),
    ("kelas 61", 1), ("kelas 62", 0), ("kelas 63", 0), ("kelas 64", 1), ("kelas 65", 0),
    ("kelas 66", 0), ("kelas 67", 1), ("kelas 68", 1), ("kelas 69", 1), ("kelas 70", 0),
    ("kelas 71", 0), ("kelas 72", 0), ("kelas 73", 0), ("kelas 74", 0), ("kelas 75", 1),
    ("kelas 76", 0), ("kelas 77", 0), ("kelas 78", 0), ("kelas 79", 1), ("kelas 80", 0),
    ("kelas 81", 0), ("kelas 82", 0), ("kelas 83", 0), ("kelas 84", 0), ("kelas 85", 0),
    ("kelas 86", 0), ("kelas 87", 0), ("kelas 88", 0), ("kelas 89", 0), ("kelas 90", 0),
    ("kelas 91", 0), ("kelas 92", 0), ("kelas 93", 0), ("kelas 94", 1), ("kelas 95", 1),
    ("kelas 96", 0), ("kelas 97", 0), ("kelas 98", 0), ("kelas 99", 0), ("kelas 100", 1),
]

def generate_random_data(start, end, count):
    """Generate random data points within the specified range."""
    return [random.randint(start, end) for _ in range(count)]

def process_dataset(multiplier, base_dir):
    """Process a single dataset with the given multiplier."""
    dataset_name = f"dataset{multiplier*1000}"
    dataset_dir = base_dir / dataset_name
    dataset_dir.mkdir(exist_ok=True)
    
    # Generate data for each class
    all_data = []
    json_files = []  # Keep track of JSON files to delete later
    start = 0
    end = 88000
    
    for nama_kelas, count in kelas:
        total = int(multiplier * count)
        if total > 0:  # Skip if no data points needed
            data = generate_random_data(start, end, total)
            result = json.dumps({"data": data})
            
            # Write JSON file
            json_path = dataset_dir / f"{nama_kelas.replace(' ', '_')}.json"
            with open(json_path, "w") as f:
                f.write(result)
            
            # Track JSON file for later deletion
            json_files.append(json_path)
            
            # Add to combined data
            all_data.extend(data)
            
            print(f"processing {nama_kelas}")
        
        start = end
        end += 88000
    
    # Sort the data in ascending order
    all_data.sort()
    
    # Write combined data to text file
    txt_path = dataset_dir / f"RandStratified{multiplier*1000}.txt"
    with open(txt_path, "w") as f:
        f.write("\n".join(map(str, all_data)))
    
    # Delete JSON files after combining
    for json_file in json_files:
        try:
            json_file.unlink()
            print(f"Deleted {json_file.name}")
        except Exception as e:
            print(f"Error deleting {json_file.name}: {e}")
    
    return dataset_name

def main():
    """Main function to generate all datasets."""
    base_dir = Path("datasets/randomStratified/generateRandomStrat")
    base_dir.mkdir(parents=True, exist_ok=True)
    
    # Process all datasets
    for multiplier in range(1, 11):
        dataset_name = process_dataset(multiplier, base_dir)
        print(f"Completed {dataset_name}")

if __name__ == "__main__":
    main()
