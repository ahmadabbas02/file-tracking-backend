package com.ahmadabbas.filetracking.backend.student.payload;

import com.ahmadabbas.filetracking.backend.student.EducationStatus;
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
    private String firstName;
    @CsvBindByName
    private String lastName;
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
    private EducationStatus educationStatus;
    @CsvBindByName
    private String picture;
    @CsvBindByName
    private boolean isEnabled;
}
