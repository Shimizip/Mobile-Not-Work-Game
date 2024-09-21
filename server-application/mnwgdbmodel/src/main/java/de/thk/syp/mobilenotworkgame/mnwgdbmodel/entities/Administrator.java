package de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "administrator", schema = "syp", catalog = "syp-db")
@jakarta.persistence.NamedQueries({
        @jakarta.persistence.NamedQuery(name = "Administrator.findAll", query = "SELECT a FROM Administrator a"),
        @jakarta.persistence.NamedQuery(name = "Administrator.findByAid", query = "SELECT a FROM Administrator a WHERE a.aid = :aid"),
        @jakarta.persistence.NamedQuery(name = "Administrator.findByBenutzername", query = "SELECT a FROM Administrator a WHERE a.benutzername = :benutzername"),
        // @javax.persistence.NamedQuery(name = "Administrator.blockSpieler", query = "SELECT a FROM Administrator a WHERE a.benutzername = :benutzername"),
})
public class Administrator {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "aid")
    private int aid;
    @Basic
    @Column(name = "benutzername")
    private String benutzername;
    @Basic
    @Column(name = "passwort")
    private String passwort;

    public Administrator(int aid, String benutzername, String passwort){
        this.aid = aid;
        this.benutzername = benutzername;
        this.passwort = passwort;
    }
    public Administrator(){}

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public String getBenutzername() {
        return benutzername;
    }

    public void setBenutzername(String benutzername) {
        this.benutzername = benutzername;
    }

    public String getPasswort() {
        return passwort;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Administrator that = (Administrator) o;
        return aid == that.aid && Objects.equals(benutzername, that.benutzername) && Objects.equals(passwort, that.passwort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aid, benutzername, passwort);
    }
}
