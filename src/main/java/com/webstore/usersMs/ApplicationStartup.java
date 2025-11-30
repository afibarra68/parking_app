package com.webstore.usersMs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.webstore.usersMs.h2Data.InsertData;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Data
@Component
@Log4j2
@Profile("!test")
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

   private final InsertData insertData;

   private boolean started = false;

   @Autowired
   public ApplicationStartup(InsertData insertData) {
      this.insertData = insertData;
   }

   @Override
   public void onApplicationEvent(@NonNull ApplicationReadyEvent applicationReadyEvent) {
      if (!started) {
         log.info(".::START::.");
         started = true;
         try {
            this.insertData.insertData();
         } catch (Exception ex) {
            log.warn("There was a problem loading the initial issuer admin configuration.", ex);
         }
      }
   }

}
