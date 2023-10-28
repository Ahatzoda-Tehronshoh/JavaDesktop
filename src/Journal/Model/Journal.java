package Journal.Model;

public class Journal {
    private final int id;
    private final int idDiscipl;
    private final int typePair;
    private final String theme;
    private final String dateStr;

    public Journal(int id, int idDiscipl, int typePair, String theme, String dateStr) {
        this.id = id;
        this.idDiscipl = idDiscipl;
        this.typePair = typePair;
        this.theme = theme;
        this.dateStr = dateStr;
    }

    public int getId() {
        return id;
    }

    public int getIdDiscipl() {
        return idDiscipl;
    }

    public int getTypePair() {
        return typePair;
    }

    public String getTheme() {
        return theme;
    }

    public String getDateStr() {
        return dateStr;
    }
}
