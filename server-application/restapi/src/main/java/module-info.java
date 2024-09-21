module de.thk.syp.mobilenotworkgame.restapi {
    requires jakarta.validation;
    requires java.base;
    requires io.swagger.v3.oas.annotations;
    requires spring.context;
    requires spring.boot;
    requires spring.beans;
    requires spring.web;
    requires spring.boot.autoconfigure;
    requires spring.security.core;
    requires spring.security.config;
    requires spring.security.web;
    requires spring.security.oauth2.jose;
    requires spring.security.oauth2.resource.server;
    requires com.nimbusds.jose.jwt;
    requires org.apache.tomcat.embed.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires org.slf4j;
    requires jdk.httpserver;
    requires transitive de.thk.syp.mobilenotworkgame.fachlogikapi;

    uses de.thk.syp.mobilenotworkgame.fachlogikapi.IAdminService;
    uses de.thk.syp.mobilenotworkgame.fachlogikapi.IKarteService;
    uses de.thk.syp.mobilenotworkgame.fachlogikapi.IMessungService;
    uses de.thk.syp.mobilenotworkgame.fachlogikapi.ISpielerService;
    uses de.thk.syp.mobilenotworkgame.fachlogikapi.ISpielService;

    exports de.thk.syp.mobilenotworkgame.restapi;
}