package org.drools.persistence.kie.persistence.cdi;

import org.drools.persistence.util.PersistenceUtil;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.cdi.KBase;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.drools.persistence.util.PersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
import static org.drools.persistence.util.PersistenceUtil.createEnvironment;
import static org.junit.Assert.assertEquals;

public class CDITest {

    private HashMap<String, Object> context;
    private Environment env;

    @Before
    public void setUp() throws Exception {
        context = PersistenceUtil.setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
        env = createEnvironment(context);
    }

    @After
    public void tearDown() throws Exception {
        PersistenceUtil.tearDown(context);
    }

    @Test
    public void testCDI() {
        // DROOLS-34
        Weld w = new Weld();
        WeldContainer wc = w.initialize();

        CDIBean bean = wc.instance().select(CDIBean.class).get();
        bean.test(env);

        w.shutdown();
    }

    public static class CDIBean {
        @Inject @KBase("cdiexample")
        KieBase kBase;

        public void test(Environment env) {
            KieSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kBase, null, env);

            List<?> list = new ArrayList<Object>();

            ksession.setGlobal( "list", list );

            ksession.insert( 1 );
            ksession.insert( 2 );
            ksession.insert( 3 );

            ksession.fireAllRules();

            assertEquals( 3, list.size() );
        }
    }
}
