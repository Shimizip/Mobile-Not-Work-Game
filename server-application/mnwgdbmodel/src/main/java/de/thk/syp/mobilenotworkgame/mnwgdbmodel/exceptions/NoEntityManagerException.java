package de.thk.syp.mobilenotworkgame.mnwgdbmodel.exceptions;

public class NoEntityManagerException extends RuntimeException{
    public NoEntityManagerException()
    {
        super("Kein EntityManager gesetzt");
    }
}
