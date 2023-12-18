package com.ahmadabbas.filetracking.backend.student.payload;

import com.opencsv.bean.CsvBindByName;
import lombok.*;

import java.util.StringJoiner;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentCsvRepresentation {
    @CsvBindByName
    private String studentId;
    @CsvBindByName
    private String advisorId;
    @CsvBindByName
    private String name;
    @CsvBindByName
    private String password;
    @CsvBindByName
    private String email;
    @CsvBindByName
    private String department;
    @CsvBindByName
    private short year;
    @CsvBindByName
    private String picture;
    @CsvBindByName
    private boolean isEnabled;

    @Override
    public String toString() {
        return new StringJoiner(", ", StudentCsvRepresentation.class.getSimpleName() + "[", "]")
                .add("studentId='" + studentId + "'")
                .add("advisorId='" + advisorId + "'")
                .add("name='" + name + "'")
                .add("password='" + password + "'")
                .add("email='" + email + "'")
                .add("department='" + department + "'")
                .add("year=" + year)
                .add("picture='" + picture + "'")
                .add("isEnabled=" + isEnabled)
                .toString();
    }
}
