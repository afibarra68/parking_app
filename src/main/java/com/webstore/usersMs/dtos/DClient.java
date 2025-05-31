package com.webstore.usersMs.dtos;

import javax.validation.constraints.NotNull;

import com.webstore.usersMs.entities.Product;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class DClient {

    private Long clientId;

    private String hashClient;

    @NotNull
    private List<Product> products;

}
