package org.example;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Post model = new Post();
            PostView view = new PostView();

            PostController controller =
                    new PostController(model, view, scanner);

            controller.run();
        }
    }
}