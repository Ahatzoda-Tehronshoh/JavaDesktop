package Journal.Model;

public class Group {
    private final int id;
    private final int idDirection;
    private final int semester;
    private final String year;
    private final String dirName;

    public String getDirName() {
        return dirName;
    }

    public Group(int id, int idDirection, int semester, String year, String dirName) {
        this.id = id;
        this.idDirection = idDirection;
        this.semester = semester;
        this.year = year;
        this.dirName = dirName;
    }

    public int getId() {
        return id;
    }

    public int getIdDirection() {
        return idDirection;
    }

    public int getSemester() {
        return semester;
    }

    public String getYear() {
        return year;
    }
}
