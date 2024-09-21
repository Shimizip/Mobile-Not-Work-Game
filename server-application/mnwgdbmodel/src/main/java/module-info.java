module de.thk.syp.mobilenotworkgame.mnwgdbmodel {

    requires transitive jakarta.persistence;

    requires java.sql;
    requires org.hibernate.orm.core;
    requires com.h2database;
    //requires jakarta.persistence;
    requires org.slf4j;

    exports de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities;
    exports de.thk.syp.mobilenotworkgame.mnwgdbmodel.exceptions;
    exports de.thk.syp.mobilenotworkgame.mnwgdbmodel.impl;
    exports de.thk.syp.mobilenotworkgame.mnwgdbmodel.services;

    opens de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities to org.hibernate.orm.core;
}