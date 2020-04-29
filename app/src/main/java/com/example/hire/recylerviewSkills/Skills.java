package com.example.hire.recylerviewSkills;

public class Skills {

    private String skill;
    private String level;

    public Skills() {
    }

    public Skills(String skill, String level) {
        this.skill = skill;
        this.level = level;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
