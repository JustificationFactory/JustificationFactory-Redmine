package fr.axonic.avek.redmine.processes.transmission;

import fr.axonic.avek.engine.support.Support;

import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

public class TransmittedSupports {

    private List<Support> supports;

    @XmlElementWrapper
    public List<Support> getSupports() {
        return supports;
    }

    public void setSupports(List<Support> supports) {
        this.supports = supports;
    }
}
