package com.webstore.usersMs.entities;

import static jakarta.persistence.GenerationType.SEQUENCE;
import com.webstore.usersMs.entities.enums.ERole;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
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
@Table(name = "user_role")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class UserRole {

    private static final String ROLE_ID_SEQ = "role_id_sequence";


    @Id
    @GeneratedValue(generator = ROLE_ID_SEQ, strategy = SEQUENCE)
    @SequenceGenerator(name = ROLE_ID_SEQ, sequenceName = ROLE_ID_SEQ, allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long rolId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ERole role = ERole.USER_APP;

}
