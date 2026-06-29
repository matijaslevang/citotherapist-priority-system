package com.ftn.sbnz.service.controllers;

import com.ftn.sbnz.model.forward.NurseAction;
import com.ftn.sbnz.model.template.ProtocolInformation;
import com.ftn.sbnz.service.util.InitialVitalsReadingDTO;
import com.ftn.sbnz.service.util.ProtocolInformationDTO;
import com.ftn.sbnz.service.util.WardSimulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MainController {

    private WardSimulator simulator;

    @Autowired
    public MainController(WardSimulator simulator) {
        this.simulator = simulator;
    }

    @PostMapping("/initial-vitals-reading")
    public ResponseEntity<Boolean> postInitialVitalsReading(@RequestBody InitialVitalsReadingDTO vitalsReadingDTO) {
        boolean result = simulator.submitReadings(vitalsReadingDTO);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/get-protocol-information")
    public ResponseEntity<ProtocolInformationDTO> getProtocolInformation() {
        ProtocolInformationDTO dto = simulator.getProtocolInfo();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/finish-patient")
    public ResponseEntity<Boolean> finishPatient() {
        boolean changeJob = simulator.finishPatient();
        return new ResponseEntity<>(changeJob, HttpStatus.OK);
    }

    @GetMapping("/do-paperwork")
    public ResponseEntity<Boolean> doPaperwork() {
        boolean changeJob = simulator.doPaperwork();
        return new ResponseEntity<>(changeJob, HttpStatus.OK);
    }

    @GetMapping("/start-injection")
    public ResponseEntity<Void> startInjection() {
        simulator.setInjecting(true);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stop-injection")
    public ResponseEntity<Void> stopInjection() {
        simulator.setInjecting(false);
        return ResponseEntity.ok().build();
    }

    // WebSocket endpoints

}
