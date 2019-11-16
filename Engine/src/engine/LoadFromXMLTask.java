package engine;

import generated.MagitRepository;
import javafx.concurrent.Task;

import javax.imageio.IIOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

import static engine.Xml.createobjects;
import static engine.Xml.objectsXml;

public class LoadFromXMLTask extends Task<Boolean> {

    private String path;
    Magit magit;

    public LoadFromXMLTask(String path,Magit magit) throws Exception {
        this.path=path;
        this.magit=magit;
        magit.loadFromXML(path);
    }


    @Override
    protected Boolean call() {
        return Boolean.TRUE;
    }



}
