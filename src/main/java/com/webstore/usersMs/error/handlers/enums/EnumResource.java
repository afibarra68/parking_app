package com.webstore.usersMs.error.handlers.enums;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EnumResource implements Serializable {

   @EqualsAndHashCode.Include
   private String id;

   @EqualsAndHashCode.Exclude
   private String description;

   @EqualsAndHashCode.Exclude
   private String descriptionExtended;

   public EnumResource(String id) {
      this.id = id;
   }

   public EnumResource(String id, String description) {
      this.id = id;
      this.description = description;
   }
}
