package com.manish.util;

import lombok.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Person {
    private Integer id;
    private String firstName;
    private String lastName;
    private int age;

    public static Stream<Person> getPersons(int count) {
        String[] names = {"Manish", "Amit", "Tarun", "Chander", "Hemant"};
        String[] lastNames = {"Maheshwari", "Shah", "Sharma", "Ahuja", "Loha"};
        List<Person> persons = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            Person p = new Person(i + 1, names[i % names.length], lastNames[i % names.length], (i + 1) * 10);
            persons.add(p);
        }
        return persons.stream();
    }

    /**
     * @return a random Person instance.
     */
    public static Person getSomePerson(){
        String[] names = {"Manish", "Amit", "Tarun", "Chander", "Hemant"};
        String[] lastNames = {"Maheshwari", "Shah", "Sharma", "Ahuja", "Loha"};
        int index = new Random().nextInt(10);
        return new Person(index , names[index % names.length], lastNames[index % names.length], (index + 1) * 10);
    }

    /**
     * @return no one.
     */
    public static Person getNoOne(){
        return new Person(-1 , "NoOne", "NoOne", -1);
    }
}