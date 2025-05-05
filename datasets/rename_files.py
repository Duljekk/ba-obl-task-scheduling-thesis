import os
from pathlib import Path

def rename_files():
    base_dir = Path("datasets")
    
    if not base_dir.exists():
        print(f"Directory {base_dir} does not exist.")
        return
    
    # Find all RandStratified files recursively
    for file in base_dir.rglob("RandStratified*.txt"):
        try:
            # Extract the number from the filename
            number = file.stem.replace("RandStratified", "")
            new_name = file.parent / f"randomStratified_{number}.txt"
            
            # Rename the file
            file.rename(new_name)
            print(f"Renamed {file.name} to {new_name.name}")
        except Exception as e:
            print(f"Error renaming {file.name}: {e}")

if __name__ == "__main__":
    rename_files() 