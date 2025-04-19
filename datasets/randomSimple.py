import random
import os

output_folder = "randomSimple"
os.makedirs(output_folder, exist_ok=True)  # Membuat folder randomSimple jika belum ada

# Menghasilkan list bilangan bulat dengan nilai acak dari rentang 10000 hingga 40000
random_values = [random.randint(10000, 40000) for _ in range(10000)]

def generate_txt(rows):
    filename = os.path.join(output_folder, f"randomSimple_{rows}.txt")
    with open(filename, mode="w") as file:
        for value in random_values[:rows]:
            file.write(f"{value}\n")
    print(f"Generated: {filename}")

# Menghasilkan variasi file .txt dari 10,000 baris hingga 1,000 baris dengan kelipatan 1,000
for r in range(10000, 0, -1000):
    generate_txt(r)