package Journal.Model;

public class Disciplina {
    private final int id;
    private final String subjectName;
    private final int idGroup;
    private final int countLK;
    private final int countPZ;
    private final int countLB;
    private final String formControl;

    public Disciplina(int id, String subjectName, int idGroup, int countLK, int countPZ, int countLB, String formControl) {
        this.id = id;
        this.subjectName = subjectName;
        this.idGroup = idGroup;
        this.countLK = countLK;
        this.countPZ = countPZ;
        this.countLB = countLB;
        this.formControl = formControl;
    }

    public int getFullCount() {
        return countLK + countPZ + countLB;
    }

    public int getId() {
        return id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getIdGroup() {
        return idGroup;
    }

    public int getCountLK() {
        return countLK;
    }

    public int getCountPZ() {
        return countPZ;
    }

    public int getCountLB() {
        return countLB;
    }

    public String getFormControl() {
        return formControl;
    }
}
