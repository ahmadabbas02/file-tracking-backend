package com.ahmadabbas.filetracking.backend.document.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


public class SubCategoryPK implements Serializable {
    private Integer parentCategoryId;
    private Integer categoryId;
}
