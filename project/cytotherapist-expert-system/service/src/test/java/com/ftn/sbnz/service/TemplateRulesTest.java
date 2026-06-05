package com.ftn.sbnz.service;

import org.junit.jupiter.api.Test;
import java.io.InputStream;
import java.util.List;

import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import com.ftn.sbnz.model.general.Patient;

class TemplateRulesTest {

    @Test
    public void testSimpleTemplateWithSpreadsheet(){

        InputStream template = TemplateRulesTest.class.getResourceAsStream("/z_templates/template-rules.drt");
        InputStream data = TemplateRulesTest.class.getResourceAsStream("/z_templates/template-data.xls");

        ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
        String drl = converter.compile(data, template, 3, 2);

        System.out.println(drl);

        KieSession ksession = this.createKieSessionFromDRL(drl);

        this.doTemplateTest(ksession);
    }

    private void doTemplateTest(KieSession ksession){
        Patient patient1 = new Patient();
        Patient patient2 = new Patient();
        Patient patient3 = new Patient();
        Patient patient4 = new Patient();

        patient1.setProtocol("TC");
        patient1.setCurrentCycle(1);

        
        patient2.setProtocol("CHOP");
        patient2.setCurrentCycle(1);

        
        patient3.setProtocol("CHOP");
        patient3.setCurrentCycle(2);

        
        patient4.setProtocol("FOLFOX");
        patient4.setCurrentCycle(2);

        ksession.insert(patient1);
        ksession.insert(patient2);
        ksession.insert(patient3);
        ksession.insert(patient4);

        ksession.fireAllRules();
    }

    private KieSession createKieSessionFromDRL(String drl){
        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(drl, ResourceType.DRL);

        Results results = kieHelper.verify();

        if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)){
            List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
            for (Message message : messages) {
                System.out.println("Error: "+message.getText());
            }

            throw new IllegalStateException("Compilation errors were found. Check the logs.");
        }

        return kieHelper.build().newKieSession();
    }

}
