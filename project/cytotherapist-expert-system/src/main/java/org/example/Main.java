package org.example;

import org.example.model.Pacijent;
import org.example.model.SistemStatus;
import org.example.model.StatusPoruka;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.io.Resource;
import org.kie.internal.io.ResourceFactory;

public class Main {

    public static void main(String[] args) {
        try {
            KieServices ks = KieServices.Factory.get();
            KieFileSystem kfs = ks.newKieFileSystem();

// Load the file directly from resources regardless of kmodule.xml
            Resource res = ResourceFactory.newClassPathResource("src/main/resources/rules.drl");
            kfs.write("rules.drl", res);

            KieBuilder kbuilder = ks.newKieBuilder(kfs);
            kbuilder.buildAll();

            KieContainer kContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
            KieSession kSession = kContainer.newKieSession(); // This will now work directly!

            System.out.println("--- SCENARIO 1: Optimal Patient (Should Start Therapy) ---");
            runScenario1(kContainer);

            System.out.println("\n--- SCENARIO 2: Emergency & Critical Patient ---");
            runScenario2(kContainer);

            System.out.println("\n--- SCENARIO 3: Staff Balance Optimization ---");
            runScenario3(kContainer);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Scenario 1 tests a normal patient under stable clinic conditions.
     * Expected chain: Categorizes temperature and pressure as NORMAL, then inserts "ZAPOCNI_TERAPIJU".
     */
    private static void runScenario1(KieContainer kContainer) {
        KieSession kSession = kContainer.newKieSession(); // Creates a fresh session

        // Create a patient with stable vitals
        Pacijent pacijent = new Pacijent();
        pacijent.setStarost(45);
        pacijent.setTemperatura(36.6); // Normal [cite: 35]
        pacijent.setPritisak("120/70"); // Normal [cite: 37]
        pacijent.setZaliSeNaBolove(false);
        pacijent.setZaliSeNaTegobe(false);

        SistemStatus status = new SistemStatus();
        status.setBrojPacijenataNaPrijemu(0);
        status.setBrojSestaraPacijenti(3);
        status.setBrojSestaraPapirologija(3); // Balanced [cite: 22]
        status.setRadSaPacijentima(true);

        // Insert facts into working memory
        kSession.insert(pacijent);
        kSession.insert(status);

        // Fire all rules
        kSession.fireAllRules();
        kSession.dispose();
    }

    /**
     * Scenario 2 tests an emergency trigger combined with a patient showing critical vitals.
     * Expected chain: Triggers emergency protocol, routes nurses, handles age warning,
     * flags critical vitals, and updates to "LEKAR_HITNO_OBAVESTEN".
     */
    private static void runScenario2(KieContainer kContainer) {
        KieSession kSession = kContainer.newKieSession("defaultKieSession");

        // Elderly patient with critically high fever
        Pacijent pacijent = new Pacijent();
        pacijent.setStarost(72); // > 65 -> trigger careful needle placement [cite: 28]
        pacijent.setTemperatura(39.5); // Critically high [cite: 36]
        pacijent.setPritisak("120/70");

        SistemStatus status = new SistemStatus();
        status.setBrojPacijenataNaPrijemu(1);
        status.setBrojSestaraPacijenti(2);
        status.setBrojSestaraPapirologija(4); // Imbalance will trigger nurse redistribution [cite: 29]
        status.setRadSaPacijentima(true);

        // Simulated event: Incoming critically ill emergency patient [cite: 31]
        StatusPoruka hitanSlucaj = new StatusPoruka("DOSAO_HITAN_PACIJENT");

        kSession.insert(pacijent);
        kSession.insert(status);
        kSession.insert(hitanSlucaj);

        kSession.fireAllRules();
        kSession.dispose();
    }

    /**
     * Scenario 3 tests the self-correcting workforce optimization balance rules.
     * Expected chain: Detects more nurses on paperwork and automatically moves them to active patient care.
     */
    private static void runScenario3(KieContainer kContainer) {
        KieSession kSession = kContainer.newKieSession();

        SistemStatus status = new SistemStatus();
        status.setBrojPacijenataNaPrijemu(0);
        status.setBrojSestaraPacijenti(2);
        status.setBrojSestaraPapirologija(6); // Heavily imbalanced [cite: 29]
        status.setRadSaPacijentima(false);

        kSession.insert(status);

        System.out.println("Before engine: Nurses with Patients = " + status.getBrojSestaraPacijenti()
                + ", Paperwork = " + status.getBrojSestaraPapirologija());

        kSession.fireAllRules();

        System.out.println("After engine execution: Nurses with Patients = " + status.getBrojSestaraPacijenti()
                + ", Paperwork = " + status.getBrojSestaraPapirologija());

        kSession.dispose();
    }
}