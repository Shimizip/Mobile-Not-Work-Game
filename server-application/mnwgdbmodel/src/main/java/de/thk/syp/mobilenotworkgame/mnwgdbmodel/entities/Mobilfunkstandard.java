package de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.List;

@Entity
@Table(name = "mobilfunkstandard", schema = "syp", catalog = "syp-db")
@jakarta.persistence.NamedQueries({
        @jakarta.persistence.NamedQuery(name = "Mobilfunkstandard.findAll", query = "SELECT m FROM Mobilfunkstandard m"),
        @jakarta.persistence.NamedQuery(name = "Mobilfunkstandard.findByMsid", query = "SELECT m FROM Mobilfunkstandard m WHERE m.msid = :msid"),
        @jakarta.persistence.NamedQuery(name = "Mobilfunkstandard.findByBezeichnung", query = "SELECT m FROM Mobilfunkstandard m WHERE m.bezeichnung = :bezeichnung")
})
public class Mobilfunkstandard {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "msid")
    private int msid;
    @Basic
    @Column(name = "bezeichnung")
    private String bezeichnung;
    @OneToMany(mappedBy = "mobilfunkstandard")
    private List<Messung> messungList;

    public Mobilfunkstandard(int msid, String bezeichnung) {
        this.msid = msid;
        this.bezeichnung = bezeichnung;
    }
    public Mobilfunkstandard(){}
    public int getMsid() {
        return msid;
    }

    public void setMsid(int msid) {
        this.msid = msid;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public List<Messung> getMessungList() {
        return messungList;
    }

    public void setMessungList(List<Messung> messungList) {
        this.messungList = messungList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mobilfunkstandard that = (Mobilfunkstandard) o;
        return msid == that.msid && Objects.equals(bezeichnung, that.bezeichnung);
    }

    @Override
    public int hashCode() {
        return Objects.hash(msid, bezeichnung);
    }
}
