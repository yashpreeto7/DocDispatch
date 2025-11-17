package online.ppriyanshu26.docdispatch;

public class QueryModel {
    int qid;
    boolean attended;
    String name;

    String doctor;
    String treatment;
    String remarks;

    public QueryModel(int qid, boolean attended, String name,
                      String doctor, String treatment, String remarks) {
        this.qid = qid;
        this.attended = attended;
        this.name = name;
        this.doctor = doctor;
        this.treatment = treatment;
        this.remarks = remarks;
    }
}



