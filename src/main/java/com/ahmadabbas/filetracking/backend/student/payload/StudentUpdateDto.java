package com.ahmadabbas.filetracking.backend.student.payload;

import com.ahmadabbas.filetracking.backend.document.base.DocumentStatus;
import com.ahmadabbas.filetracking.backend.exception.APIException;
import com.ahmadabbas.filetracking.backend.student.EducationStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
public class StudentUpdateDto implements Serializable {
    private final String program;
    private final Short year;
    @Setter
    private String advisorId;
    private final DocumentStatus.InternshipCompletionStatus internshipCompletionStatus;
    private final DocumentStatus.InternshipPaymentStatus paymentStatus;
    private final EducationStatus educationStatus;

    public StudentUpdateDto(String program,
                            Short year,
                            String advisorId,
                            DocumentStatus.InternshipCompletionStatus internshipCompletionStatus,
                            DocumentStatus.InternshipPaymentStatus paymentStatus, EducationStatus educationStatus) {
        List<String> allowedProgramValues = Arrays.asList("CMSE", "CMPE", "BLGM");
        if (program != null && !allowedProgramValues.contains(program)) {
            throw new APIException(HttpStatus.BAD_REQUEST,
                    "Only %s are accepted values for student programs".formatted(allowedProgramValues));
        }
        this.program = program;
        this.year = year;
        this.advisorId = advisorId;
        this.internshipCompletionStatus = internshipCompletionStatus;
        this.paymentStatus = paymentStatus;
        this.educationStatus = educationStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentUpdateDto that)) return false;
        return Objects.equals(getProgram(), that.getProgram()) && Objects.equals(getYear(), that.getYear()) && Objects.equals(getAdvisorId(), that.getAdvisorId()) && getInternshipCompletionStatus() == that.getInternshipCompletionStatus() && getPaymentStatus() == that.getPaymentStatus() && getEducationStatus() == that.getEducationStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProgram(), getYear(), getAdvisorId(), getInternshipCompletionStatus(), getPaymentStatus(), getEducationStatus());
    }

    @Override
    public String toString() {
        return "StudentUpdateDto{" +
               "program='" + program + '\'' +
               ", year=" + year +
               ", advisorId='" + advisorId + '\'' +
               ", internshipCompletionStatus=" + internshipCompletionStatus +
               ", paymentStatus=" + paymentStatus +
               '}';
    }
}
