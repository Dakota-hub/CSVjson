package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Задача 1: CSV → JSON");
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> fromCsv = parseCSV(columnMapping, "data.csv");
        String jsonCsv = listToJson(fromCsv);
        writeString(jsonCsv, "data.json");
        System.out.println("data.json создан");

        System.out.println("\nЗадача 2: XML → JSON");
        List<Employee> fromXml = parseXML("data.xml");
        String jsonXml = listToJson(fromXml);
        writeString(jsonXml, "data2.json");
        System.out.println("data2.json создан");

        System.out.println("\nПример объектов:");
        fromXml.forEach(System.out::println);
    }


    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .withSkipLines(0)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse();
        } catch (IOException e) {
            System.err.println("Ошибка при чтении CSV: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public static String listToJson(List<Employee> list) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    public static void writeString(String json, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
        } catch (IOException e) {
            System.err.println("Ошибка записи JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static List<Employee> parseXML(String fileName) {
        List<Employee> employees = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc = builder.parse(fileName);

            Element root = doc.getDocumentElement();

            NodeList nodeList = root.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                Element employeeElement = (Element) node;

                long id = Long.parseLong(employeeElement.getElementsByTagName("id").item(0).getTextContent());
                String firstName = employeeElement.getElementsByTagName("firstName").item(0).getTextContent();
                String lastName = employeeElement.getElementsByTagName("lastName").item(0).getTextContent();
                String country = employeeElement.getElementsByTagName("country").item(0).getTextContent();
                int age = Integer.parseInt(employeeElement.getElementsByTagName("age").item(0).getTextContent());

                Employee emp = new Employee(id, firstName, lastName, country, age);
                employees.add(emp);
            }

        } catch (Exception e) {
            System.err.println("Ошибка при парсинге XML: " + e.getMessage());
            e.printStackTrace();
        }

        return employees;
    }
}