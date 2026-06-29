package com.ftn.sbnz.service.util;

import com.ftn.sbnz.model.cep.*;
import com.ftn.sbnz.model.forward.NurseAction;
import com.ftn.sbnz.model.forward.Symptom;
import com.ftn.sbnz.model.general.Patient;
import com.ftn.sbnz.model.template.ProtocolInformation;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class WardSimulator {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void broadcastEvent(String eventType) {
        messagingTemplate.convertAndSend("/topic/events", eventType);
    }

    private static final String[] PROTOCOLS = {
            "CHOP", "CHOP", "ABVD", "ABVD", "FOLFOX", "FOLFOX", "FEC", "BEP", "BEP", "TC"
    };
    private static final int[] CYCLES = {
            1, 2, 1, 2, 1, 2, 1, 1, 2, 1
    };

    private final double HICCUP_CHANCE = 0.1;
    private final double COUGH_CHANCE = 0.1;
    private final double PAIN_CHANCE = 0.05;
    private final double SKIN_CHANGE_CHANCE = 0.005;


    private final double TEMP_CHANGE_CHANCE = 1.0;
    private final double UPPER_PRESSURE_CHANGE_CHANCE = 0.3;
    private final double LOWER_PRESSURE_CHANGE_CHANCE = 0.1;
    private final double BREATHING_CHANGE_CHANCE = 0.5;


    private final double TEMP_CHANGE_LIMIT = 0.05;
    private final int UPPER_PRESSURE_CHANGE_LIMIT = 1;
    private final int LOWER_PRESSURE_CHANGE_LIMIT = 1;
    private final double BREATHING_CHANGE_LIMIT = 0.2;

    private final double PAPERWORK_ASSIGMENT_CHANCE = 0.5;
    private final double PAPERWORK_EVENT_CHANCE = 0.1;

    private final double NEW_PATIENT_CHANCE = 0.2;

    private final double BREATHING_CYCLE_DEFAULT = 5;

    private final KieContainer kContainer;
    private KieSession forwardSession;
    private KieSession cepSession;
    private KieSession templateSession;

    private int totalNursesWithPatients;
    private int totalNursesWithPaperwork;
    private int waitingPatients;
    private boolean isWorkingWithPatients;
    private boolean isInjecting = false;

    private double currentTemperature;
    private int currentLowerPressure;
    private int currentUpperPressure;
    private double currentBreathingPattern;

    public void setInjecting(boolean injecting) {
        this.isInjecting = injecting;
    }

    private Patient currentPatient;

    public WardSimulator() {
        KieServices ks = KieServices.Factory.get();
        this.kContainer = ks
                .newKieContainer(ks.newReleaseId("com.ftn.sbnz", "kjar", "0.0.1-SNAPSHOT"));
        KieScanner kScanner = ks.newKieScanner(kContainer);
        kScanner.start(1000);
        forwardSession = kContainer.newKieSession("forwardChainingKsession");
        cepSession = kContainer.newKieSession("cepKsession");
        templateSession = kContainer.newKieSession("templateProtocolSession");
        waitingPatients = 5;
    }

    public boolean submitReadings(InitialVitalsReadingDTO initialVitals) {
        createNewPatient(initialVitals);

        forwardSession.insert(currentPatient);

        if (initialVitals.getHadPainComplaints()) {
             forwardSession.insert(new Symptom("BOLOVI"));
             System.out.println("HELLOOOOOOOOOOOOOOO");
         }
         if (initialVitals.getHadMedicalIssues()) {
             forwardSession.insert(new Symptom("TEGOBE"));
         }

        forwardSession.fireAllRules();

        Collection<?> actions = forwardSession.getObjects(new ClassObjectFilter(NurseAction.class));

        boolean startTherapy = false;
        for (Object obj : actions) {
            NurseAction nurseAction = (NurseAction) obj;
            String actionType = nurseAction.getAction();

            if ("ZAPOCETI_TERAPIJU".equals(actionType)) {
                System.out.println("OBAVESTITI LEKARA!");
                startTherapy = true;
            } else if ("OBAVESTITI_LEKARA".equals(actionType)) {
                System.out.println("OBAVESTITI LEKARA!");
            } else if ("HITNO_OBAVESTITI_LEKARA".equals(actionType)) {
                System.out.println("HITNO OBAVESTITI LEKARA!!!");
            }
        }

        checkIfPatientCameIn();
        return startTherapy;
    }

    public ProtocolInformationDTO getProtocolInfo() {
        templateSession.insert(currentPatient);
        templateSession.fireAllRules();

        Collection<?> results = templateSession.getObjects(new ClassObjectFilter(ProtocolInformation.class));

        if (results == null || results.isEmpty()) {
            return null;
        }

        ProtocolInformation info = (ProtocolInformation) results.iterator().next();
        ProtocolInformationDTO dto = new ProtocolInformationDTO();

        dto.setPrepAmpouleName(info.getPrepAmpouleName());
        dto.setPrepAmpouleAmount(info.getPrepAmpouleAmount());
        dto.setMedicineName(info.getMedicineName());
        dto.setMedicineAmount(info.getMedicineAmount());
        dto.setMedicineSolventAmount(info.getMedicineSolventAmount());

        if (info.getPrepAmpouleTime() != null) {
            String prepTimeStr = info.getPrepAmpouleTime().replace(" min", "").trim();
            dto.setPrepAmpouleTime(Double.parseDouble(prepTimeStr));
        }

        if (info.getMedicineTime() != null) {
            String medTimeStr = info.getMedicineTime().replace(" min", "").trim();
            dto.setMedicineTime(Double.parseDouble(medTimeStr));
        }

        currentTemperature = currentPatient.getCurrentTemperature();
        currentLowerPressure = currentPatient.getCurrentLowerPressure();
        currentUpperPressure = currentPatient.getCurrentUpperPressure();
        currentBreathingPattern = currentPatient.getCurrentBreathingCycle();

        cepSession.insert(currentPatient);

        return dto;
    }

    public boolean finishPatient() {
        waitingPatients--;
        boolean changeJob = false;
        if (ThreadLocalRandom.current().nextDouble() < PAPERWORK_EVENT_CHANCE) {
            if (ThreadLocalRandom.current().nextDouble() < PAPERWORK_ASSIGMENT_CHANCE) {
                changeJob = true;
            }
        }
        if (waitingPatients <= 0) {
            changeJob = true;
        }
        checkIfPatientCameIn();
        flushSessions();
        return changeJob;
    }

    public boolean doPaperwork() {
        boolean changeJob = false;
        if (ThreadLocalRandom.current().nextDouble() < PAPERWORK_EVENT_CHANCE) {
            if (ThreadLocalRandom.current().nextDouble() < PAPERWORK_ASSIGMENT_CHANCE) {
                changeJob = true;
            }
        }
        if (waitingPatients <= 0) {
            changeJob = false;
        }
        checkIfPatientCameIn();
        return changeJob;
    }

    private void checkIfPatientCameIn() {
        if (ThreadLocalRandom.current().nextDouble() < NEW_PATIENT_CHANCE) {
            waitingPatients++;
        }
    }

    private void createNewPatient(InitialVitalsReadingDTO initialVitals) {
        currentPatient = new Patient();
        currentPatient.setCurrentTemperature(initialVitals.getTemperature());
        currentPatient.setCurrentLowerPressure(initialVitals.getLowerPressure());
        currentPatient.setCurrentUpperPressure(initialVitals.getUpperPressure());
        currentPatient.setCurrentBreathingCycle(BREATHING_CYCLE_DEFAULT);

        int randomIndex = ThreadLocalRandom.current().nextInt(PROTOCOLS.length);
        currentPatient.setProtocol(PROTOCOLS[randomIndex]);
        currentPatient.setCurrentCycle(CYCLES[randomIndex]);
    }

    private void flushSessions() {
        forwardSession.dispose();
        templateSession.dispose();
        cepSession.dispose();

        forwardSession = kContainer.newKieSession("forwardChainingKsession");
        cepSession = kContainer.newKieSession("cepKsession");
        templateSession = kContainer.newKieSession("templateProtocolSession");
    }

    @Scheduled(fixedRate = 1000)
    public void simulateInjectionEvents() {
        if (!isInjecting) return;

        if (ThreadLocalRandom.current().nextDouble() < TEMP_CHANGE_CHANCE) {
            currentTemperature += (ThreadLocalRandom.current().nextDouble() * 2 * TEMP_CHANGE_LIMIT) - TEMP_CHANGE_LIMIT;
            cepSession.insert(new TemperatureReadingEvent(currentTemperature));
        }

        if (ThreadLocalRandom.current().nextDouble() < UPPER_PRESSURE_CHANGE_CHANCE) {
            currentUpperPressure += (ThreadLocalRandom.current().nextInt(2) * UPPER_PRESSURE_CHANGE_LIMIT) - UPPER_PRESSURE_CHANGE_LIMIT;
        }
        if (ThreadLocalRandom.current().nextDouble() < LOWER_PRESSURE_CHANGE_CHANCE) {
            currentLowerPressure += (ThreadLocalRandom.current().nextInt(2) * LOWER_PRESSURE_CHANGE_LIMIT) - LOWER_PRESSURE_CHANGE_LIMIT;
        }
        // Insert pressure reading if either value was potentially changed
        cepSession.insert(new PressureReadingEvent(currentUpperPressure, currentLowerPressure));

        if (ThreadLocalRandom.current().nextDouble() < BREATHING_CHANGE_CHANCE) {
            currentBreathingPattern += (ThreadLocalRandom.current().nextDouble() * 2 * BREATHING_CHANGE_LIMIT) - BREATHING_CHANGE_LIMIT;
            cepSession.insert(new BreathingReadingEvent(currentBreathingPattern));
        }

        if (ThreadLocalRandom.current().nextDouble() < COUGH_CHANCE) {
            cepSession.insert(new CoughEvent());
            broadcastEvent("COUGH");
        }

        if (ThreadLocalRandom.current().nextDouble() < HICCUP_CHANCE) {
            cepSession.insert(new HiccupEvent());
            broadcastEvent("HICCUP");
        }

        boolean hadPainComplaint = false;
        boolean hadSkinChange = false;

        if (ThreadLocalRandom.current().nextDouble() < PAIN_CHANCE) {
            cepSession.insert(new PainComplaintEvent());
            hadPainComplaint = true;
        }

        if (ThreadLocalRandom.current().nextDouble() < SKIN_CHANGE_CHANCE) {
            cepSession.insert(new SkinChangeEvent());
            hadSkinChange = true;
        }

        boolean criticalTempBefore = !cepSession.getObjects(new ClassObjectFilter(CriticalTemperatureChangeEvent.class)).isEmpty();
        boolean criticalPressureBefore = !cepSession.getObjects(new ClassObjectFilter(CriticalPressureChangeEvent.class)).isEmpty();

        cepSession.fireAllRules();

        boolean breathingPatternAfter = !cepSession.getObjects(new ClassObjectFilter(BreathingPatternChangeEvent.class)).isEmpty();
        boolean criticalTempAfter = !cepSession.getObjects(new ClassObjectFilter(CriticalTemperatureChangeEvent.class)).isEmpty();
        boolean criticalPressureAfter = !cepSession.getObjects(new ClassObjectFilter(CriticalPressureChangeEvent.class)).isEmpty();

        if (breathingPatternAfter) {
            if (!cepSession.getObjects(new ClassObjectFilter(CoughEvent.class)).isEmpty()) {
                broadcastEvent("RULE9_COUGH_WITH_BREATHING_CHANGE");
            }
            if (!cepSession.getObjects(new ClassObjectFilter(HiccupEvent.class)).isEmpty()) {
                broadcastEvent("RULE9_HICCUP_WITH_BREATHING_CHANGE");
            }
        }

        if (hadPainComplaint) {
            broadcastEvent("RULE10_PAIN_COMPLAINT");
        }
        if (hadSkinChange) {
            broadcastEvent("RULE10_SKIN_CHANGE");
        }
        if (!criticalTempBefore && criticalTempAfter) {
            broadcastEvent("RULE10_CRITICAL_TEMPERATURE");
        }
        if (!criticalPressureBefore && criticalPressureAfter) {
            broadcastEvent("RULE10_CRITICAL_PRESSURE");
        }
    }
}
