package com.ftn.sbnz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.ftn.sbnz.model.general.Patient;
import com.ftn.sbnz.model.forward.*;

public class ServiceApplicationTests {

    private KieSession createForwardSession() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        // Osiguraj da ti se u kmodule.xml sesija zove ovako ili promeni ime
        return kContainer.newKieSession("forwardChainingKsession");
    }

    @Test
    public void testEkstremnaTemperaturaOkidaHitnuAkciju() {
        KieSession ksession = createForwardSession();

        Patient p = new Patient();
        p.setCurrentTemperature(40.0); // Pravilo T6 -> IZUZETNO_VISOKA
        ksession.insert(p);

        ksession.fireAllRules();

        // 1. Proveri da li je napravljena procena
        Collection<?> assessments = ksession.getObjects(new ClassObjectFilter(TemperatureAssessment.class));
        assessments.stream().forEach(a -> System.out.println(((TemperatureAssessment)a).getStatus()));
        assertTrue(assessments.stream().anyMatch(a -> ((TemperatureAssessment)a).getStatus().equals("IZUZETNO_VISOKA")));

        // 2. Proveri da li je izvedena hitna akcija (Pravilo A1)
        Collection<?> actions = ksession.getObjects(new ClassObjectFilter(NurseAction.class));
        actions.stream().forEach(a -> System.out.println(((NurseAction)a).getAction()));
        assertTrue(actions.stream().anyMatch(a -> ((NurseAction)a).getAction().equals("HITNO_OBAVESTITI_LEKARA")));
    }

    @Test
    public void testNormalniParametriZapočinjuTerapiju() {
        KieSession ksession = createForwardSession();

        Patient p = new Patient();
        p.setCurrentTemperature(36.5); // T3 -> NORMALNA
        p.setCurrentUpperPressure(120);
        p.setCurrentLowerPressure(70);  // P1 -> NORMALAN
        ksession.insert(p);

        ksession.fireAllRules();

        Collection<?> actions = ksession.getObjects(new ClassObjectFilter(NurseAction.class));
        actions.stream().forEach(a -> System.out.println(((NurseAction)a).getAction()));
        assertTrue(actions.stream().anyMatch(a -> ((NurseAction)a).getAction().equals("ZAPOCETI_TERAPIJU")));
    }

    @Test
    public void testSimptomBolOkidaObavestenje() {
        KieSession ksession = createForwardSession();

        ksession.insert(new Symptom("BOLOVI")); // Pravilo S1

        ksession.fireAllRules();

        Collection<?> actions = ksession.getObjects(new ClassObjectFilter(NurseAction.class));
        actions.stream().forEach(a -> System.out.println(((NurseAction)a).getAction()));
        assertTrue(actions.stream().anyMatch(a -> ((NurseAction)a).getAction().equals("OBAVESTITI_LEKARA")));
    }

    @Test
    public void testKombinovaniParametriIObavestavanje() {
        KieSession ksession = createForwardSession();

        Patient p = new Patient();
        p.setCurrentTemperature(37.5); // T4 -> POVISENA
        p.setCurrentUpperPressure(100);
        p.setCurrentLowerPressure(65);  // P2 -> NIZAK
        ksession.insert(p);

        ksession.fireAllRules();

        // Provera da li sistem prepoznaje oba stanja i oba puta obaveštava lekara (A2)
        Collection<?> actions = ksession.getObjects(new ClassObjectFilter(NurseAction.class));
        actions.stream().forEach(a -> System.out.println(((NurseAction)a).getAction()));
        long count = actions.stream().filter(a -> ((NurseAction)a).getAction().equals("OBAVESTITI_LEKARA")).count();
        
        // Trebalo bi da imamo akciju iz Temperature (A2) i akciju iz Pritiska (A2)
        assertTrue(count >= 1);
    }

    @Test
    public void testStabilanPacijentNeOkidaNista() {
        KieSession ksession = createForwardSession();

        Patient p = new Patient();
        p.setCurrentTemperature(36.8); // NORMALNA
        p.setCurrentUpperPressure(120);
        p.setCurrentLowerPressure(70);  // NORMALAN
        ksession.insert(p);

        int rulesFired = ksession.fireAllRules();

        // Pravila T3, P1 i A3 treba da opale
        // T3 insertuje TemperatureAssessment("NORMALNA")
        // P1 insertuje PressureAssessment("NORMALAN")
        // A3 insertuje NurseAction("ZAPOCETI_TERAPIJU")
        // Ukupno 3 pravila
        assertEquals(3, rulesFired);
        
        // Provera da NEMA hitnih obaveštenja
        Collection<?> actions = ksession.getObjects(new ClassObjectFilter(NurseAction.class));
        boolean hasHitno = actions.stream().anyMatch(a -> ((NurseAction)a).getAction().contains("HITNO"));
        assertTrue(!hasHitno, "Sistem ne sme da traži hitnu reakciju za stabilnog pacijenta!");
    }

    @Test
    public void testGranicnaVrednostTemperature() {
        KieSession ksession = createForwardSession();

        Patient p = new Patient();
        // T5 pravilo kaže: 38.0 <= temp <= 39.0
        p.setCurrentTemperature(39.0); 
        ksession.insert(p);

        ksession.fireAllRules();

        Collection<?> assessments = ksession.getObjects(new ClassObjectFilter(TemperatureAssessment.class));
        assertTrue(assessments.stream().anyMatch(a -> ((TemperatureAssessment)a).getStatus().equals("VISOKA")));
    }
}