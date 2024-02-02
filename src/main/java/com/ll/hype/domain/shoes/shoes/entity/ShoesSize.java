package com.ll.hype.domain.shoes.shoes.entity;

import com.ll.hype.global.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ShoesSize extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Shoes shoes;

    private int size;

    public void addShoes(Shoes shoes) {
        this.shoes = shoes;
    }
}
