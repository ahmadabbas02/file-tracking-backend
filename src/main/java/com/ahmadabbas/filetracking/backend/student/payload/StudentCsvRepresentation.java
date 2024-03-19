package com.ahmadabbas.filetracking.backend.student.payload;

import com.opencsv.bean.CsvBindByName;
import lombok.*;

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
    private String surname;
    @CsvBindByName
    private String password;
    @CsvBindByName
    private String email;
    @CsvBindByName
    private String program;
    @CsvBindByName
    private String phoneNumber;
    @CsvBindByName
    private short year;
    @CsvBindByName
    private String picture;
    @CsvBindByName
    private boolean isEnabled;
}
