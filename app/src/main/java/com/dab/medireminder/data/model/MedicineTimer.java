package com.dab.medireminder.data.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MedicineTimer {
    private String id;
    private String name;
    private String dose;
    private String timer;
    private String repeat;
    private String icon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon == null ? "" : icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<Integer> getDayOfWeek() {
        List<Integer> integers = new ArrayList<>();
        if (repeat.contains(",")) {
            String[] listRepeat = repeat.split(",");
            for (String value : listRepeat) {
                int day;
                if (value.equals("Chủ nhật")) {
                    day = 1;
                } else {
                    day = Integer.parseInt(value.replace("Thứ", "").trim());
                }
                integers.add(day);
            }
        } else if (repeat.contains("đến")) {
            String[] listRepeat = repeat.split("đến");
            int start = Integer.parseInt(listRepeat[0].replace("Thứ", "").trim());
            int end;
            if (listRepeat[1].equals("Chủ nhật")) {
                end = 7;
                integers.add(1);
                for (int i = start; i <= end; i++) {
                    integers.add(i);
                }
            } else {
                end = Integer.parseInt(listRepeat[1].replace("Thứ", "").trim());

                for (int i = start; i <= end; i++) {
                    integers.add(i);
                }
            }
        } else if (repeat.contains("Thứ")) {
            int day = Integer.parseInt(repeat.replace("Thứ", "").trim());
            integers.add(day);
        } else if (repeat.equals("Chủ nhật")) {
            integers.add(1);
        } else {
            integers.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        }
        return integers;
    }
}
