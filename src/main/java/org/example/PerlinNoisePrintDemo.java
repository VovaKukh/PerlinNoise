package org.example;

public class PerlinNoisePrintDemo {
    public static void main(String[] args) {
        int width = 100;
        int height = 100;
        double[][] noiseGrid = new double[width][height];

        double scale = 0.1;

        // Populate the noise grid with Perlin noise values
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double noiseValue = PerlinNoise.noise(x * scale, y * scale, 0); // Z is kept as 0 for 2D noise
                noiseGrid[x][y] = noiseValue;
            }
        }

        // Print the first 10 values from the noise grid
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                System.out.printf("%.4f ", noiseGrid[i][j]);
                if (j == 9) { // New line after every 10th value for readability
                    System.out.println();
                }
            }
        }
    }
}

