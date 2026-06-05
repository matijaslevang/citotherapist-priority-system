package com.ftn.sbnz.service;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

import com.ftn.sbnz.model.general.Patient;
import com.ftn.sbnz.model.cep.*;

public class CEPRulesTest {

    // Helper metoda koja kreira KIE Sesiju direktno iz DRL fajla sa uključenim STREAM modom
    private KieSession createCEPSession() {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        
        // Podesi tačnu putanju do tvog DRL fajla za CEP
        kfs.write(ResourceFactory.newClassPathResource("z_cep/cep-rules.drl"));

        KieBuilder kbuilder = ks.newKieBuilder(kfs);
        kbuilder.buildAll();
        
        if (kbuilder.getResults().hasMessages(Message.Level.ERROR)) {
            throw new IllegalArgumentException("Greška u kompilaciji DRL-a: " + kbuilder.getResults());
        }
        
        KieContainer kContainer = ks.newKieContainer(kbuilder.getKieModule().getReleaseId());
        KieBaseConfiguration kbconf = ks.newKieBaseConfiguration();
        // Za CEP je obavezan STREAM mod
        kbconf.setOption(EventProcessingOption.STREAM);
        KieBase kbase = kContainer.newKieBase(kbconf);

        return kbase.newKieSession();
    }

    @Test
    public void testPravilo10_HitnoObavestenjeLekara_Bol() {
        KieSession ksession = createCEPSession();

        // Ubacujemo samo žaljenje na bol
        ksession.insert(new PainComplaintEvent());

        int firedRules = ksession.fireAllRules();

        // Pravilo 10 bi moralo da opali i odmah uradi drools.halt()
        assertThat(firedRules, equalTo(1));
    }

    @Test
    public void testPravilo9_PromenaDisanjaIKasalj() {
        KieSession ksession = createCEPSession();

        Patient patient = new Patient();
        patient.setCurrentBreathingCycle(4.0);
        ksession.insert(patient);

        // Ubacujemo 3 očitavanja disanja sa promenom >= 0.5 (okida Pravilo 7)
        // Pod pretpostavkom da konstruktor prima (double cycleLength)
        ksession.insert(new BreathingReadingEvent(4.6)); 
        ksession.insert(new BreathingReadingEvent(5.2)); 
        ksession.insert(new BreathingReadingEvent(5.8)); 

        ksession.fireAllRules(); // Ovde će se okinuti Pravilo 7 (3x) i Pravilo 8 (1x)

        // Proveravamo da li je sistem izveo zaključak o promeni disanja
        Collection<?> breathingChanges = ksession.getObjects(new ClassObjectFilter(BreathingPatternChangeEvent.class));
        assertThat(breathingChanges.size(), equalTo(1));

        // Zatim ubacujemo kašalj
        ksession.insert(new CoughEvent());
        int firedRules = ksession.fireAllRules();

        // Sada Pravilo 9 mora da opali jer imamo i promenu disanja i kašalj
        assertThat(firedRules, equalTo(1));
    }

    @Test
    public void testEskalacijaTemperature_P1_do_P10() {
        KieSession ksession = createCEPSession();

        Patient patient = new Patient();
        patient.setCurrentTemperature(36.0);
        ksession.insert(patient);

        int totalFiredRules = 0;
        double currentTemp = 36.0;
        
        // Simulacija postepenog rasta temperature (okidamo pravila posle svakog merenja)
        for (int i = 0; i < 15; i++) {
            currentTemp += 0.15; // Promena veća od 0.1
            ksession.insert(new TemperatureReadingEvent(currentTemp));
            
            // KLJUČNA PROMENA: Okidamo pravila odmah po pristizanju događaja
            totalFiredRules += ksession.fireAllRules(); 
        }

        // Proveravamo da li je kreiran kritičan događaj za temperaturu
        Collection<?> criticalEvents = ksession.getObjects(new ClassObjectFilter(CriticalTemperatureChangeEvent.class));
        assertEquals(1, criticalEvents.size());

        // Pravila koja moraju da opale: 
        // P1 (15 puta) + P3 (3 puta) + P4 (1 put) + P10 (1 put) = 20 pravila ukupno
        assertEquals(20, totalFiredRules);
    }

    @Test
    public void testEskalacijaPritiska_P2_do_P10() {
        KieSession ksession = createCEPSession();

        Patient patient = new Patient();
        patient.setCurrentUpperPressure(120);
        patient.setCurrentLowerPressure(80);
        ksession.insert(patient);

        int totalFiredRules = 0;
        int currentUpper = 120;
        
        // Okidamo pravila posle svakog merenja
        for (int i = 0; i < 15; i++) {
            currentUpper += 3;
            ksession.insert(new PressureReadingEvent(currentUpper, 80));
            
            totalFiredRules += ksession.fireAllRules();
        }

        // Proveravamo da li je kreiran kritičan događaj za pritisak
        Collection<?> criticalEvents = ksession.getObjects(new ClassObjectFilter(CriticalPressureChangeEvent.class));
        assertEquals(1, criticalEvents.size());

        // P2 (15) + P5 (3) + P6 (1) + P10 (1) = 20
        assertEquals(20, totalFiredRules);
    }
}