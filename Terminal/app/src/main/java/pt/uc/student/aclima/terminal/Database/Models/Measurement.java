package pt.uc.student.aclima.terminal.Database.Models;

/**
 * Created by aclima on 13/12/2016.
 */

public class Measurement {

    private Integer id;
    private String name;

    public Measurement(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
