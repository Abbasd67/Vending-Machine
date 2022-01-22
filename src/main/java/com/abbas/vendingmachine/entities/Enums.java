package com.abbas.vendingmachine.entities;

import java.util.Arrays;
import java.util.Optional;

public class Enums {

    public enum Role {
        ADMIN,
        SELLER,
        BUYER
    }

    public enum DepositType {
        FIVE(5),
        TEN(10),
        TWENTY(20),
        FIFTY(50),
        HUNDRED(100);
        public final int amount;

        DepositType(int amount) {
            this.amount = amount;
        }

        public static Optional<DepositType> valueOf(int value) {
            return Arrays.stream(values())
                    .filter(v -> v.amount == value)
                    .findFirst();
        }
    }
}
