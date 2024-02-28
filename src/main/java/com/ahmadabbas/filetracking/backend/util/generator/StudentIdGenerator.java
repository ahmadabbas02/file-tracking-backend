package com.ahmadabbas.filetracking.backend.util.generator;

import com.ahmadabbas.filetracking.backend.student.Student;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class StudentIdGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object o) {
        LocalDate localDate = LocalDate.now();
        int currentYear = localDate.getYear() - 2000;
        String query = "SELECT MAX(CAST(id AS DECIMAL)) FROM student WHERE id LIKE '" + currentYear + "%'";
        if (o instanceof Student student) {
            // if id is provided, don't generate
            if (student.getId() != null && !student.getId().isBlank()) {
                return student.getId();
            }
            try (Connection connection = session.getJdbcConnectionAccess().obtainConnection();
                 PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet rs = statement.executeQuery()
            ) {
                if (rs.next()) {
                    String lastId = rs.getString(1);
                    int uniqueId = (lastId != null)
                            ? Integer.parseInt(lastId.substring(2, 8)) + 1 // because first 2 digits are year
                            : 1;
                    return currentYear + addZeroPadding(uniqueId);
                } else {
                    return currentYear + addZeroPadding(1);
                }
            } catch (SQLException e) {
                throw new HibernateException("Unable to generate ID", e);
            }
        } else {
            throw new UnsupportedOperationException("StudentIdGenerator only supports generating ids for Student entity.");
        }
    }

    private String addZeroPadding(int number) {
        String numberStr = String.valueOf(number);
        int zerosToAdd = 6 - numberStr.length();
        return "0".repeat(Math.max(0, zerosToAdd)) + numberStr;
    }
}
