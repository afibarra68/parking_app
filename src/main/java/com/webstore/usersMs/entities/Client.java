package com.webstore.usersMs.entities;

import static jakarta.persistence.GenerationType.SEQUENCE;

import javax.validation.constraints.NotNull;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.List;
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
@Table(name = "client")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Client {

    private static final String USER_ID_SEQ = "client_id_seq";

    @Id
    @GeneratedValue(generator = USER_ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = USER_ID_SEQ, sequenceName = USER_ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long clientId;

    @NotNull
    @JoinColumn(table = "hash_client")
    private String hashClient;

    @NotNull
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "client_has_product",
        joinColumns = @JoinColumn(name = "client_client_id"),
        inverseJoinColumns = @JoinColumn(name = "product_product_id")
    )
    private List<Product> products;

}
