package com.example.lucas.haushaltsmanager.ExpenseImporter;

//import com.example.lucas.haushaltsmanager.Entities.File;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.IFileReader;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ILine;

import java.io.File;
import java.util.Scanner;

public class FileReader implements IFileReader {
    private File file;
    private Scanner scanner;

    public FileReader(File file) {
        this.file = file;
//        scanner = new Scanner(file);
    }

    @Override
    public ILine get(int lineNumber) {
        return null;
    }

    @Override
    public ILine getCurrent() {
        return null;
    }

    @Override
    public boolean moveToNext() {
        return false;
    }

    @Override
    public int getCount() {
        int result = 0;
//        try (
//                FileReader input = new FileReader("input.txt");
//                LineNumberReader count = new LineNumberReader(input);) {
//            while (count.skip(Long.MAX_VALUE) > 0) {
//                // Loop just in case the file is > Long.MAX_VALUE or skip() decides to not read the entire file
//            }
//
//            result = count.getLineNumber() + 1;
//        }
//
        return result;
    }

//    private void test() {
//        Scanner scanner = new Scanner(new File("/Users/pankaj/Downloads/myfile.txt"));
//
//        while (scanner.hasNextLine()) {
//            System.out.println(scanner.nextLine());
//        }
//    }
}
