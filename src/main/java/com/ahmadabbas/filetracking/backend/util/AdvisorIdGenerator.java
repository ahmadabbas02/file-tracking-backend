package com.ahmadabbas.filetracking.backend.util;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AdvisorIdGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object o) {
        LocalDate localDate = LocalDate.now();
        int currentYear = localDate.getYear() - 2000;
        String prefix = "AP";
        String query = "SELECT max(CAST((substring(id,3)) AS DECIMAL)) FROM advisor WHERE id LIKE '" + prefix + currentYear + "%'";
        try (Connection connection = session.getJdbcConnectionAccess().obtainConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()
        ) {
            if (rs.next()) {
                String lastId = rs.getString(1);
                if (lastId != null) {
                    lastId = lastId.replace("AP", "");
                    int year = Integer.parseInt(lastId.substring(0, 2));
                    int uniqueId = Integer.parseInt(lastId.substring(2, 8));
                    if (currentYear == year) {
                        return prefix + currentYear + addZeroPadding(uniqueId + 1);
                    }
                }
            }
        } catch (SQLException e) {
            throw new HibernateException("Unable to generate ID", e);
        }
        return prefix + currentYear + addZeroPadding(1);
    }

    private String addZeroPadding(int number) {
        String numberStr = String.valueOf(number);
        int zerosToAdd = 6 - numberStr.length();
        return "0".repeat(Math.max(0, zerosToAdd)) + numberStr;
    }
}
