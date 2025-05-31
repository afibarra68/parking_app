package com.webstore.usersMs.entities;

import static jakarta.persistence.GenerationType.SEQUENCE;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Product implements Serializable {

    private static final String USER_ID_SEQ = "product_id_seq";

    @Id
    @GeneratedValue(generator = USER_ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = USER_ID_SEQ, sequenceName = USER_ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long productId;

    private String name;



}
