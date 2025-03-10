import random
import os

output_folder = "randomSimple"
os.makedirs(output_folder, exist_ok=True)  # Create folder if it doesn't exist

# Generate a consistent list of random values
random_values = [random.randint(10000, 40000) for _ in range(10000)]

def generate_txt(rows):
    filename = os.path.join(output_folder, f"randomSimple_{rows}.txt")
    with open(filename, mode="w") as file:
        for value in random_values[:rows]:
            file.write(f"{value}\n")
    print(f"Generated: {filename}")

# Generate TXT files for 10,000 down to 1,000 rows
for r in range(10000, 0, -1000):
    generate_txt(r)
