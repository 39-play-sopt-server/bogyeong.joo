package org.example.client;

import org.example.domain.Category;
import java.util.Scanner;

public class PostInput {
    private final Scanner scanner;

    public PostInput(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readText() {
        return scanner.nextLine();
    }

    public int readCommand() {
        return Integer.parseInt(readText().trim());
    }

    public long readPostNumber() {
        return Long.parseLong(readText().trim());
    }

    public Category readCategory() {
        long number = readPostNumber();
        Category[] categories = Category.values();
        if (number < 1 || number > categories.length) {
            throw new IllegalArgumentException("목록에 있는 카테고리를 선택해주세요.");
        }
        return categories[(int) number - 1];
    }
}
