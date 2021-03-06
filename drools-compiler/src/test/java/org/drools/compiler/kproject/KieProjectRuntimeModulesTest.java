package org.drools.compiler.kproject;

import org.drools.core.common.InternalRuleBase;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.drools.compiler.kie.builder.impl.FileKieModule;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.compiler.kie.builder.impl.ZipKieModule;
import org.kie.api.runtime.KieContainer;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class KieProjectRuntimeModulesTest extends AbstractKnowledgeTest {

    @Test
    public void createMultpleJarAndFileResources() throws IOException,
                                                  ClassNotFoundException,
                                                  InterruptedException {
        KieModuleModel kProjModel1 = createKieModule( "jar1", true );
        KieModuleModel kProjModel2 = createKieModule( "jar2", true );
        KieModuleModel kProjModel3 = createKieModule( "jar3", true );
        KieModuleModel kProjModel4 = createKieModule( "fol4", false );

        ReleaseId releaseId1 = KieServices.Factory.get().newReleaseId("jar1",
                                                                      "art1",
                                                                      "1.0-SNAPSHOT");
        ReleaseId releaseId2 = KieServices.Factory.get().newReleaseId("jar2",
                                                                       "art1",
                                                                       "1.0-SNAPSHOT");
        ReleaseId releaseId3 = KieServices.Factory.get().newReleaseId("jar3",
                                                                      "art1",
                                                                      "1.0-SNAPSHOT");
        ReleaseId releaseId4 = KieServices.Factory.get().newReleaseId("fol4",
                                                                      "art1",
                                                                      "1.0-SNAPSHOT");

        java.io.File file1 = fileManager.newFile( "jar1-1.0-SNAPSHOT.jar" );
        java.io.File file2 = fileManager.newFile( "jar2-1.0-SNAPSHOT.jar" );
        java.io.File file3 = fileManager.newFile( "jar3-1.0-SNAPSHOT.jar" );
        java.io.File fol4 = fileManager.newFile( "fol4-1.0-SNAPSHOT" );

        ZipKieModule mod1 = new ZipKieModule(releaseId1,
                                              kProjModel1,
                                              file1 );
        ZipKieModule mod2 = new ZipKieModule(releaseId2,
                                              kProjModel2,
                                              file2 );
        ZipKieModule mod3 = new ZipKieModule(releaseId3,
                                              kProjModel3,
                                              file3 );
        FileKieModule mod4 = new FileKieModule(releaseId4,
                                                kProjModel4,
                                                fol4 );

        mod1.addDependency( mod2 );
        mod1.addDependency( mod3 );
        mod1.addDependency( mod4 );

        KieModuleKieProject kProject = new KieModuleKieProject(mod1);
        
        KieContainer kContainer = new KieContainerImpl( kProject,
                                                        null );

        KieBase kBase = kContainer.getKieBase( "jar1.KBase1" );
        ClassLoader cl = ((InternalRuleBase) ((KnowledgeBaseImpl) kBase).getRuleBase()).getRootClassLoader();

        Class cls = cl.loadClass( "org.drools.compiler.cdi.test.KProjectTestClassjar1" );
        assertNotNull( cls );
        cls = cl.loadClass( "org.drools.compiler.cdi.test.KProjectTestClassjar2" );
        assertNotNull( cls );
        cls = cl.loadClass( "org.drools.compiler.cdi.test.KProjectTestClassjar3" );
        assertNotNull( cls );

        testEntry( new KProjectTestClassImpl( "jar1",
                                              kContainer ),
                   "jar1" );
        testEntry( new KProjectTestClassImpl( "jar2",
                                              kContainer ),
                   "jar2" );
        testEntry( new KProjectTestClassImpl( "jar3",
                                              kContainer ),
                   "jar3" );
        testEntry( new KProjectTestClassImpl( "fol4",
                                              kContainer ),
                   "fol4" );

    }

}
