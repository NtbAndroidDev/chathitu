package vn.hitu.ntb.chat.model.entity;

import java.util.Comparator;

public class NumberComparator implements Comparator<ReactionItem> {
    @Override
    public int compare(ReactionItem o1, ReactionItem o2) {
        return Integer.compare(o1.getNumber(), o2.getNumber());
    }
}
