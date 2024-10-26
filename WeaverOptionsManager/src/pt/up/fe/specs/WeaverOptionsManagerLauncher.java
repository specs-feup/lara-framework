package pt.up.fe.specs;

import org.suikasoft.jOptions.app.App;
import org.suikasoft.jOptions.app.AppKernel;
import org.suikasoft.jOptions.app.AppPersistence;
import org.suikasoft.jOptions.gui.SimpleGui;
import org.lara.interpreter.weaver.defaultweaver.DefaultWeaver;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.suikasoft.jOptions.persistence.XmlPersistence;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;


import pt.up.fe.specs.util.SpecsSystem;
import larai.LaraI;

public class WeaverOptionsManagerLauncher {
    public static void main(String[] args) {
        launchGui(new DefaultWeaver());
    }

    public static void launchGui(WeaverEngine weaverEngine) {
        SpecsSystem.programStandardInit();

        AppKernel kernel = new WeaverOptionsManagerKernel();

        StoreDefinition definition = LaraI.getStoreDefinition(weaverEngine);

        AppPersistence persistence = new XmlPersistence(definition);

        SimpleGui gui = new SimpleGui(App.newInstance(definition.getName(), definition, persistence, kernel));

        gui.execute();
    }
}
