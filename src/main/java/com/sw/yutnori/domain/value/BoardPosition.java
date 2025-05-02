package com.sw.yutnori.domain.value;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class BoardPosition {
    private int x;
    private int y;
    private String label;
}
