package de.thk.syp.mobilenotworkgame.mnwgdbmodel.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "mobilfunkanbieter", schema = "syp", catalog = "syp-db")
@jakarta.persistence.NamedQueries({
        @jakarta.persistence.NamedQuery(name = "Mobilfunkanbieter.findAll", query = "SELECT m FROM Mobilfunkanbieter m"),
        @jakarta.persistence.NamedQuery(name = "Mobilfunkanbieter.findByMfid", query = "SELECT m FROM Mobilfunkanbieter m WHERE m.mfid = :mfid"),
        @jakarta.persistence.NamedQuery(name = "Mobilfunkanbieter.findByBezeichnung", query = "SELECT m FROM Mobilfunkanbieter m WHERE m.bezeichnung = :bezeichnung")
})
public class Mobilfunkanbieter {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "mfid")
    private int mfid;
    @Basic
    @Column(name = "bezeichnung")
    private String bezeichnung;
    @OneToMany(mappedBy = "mobilfunkanbieter")
    private List<Messung> messungList;

    public Mobilfunkanbieter(int mfid, String bezeichnung) {
        this.mfid = mfid;
        this.bezeichnung = bezeichnung;
    }
    public Mobilfunkanbieter(){}

    public int getMfid() {
        return mfid;
    }

    public void setMfid(int mfid) {
        this.mfid = mfid;
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
        Mobilfunkanbieter that = (Mobilfunkanbieter) o;
        return mfid == that.mfid && Objects.equals(bezeichnung, that.bezeichnung);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mfid, bezeichnung);
    }
}
