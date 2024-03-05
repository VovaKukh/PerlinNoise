package org.example;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class PerlinNoise3D extends Application {

    private final double noiseScale = 0.06;

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, 1080, 720, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.LIGHTGREY);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-1000);
        camera.setRotate(180);
        camera.setNearClip(0.1);
        camera.setFarClip(3000.0);
        scene.setCamera(camera);

        // Create the point light
        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(0);
        pointLight.setTranslateY(100);
        pointLight.setTranslateZ(-400);

        // Visualize the point light with a small sphere
        // Using an image to simulate the point light
        Image glowImage = new Image("c511988613fa2ad1ef61695155cbd687_2.png");
        Sphere lightSphere = new Sphere(8);
        lightSphere.setTranslateX(0);       // right - left - 0 middle
        lightSphere.setTranslateY(100);       // down - up - 0 middle
        lightSphere.setTranslateZ(-400);    // close - far
        PhongMaterial lightMaterial = new PhongMaterial(Color.YELLOW);
        lightMaterial.setDiffuseColor(Color.YELLOW);
        lightMaterial.setSpecularColor(Color.rgb(255, 255, 255));
        lightMaterial.setSelfIlluminationMap(glowImage);
        lightSphere.setMaterial(lightMaterial);

        // Create a TriangleMesh
        TriangleMesh mesh = new TriangleMesh();

        // Define size and resolution of the mesh
        final int width = 200;
        final int depth = 300;
        final float cellSize = 8.0f;

        // Create vertices
        for (int z = 0; z <= depth; z++) {
            for (int x = 0; x <= width; x++) {
                float height = (float) PerlinNoise.noise(x * noiseScale, z * noiseScale, 0) * 35;
                mesh.getPoints().addAll(x * cellSize, height, z * cellSize);
            }
        }

        // Create texture coordinates
        mesh.getTexCoords().addAll(0, 0);

        // Create faces (two triangles per cell)
        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++) {
                int tl = z * (width + 1) + x; // top-left
                int bl = (z + 1) * (width + 1) + x; // bottom-left
                int tr = tl + 1; // top-right
                int br = bl + 1; // bottom-right

                mesh.getFaces().addAll(tl, 0, bl, 0, tr, 0);
                mesh.getFaces().addAll(tr, 0, bl, 0, br, 0);
            }
        }

        // Create a MeshView and add it to the scene
        MeshView meshView = new MeshView(mesh);
        meshView.setTranslateX(-width * cellSize / 2.0f);
        meshView.setTranslateY(-100);
        meshView.setTranslateZ(-depth * cellSize / 2.0f);
        PhongMaterial material = new PhongMaterial(Color.GREEN);
        meshView.setMaterial(material);

        // Wireframe mode
        // meshView.setDrawMode(DrawMode.LINE);

        root.getChildren().add(meshView);
        root.getChildren().add(pointLight);
        root.getChildren().add(lightSphere);

        primaryStage.setTitle("3D Perlin Noise Terrain");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
